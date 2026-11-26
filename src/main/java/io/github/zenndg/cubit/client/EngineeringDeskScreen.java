package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.CascadeMenuPart;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

/**
 * The screen of the Engineering Desk, a designer's board with the Project, Storage, Disassemble and
 * Cascade tabs (answers 1467, 1473, 1524, 2017 and 2535). It only shows and asks: the server
 * decides (rule 19).
 */
final class EngineeringDeskScreen extends AbstractContainerScreen<EngineeringDeskMenu> {
  static final int HEIGHT = 258;
  private static final int MARGIN = 8;
  private static final int LABEL_Y = 20;
  private static final int INVENTORY_LABEL_Y = 164;
  private static final int SEARCH_HEIGHT = 12;
  private static final int SEARCH_INSET = 2;
  private static final int SEARCH_MAX = 32;
  private static final String NO_SEARCH = "";

  private final DeskCatalog catalog;
  private final DeskCatalog cascadeCatalog;
  private final ProjectTab project;
  private final CascadeTab cascade;
  private final DisassembleTab disassemble;
  private DeskTab tab = DeskTab.PROJECT;
  private EditBox search;

  EngineeringDeskScreen(EngineeringDeskMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    CascadeCatalogRules rules = new CascadeCatalogRules(menu);
    catalog = new DeskCatalog(new ProjectCatalogRules(menu));
    cascadeCatalog = new DeskCatalog(rules);
    project = new ProjectTab(menu, catalog, this::send, this::reveal);
    cascade = new CascadeTab(menu, cascadeCatalog, rules, this::send);
    disassemble = new DisassembleTab(menu, this::send);
    imageWidth = EngineeringDeskMenu.WIDTH;
    imageHeight = HEIGHT;
    inventoryLabelX = MARGIN;
    inventoryLabelY = INVENTORY_LABEL_Y;
  }

  @Override
  protected void init() {
    super.init();
    project.init(leftPos, topPos, this::addRenderableWidget);
    cascade.init(leftPos, topPos, this::addRenderableWidget);
    disassemble.init(leftPos, topPos, this::addRenderableWidget);
    search = searchBox();
    catalog.showCategoryOf(menu.recipeIndex());
    showTab();
  }

  /** The search box: the hint shows while it is empty, and each letter filters the catalog. */
  private EditBox searchBox() {
    EditBox box =
        new EditBox(
            font,
            leftPos + DeskCatalog.X + SEARCH_INSET,
            topPos + DeskCatalog.SEARCH_Y + SEARCH_INSET,
            DeskCatalog.WIDTH - SEARCH_INSET * 2,
            SEARCH_HEIGHT - SEARCH_INSET,
            Component.translatable(DeskScreenKeys.CATALOG));
    box.setBordered(false);
    box.setMaxLength(SEARCH_MAX);
    box.setTextColor(BlueprintPainter.TEXT);
    box.setHint(
        Component.translatable(DeskScreenKeys.SEARCH).withColor(BlueprintPainter.TEXT_SOFT));
    box.setResponder(typed -> {
      catalog.search(typed);
      cascadeCatalog.search(typed);
    });
    return addRenderableWidget(box);
  }

  private void send(int id) {
    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
  }

  /** Out of a search, so the category of the recipe shows (answer 2477). */
  private void reveal(int place) {
    search.setValue(NO_SEARCH);
    catalog.reveal(place);
  }

  private boolean storageOpen() {
    return tab == DeskTab.STORAGE;
  }

  /** The Project tab and the first step of the Cascade have the catalog. */
  private boolean showsCatalog() {
    return tab == DeskTab.PROJECT || tab == DeskTab.CASCADE && cascade.choosing();
  }

  private DeskCatalog openCatalog() {
    return tab == DeskTab.CASCADE ? cascadeCatalog : catalog;
  }

  private void showTab() {
    menu.showTab(storageOpen(), tab == DeskTab.DISASSEMBLE);
    project.show(tab == DeskTab.PROJECT);
    cascade.show(tab == DeskTab.CASCADE);
    disassemble.show(tab == DeskTab.DISASSEMBLE);
    showSearch();
  }

  private void showSearch() {
    search.visible = showsCatalog();
    if (!search.visible) {
      search.setFocused(false);
    }
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    project.update();
    boolean asking = cascade.question().isOpen();
    super.render(graphics, mouseX, mouseY, partialTick);
    if (asking) {
      cascade.question().render(graphics, font, leftPos, topPos, mouseX, mouseY);
      return;
    }
    renderTooltip(graphics, mouseX, mouseY);
    if (showsCatalog()) {
      DeskCatalog open = openCatalog();
      open.at(mouseX, mouseY, leftPos, topPos)
          .ifPresent(index -> open.tooltip(graphics, font, index, mouseX, mouseY));
      open.categoryTooltip(graphics, font, leftPos, topPos, mouseX, mouseY);
    }
    if (tab == DeskTab.PROJECT) {
      project.tooltips(graphics, font, mouseX, mouseY);
    } else if (tab == DeskTab.DISASSEMBLE) {
      disassemble.tooltips(graphics, font, mouseX, mouseY, leftPos, topPos);
    }
    List<Component> lines = hint(mouseX, mouseY);
    if (!lines.isEmpty()) {
      graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }
  }

  /** What the Cascade tab and the tabs of the strip say under the mouse. */
  private List<Component> hint(int mouseX, int mouseY) {
    DeskTab over = tabAt(mouseX, mouseY);
    if (over == DeskTab.CASCADE) {
      return List.of(cascade.name());
    }
    if (over == DeskTab.DISASSEMBLE && !menu.cascade().locks()) {
      return List.of(disassemblyName());
    }
    if (over != null && over.isLocked(menu.cascade(), menu.disassembly())) {
      return List.of(Component.translatable(DeskScreenKeys.CASCADE_LOCKS));
    }
    return tab == DeskTab.CASCADE ? cascade.hint(mouseX - leftPos, mouseY - topPos) : List.of();
  }

  private Component disassemblyName() {
    return Component.translatable(
        menu.disassembly().allowed()
            ? DeskScreenKeys.DISASSEMBLE
            : DeskScreenKeys.DISASSEMBLE_LOCKED);
  }

  private DeskTab tabAt(double mouseX, double mouseY) {
    return DeskTab.at(mouseX - (leftPos + imageWidth - ScreenTabStrip.INSET), mouseY - topPos);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    int tabsLeft = leftPos + imageWidth - ScreenTabStrip.INSET;
    int openTop = tab.top();
    ScreenTabStrip.renderBehind(graphics, tabsLeft, topPos, DeskTab.TOPS, openTop);
    BlueprintPainter.sheet(graphics, leftPos, topPos, imageWidth, imageHeight);
    ScreenTabStrip.renderOpen(graphics, tabsLeft, topPos, openTop);
    DeskTab.renderIcons(graphics, tabsLeft, topPos, menu.cascade(), menu.disassembly());
    DeskScreenFrames.player(graphics, leftPos, topPos);
    if (storageOpen()) {
      DeskScreenFrames.storage(graphics, leftPos, topPos, menu.slots());
      DeskScreenFrames.storageArrow(graphics, leftPos, topPos);
    } else {
      renderBoard(graphics);
    }
  }

  private void renderBoard(GuiGraphics graphics) {
    if (showsCatalog()) {
      renderSearchFrame(graphics);
      openCatalog().render(graphics, leftPos, topPos);
    }
    if (tab == DeskTab.PROJECT) {
      project.render(graphics);
    } else if (tab == DeskTab.DISASSEMBLE) {
      disassemble.render(graphics, leftPos, topPos);
    }
    DeskEnergyBar.render(graphics, menu, leftPos, topPos);
  }

  private void renderSearchFrame(GuiGraphics graphics) {
    int x = leftPos + DeskCatalog.X;
    int y = topPos + DeskCatalog.SEARCH_Y;
    graphics.fill(x, y, x + DeskCatalog.WIDTH, y + SEARCH_HEIGHT, BlueprintPainter.CELL);
    graphics.renderOutline(x, y, DeskCatalog.WIDTH, SEARCH_HEIGHT, BlueprintPainter.LINE_SOFT);
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    DeskScreenHeader.render(graphics, font, title, menu.tier(), menu.status(), imageWidth);
    graphics.drawString(
        font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, BlueprintPainter.TEXT, false);
    if (storageOpen()) {
      DeskScreenFrames.storageLabels(graphics, font, LABEL_Y);
      return;
    }
    DeskEnergyBar.renderLabels(graphics, font, menu);
    if (tab == DeskTab.PROJECT) {
      project.renderLabels(graphics, font);
    } else if (tab == DeskTab.DISASSEMBLE) {
      disassemble.renderLabels(graphics, font);
    } else {
      cascade.renderLabels(graphics, font);
    }
  }

  /**
   * The question takes every key, and Esc answers no. While the search box has the focus, the keys
   * write in it, and only Esc closes the screen.
   */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (cascade.question().isOpen()) {
      cascade.question().ask(keyCode != GLFW.GLFW_KEY_ESCAPE);
      return true;
    }
    if (search.isFocused() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
      search.keyPressed(keyCode, scanCode, modifiers);
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (cascade.question().isOpen()) {
      cascade.question().click(mouseX, mouseY, button);
      return true;
    }
    DeskTab clicked = tabAt(mouseX, mouseY);
    if (clicked != null) {
      open(clicked);
      return true;
    }
    if (showsCatalog() && clickedCatalog(mouseX, mouseY)
        || tab == DeskTab.PROJECT && project.click(mouseX, mouseY)) {
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  /** Under the question, a release never drops nor places the carried item. */
  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (cascade.question().isOpen()) {
      setDragging(false);
      return true;
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  /** The tabs are part of the window, so a click there never drops the carried item (#255). */
  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
    return tabAt(mouseX, mouseY) == null
        && super.hasClickedOutside(mouseX, mouseY, left, top, button);
  }

  /** A plan on its way keeps the Cascade tab open, and a tier without a tab never opens it. */
  private void open(DeskTab wanted) {
    if (menu.cascade().locks()) {
      tab = DeskTab.CASCADE;
    } else if (opens(wanted)) {
      tab = wanted;
    }
    showTab();
  }

  private boolean opens(DeskTab wanted) {
    return switch (wanted) {
      case CASCADE -> menu.cascade().allowed();
      case DISASSEMBLE -> menu.disassembly().allowed();
      default -> true;
    };
  }

  @Override
  protected void containerTick() {
    super.containerTick();
    if (menu.cascade().locks() && tab != DeskTab.CASCADE) {
      open(DeskTab.CASCADE);
    }
    cascade.tick();
    disassemble.tick();
    project.tick();
    showSearch();
  }

  private boolean clickedCatalog(double mouseX, double mouseY) {
    if (openCatalog().clickCategory(mouseX, mouseY, leftPos, topPos)) {
      return true;
    }
    var recipe = openCatalog().at(mouseX, mouseY, leftPos, topPos);
    boolean inCascade = tab == DeskTab.CASCADE;
    if (recipe.isPresent() && (inCascade || !menu.holdsRecipe())) {
      send(inCascade ? CascadeMenuPart.choose(recipe.get()) : DeskControl.choose(recipe.get()));
    }
    return recipe.isPresent();
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (cascade.question().isOpen()) {
      return true;
    }
    if (showsCatalog() && openCatalog().isOver(mouseX, mouseY, leftPos, topPos)) {
      openCatalog().scroll(scrollY);
      return true;
    }
    return tab == DeskTab.CASCADE && cascade.scroll(mouseX - leftPos, mouseY - topPos, scrollY)
        || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }
}
