package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.DeskGrid;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.lwjgl.glfw.GLFW;

/**
 * The screen of the Engineering Desk, a designer's board with the Project and Storage tabs (answers
 * 1467, 1473 and 1524). It only shows and asks: the server decides (rule 19).
 */
final class EngineeringDeskScreen extends AbstractContainerScreen<EngineeringDeskMenu> {
  private static final int HEIGHT = 258;
  private static final int MARGIN = 8;
  private static final int LABEL_Y = 20;
  private static final int GRID_FRAME = 3;
  private static final int ARROW_X = 156;
  private static final int ARROW_Y = 50;
  private static final int ARROW_WIDTH = 22;
  private static final int ARROW_HEIGHT = 6;
  private static final int RESULT_X = 184;
  private static final int RESULT_Y = 44;
  private static final int ORDER_Y = 92;
  private static final int CANCEL_Y = 108;
  private static final int BUTTON_HEIGHT = 12;
  private static final int SMALL_BUTTON = 14;
  private static final int ORDER_GAP = 2;

  /** The dashed frame of the grid, which the row of the order follows (answer 2040). */
  private static final int FRAME_X = DeskGridView.X - GRID_FRAME;

  private static final int FRAME_WIDTH = DeskGrid.SIDE * ScreenPainter.SLOT_SIZE + GRID_FRAME * 2;
  private static final int MINUS_X = FRAME_X;
  private static final int PLUS_X = FRAME_X + FRAME_WIDTH - SMALL_BUTTON;
  private static final int CANCEL_X = FRAME_X;
  private static final int CANCEL_WIDTH = FRAME_WIDTH;
  private static final int ORDER_TEXT_X = FRAME_X + FRAME_WIDTH / 2;
  private static final int ORDER_ROOM = FRAME_WIDTH - SMALL_BUTTON * 2 - ORDER_GAP * 2;
  private static final int INVENTORY_LABEL_Y = 164;
  private static final int SEARCH_HEIGHT = 12;
  private static final int SEARCH_INSET = 2;
  private static final int SEARCH_MAX = 32;
  private static final int PERCENT = 100;
  private static final String MINUS_LABEL = "-";
  private static final String PLUS_LABEL = "+";

  private final DeskCatalog catalog;
  private final DeskCatalog cascadeCatalog;
  private final CascadeTab cascade;
  private final DeskGridView grid = new DeskGridView();
  private final DeskOrderView orderView;
  private DeskTab tab = DeskTab.PROJECT;
  private EditBox search;
  private BlueprintButton minus;
  private BlueprintButton plus;
  private BlueprintButton cancel;

  EngineeringDeskScreen(EngineeringDeskMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    CascadeCatalogRules rules = new CascadeCatalogRules(menu);
    catalog = new DeskCatalog(new ProjectCatalogRules(menu));
    cascadeCatalog = new DeskCatalog(rules);
    orderView = new DeskOrderView(menu, catalog);
    cascade = new CascadeTab(menu, cascadeCatalog, rules, this::send);
    imageWidth = EngineeringDeskMenu.WIDTH;
    imageHeight = HEIGHT;
    inventoryLabelX = MARGIN;
    inventoryLabelY = INVENTORY_LABEL_Y;
  }

  @Override
  protected void init() {
    super.init();
    minus =
        button(MINUS_X, ORDER_Y, SMALL_BUTTON, Component.literal(MINUS_LABEL), () -> order(false));
    plus = button(PLUS_X, ORDER_Y, SMALL_BUTTON, Component.literal(PLUS_LABEL), () -> order(true));
    cancel =
        addRenderableWidget(
            new BlueprintButton(
                leftPos + CANCEL_X,
                topPos + CANCEL_Y,
                CANCEL_WIDTH,
                BUTTON_HEIGHT,
                Component.translatable(DeskScreenKeys.CANCEL),
                BlueprintPainter.CANCEL,
                () -> send(DeskControl.CANCEL)));
    cascade.init(leftPos, topPos, this::addRenderableWidget);
    search = searchBox();
    catalog.showCategoryOf(menu.recipeIndex());
    showTab();
  }

  private BlueprintButton button(int x, int y, int width, Component label, Runnable action) {
    return addRenderableWidget(
        new BlueprintButton(
            leftPos + x, topPos + y, width, BUTTON_HEIGHT, label, BlueprintPainter.CELL, action));
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

  private void order(boolean up) {
    boolean shift = hasShiftDown();
    send(
        up
            ? (shift ? DeskControl.PLUS_SHIFT : DeskControl.PLUS)
            : (shift ? DeskControl.MINUS_SHIFT : DeskControl.MINUS));
  }

  private void send(int id) {
    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
  }

  private boolean storageOpen() {
    return tab == DeskTab.STORAGE;
  }

  private DeskCatalog openCatalog() {
    return tab == DeskTab.CASCADE ? cascadeCatalog : catalog;
  }

  private void showTab() {
    menu.showStorage(storageOpen());
    minus.visible = tab == DeskTab.PROJECT;
    plus.visible = tab == DeskTab.PROJECT;
    cancel.visible = tab == DeskTab.PROJECT;
    cascade.show(tab == DeskTab.CASCADE);
    search.visible = !storageOpen();
    if (storageOpen()) {
      search.setFocused(false);
    }
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    plus.active = orderView.mayRaise();
    super.render(graphics, mouseX, mouseY, partialTick);
    renderTooltip(graphics, mouseX, mouseY);
    if (!storageOpen()) {
      DeskCatalog open = openCatalog();
      open.at(mouseX, mouseY, leftPos, topPos)
          .ifPresent(index -> open.tooltip(graphics, font, index, mouseX, mouseY));
      open.categoryTooltip(graphics, font, leftPos, topPos, mouseX, mouseY);
    }
    if (tab == DeskTab.PROJECT) {
      recipeTooltip(graphics, mouseX, mouseY);
      plusTooltip(graphics, mouseX, mouseY);
    }
    cascade.tooltips(graphics, font, mouseX, mouseY);
    if (overCascadeTab(mouseX, mouseY)) {
      graphics.renderTooltip(font, cascade.name(), mouseX, mouseY);
    }
  }

  /**
   * The name of an item of the recipe, in the grid or in the result, as in a slot (answer 1548).
   */
  private void recipeTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    ItemStack item =
        grid.at(mouseX, mouseY, leftPos, topPos)
            .map(cell -> DeskGridView.markAt(cell, chosenMarks(), menu.marks(), playerTicks()))
            .orElseGet(() -> overResult(mouseX, mouseY) ? chosenResult() : ItemStack.EMPTY);
    if (!item.isEmpty()) {
      graphics.renderTooltip(font, item, mouseX, mouseY);
    }
  }

  /** Why the plus is off, one reason a line (answer 1988). */
  private void plusTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    List<Component> reasons = plus.isHovered() && !plus.active ? orderView.reasons() : List.of();
    if (!reasons.isEmpty()) {
      graphics.renderComponentTooltip(font, reasons, mouseX, mouseY);
    }
  }

  private boolean overResult(int mouseX, int mouseY) {
    int x = leftPos + RESULT_X;
    int y = topPos + RESULT_Y;
    return mouseX >= x
        && mouseX < x + ScreenPainter.SLOT_SIZE
        && mouseY >= y
        && mouseY < y + ScreenPainter.SLOT_SIZE;
  }

  private long playerTicks() {
    return minecraft.player == null ? 0 : minecraft.player.tickCount;
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    int tabsLeft = leftPos + imageWidth - ScreenTabStrip.INSET;
    int openTop = tab.top();
    ScreenTabStrip.renderBehind(graphics, tabsLeft, topPos, DeskTab.TOPS, openTop);
    BlueprintPainter.sheet(graphics, leftPos, topPos, imageWidth, imageHeight);
    ScreenTabStrip.renderOpen(graphics, tabsLeft, topPos, openTop);
    DeskTab.renderIcons(graphics, tabsLeft, topPos);
    DeskScreenFrames.player(graphics, leftPos, topPos);
    if (storageOpen()) {
      DeskScreenFrames.storage(graphics, leftPos, topPos, menu.slots());
      DeskScreenFrames.storageArrow(graphics, leftPos, topPos);
    } else {
      renderProject(graphics);
    }
  }

  private void renderProject(GuiGraphics graphics) {
    renderSearchFrame(graphics);
    openCatalog().render(graphics, leftPos, topPos);
    if (tab == DeskTab.PROJECT) {
      renderGrid(graphics);
    }
    DeskEnergyBar.render(graphics, menu, leftPos, topPos);
  }

  private void renderSearchFrame(GuiGraphics graphics) {
    int x = leftPos + DeskCatalog.X;
    int y = topPos + DeskCatalog.SEARCH_Y;
    graphics.fill(x, y, x + DeskCatalog.WIDTH, y + SEARCH_HEIGHT, BlueprintPainter.CELL);
    graphics.renderOutline(x, y, DeskCatalog.WIDTH, SEARCH_HEIGHT, BlueprintPainter.LINE_SOFT);
  }

  /** The grid of marks in a dashed frame, the arrow of the progress and the item it makes. */
  private void renderGrid(GuiGraphics graphics) {
    BlueprintPainter.dashed(
        graphics,
        leftPos + FRAME_X,
        topPos + DeskGridView.Y - GRID_FRAME,
        FRAME_WIDTH,
        FRAME_WIDTH);
    grid.render(
        graphics,
        leftPos,
        topPos,
        chosenMarks(),
        menu.marks(),
        orderView.shortage(),
        playerTicks());
    BlueprintPainter.bar(
        graphics,
        new ScreenPainter.Box(leftPos + ARROW_X, topPos + ARROW_Y, ARROW_WIDTH, ARROW_HEIGHT),
        menu.progressPercent(),
        PERCENT,
        BlueprintPainter.CRAFTING);
    BlueprintPainter.slot(graphics, leftPos + RESULT_X, topPos + RESULT_Y);
    graphics.renderFakeItem(chosenResult(), leftPos + RESULT_X + 1, topPos + RESULT_Y + 1);
  }

  private List<Ingredient> chosenMarks() {
    var all = catalog.recipes();
    int index = menu.recipeIndex();
    return index < 0 || index >= all.size() ? List.of() : DeskGrid.of(all.get(index).value());
  }

  private ItemStack chosenResult() {
    var all = catalog.recipes();
    int index = menu.recipeIndex();
    return index < 0 || index >= all.size()
        ? ItemStack.EMPTY
        : DeskCatalog.resultOf(all.get(index));
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
      renderOrderLabel(graphics);
    } else {
      cascade.renderLabels(graphics, font);
    }
  }

  private void renderOrderLabel(GuiGraphics graphics) {
    Component order = Component.translatable(DeskScreenKeys.ORDER, menu.done(), menu.total());
    BlueprintPainter.fitted(
        graphics, font, order, ORDER_TEXT_X, ORDER_Y + 2, ORDER_ROOM, BlueprintPainter.TEXT);
  }

  /** While the search box has the focus, the keys write in it, and only Esc closes the screen. */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (search.isFocused() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
      search.keyPressed(keyCode, scanCode, modifiers);
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    double tabX = mouseX - (leftPos + imageWidth - ScreenTabStrip.INSET);
    double tabY = mouseY - topPos;
    DeskTab clicked = DeskTab.at(tabX, tabY);
    if (clicked != null) {
      open(clicked);
      return true;
    }
    if (!storageOpen() && clickedProject(mouseX, mouseY)) {
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  private boolean overCascadeTab(int mouseX, int mouseY) {
    return DeskTab.at(mouseX - (leftPos + imageWidth - ScreenTabStrip.INSET), mouseY - topPos)
        == DeskTab.CASCADE;
  }

  /** A plan on its way keeps the Cascade tab open, and a tier without it never opens it. */
  private void open(DeskTab wanted) {
    if (menu.cascade().locks()) {
      tab = DeskTab.CASCADE;
    } else if (wanted != DeskTab.CASCADE || menu.cascade().allowed()) {
      tab = wanted;
    }
    showTab();
  }

  @Override
  protected void containerTick() {
    super.containerTick();
    if (menu.cascade().locks() && tab != DeskTab.CASCADE) {
      open(DeskTab.CASCADE);
    }
    cascade.tick();
  }

  private boolean clickedProject(double mouseX, double mouseY) {
    if (openCatalog().clickCategory(mouseX, mouseY, leftPos, topPos)) {
      return true;
    }
    var recipe = openCatalog().at(mouseX, mouseY, leftPos, topPos);
    if (recipe.isPresent()) {
      boolean inCascade = tab == DeskTab.CASCADE;
      send(inCascade ? CascadeMenuPart.choose(recipe.get()) : DeskControl.choose(recipe.get()));
      return true;
    }
    if (tab == DeskTab.CASCADE) {
      return false;
    }
    var cell = grid.at(mouseX, mouseY, leftPos, topPos);
    if (cell.isPresent()) {
      menu.marks().set(cell.get(), menu.getCarried().copyWithCount(1));
      send(DeskControl.mark(cell.get()));
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (!storageOpen() && openCatalog().isOver(mouseX, mouseY, leftPos, topPos)) {
      openCatalog().scroll(scrollY);
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }
}
