package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.DeskGrid;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
  private static final int PROJECT_Y = 9;
  private static final int STORAGE_Y = 37;
  private static final int[] TAB_TOPS = {PROJECT_Y, STORAGE_Y};
  private static final int ITEM_SHIFT = -2;
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
  private static final int MINUS_X = 96;
  private static final int PLUS_X = 136;
  private static final int CANCEL_X = 96;
  private static final int CANCEL_WIDTH = 54;
  private static final int ORDER_TEXT_X = 123;
  private static final int ENERGY_LABEL_Y = 128;
  private static final int BAR_Y = 139;
  private static final int BAR_WIDTH = 204;
  private static final int BAR_HEIGHT = 8;
  private static final int INVENTORY_LABEL_Y = 164;
  private static final int SEARCH_HEIGHT = 12;
  private static final int SEARCH_INSET = 2;
  private static final int SEARCH_MAX = 32;
  private static final int PERCENT = 100;
  private static final String MINUS_LABEL = "-";
  private static final String PLUS_LABEL = "+";

  private final DeskCatalog catalog = new DeskCatalog();
  private final DeskGridView grid = new DeskGridView();
  private boolean storageOpen;
  private EditBox search;
  private BlueprintButton minus;
  private BlueprintButton plus;
  private BlueprintButton cancel;

  EngineeringDeskScreen(EngineeringDeskMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
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
    box.setResponder(catalog::search);
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

  private void showTab() {
    menu.showStorage(storageOpen);
    minus.visible = !storageOpen;
    plus.visible = !storageOpen;
    cancel.visible = !storageOpen;
    search.visible = !storageOpen;
    if (storageOpen) {
      search.setFocused(false);
    }
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    renderTooltip(graphics, mouseX, mouseY);
    if (!storageOpen) {
      catalog
          .at(mouseX, mouseY, leftPos, topPos)
          .ifPresent(index -> catalog.tooltip(graphics, font, index, mouseX, mouseY));
      catalog.categoryTooltip(graphics, font, leftPos, topPos, mouseX, mouseY);
    }
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    int tabsLeft = leftPos + imageWidth - ScreenTabStrip.INSET;
    int openTop = storageOpen ? STORAGE_Y : PROJECT_Y;
    ScreenTabStrip.renderBehind(graphics, tabsLeft, topPos, TAB_TOPS, openTop);
    BlueprintPainter.sheet(graphics, leftPos, topPos, imageWidth, imageHeight);
    ScreenTabStrip.renderOpen(graphics, tabsLeft, topPos, openTop);
    renderTabIcons(graphics, tabsLeft);
    DeskScreenFrames.player(graphics, leftPos, topPos);
    if (storageOpen) {
      DeskScreenFrames.storage(graphics, leftPos, topPos, menu.slots());
      DeskScreenFrames.storageArrow(graphics, leftPos, topPos);
    } else {
      renderProject(graphics);
    }
  }

  private void renderTabIcons(GuiGraphics graphics, int tabsLeft) {
    int x = tabsLeft + ScreenTabStrip.ICON_X + ITEM_SHIFT;
    int y = topPos + ScreenTabStrip.ICON_Y + ITEM_SHIFT;
    graphics.renderItem(new ItemStack(CubitItems.ENGINEERING_DESK.get()), x, y + PROJECT_Y);
    graphics.renderItem(new ItemStack(Items.CHEST), x, y + STORAGE_Y);
  }

  private void renderProject(GuiGraphics graphics) {
    renderSearchFrame(graphics);
    catalog.render(graphics, leftPos, topPos, menu.recipeIndex());
    renderGrid(graphics);
    renderEnergy(graphics);
  }

  private void renderSearchFrame(GuiGraphics graphics) {
    int x = leftPos + DeskCatalog.X;
    int y = topPos + DeskCatalog.SEARCH_Y;
    graphics.fill(x, y, x + DeskCatalog.WIDTH, y + SEARCH_HEIGHT, BlueprintPainter.CELL);
    graphics.renderOutline(x, y, DeskCatalog.WIDTH, SEARCH_HEIGHT, BlueprintPainter.LINE_SOFT);
  }

  /** The grid of marks in a dashed frame, the arrow of the progress and the item it makes. */
  private void renderGrid(GuiGraphics graphics) {
    int gridSide = DeskGrid.SIDE * ScreenPainter.SLOT_SIZE + GRID_FRAME * 2;
    BlueprintPainter.dashed(
        graphics,
        leftPos + DeskGridView.X - GRID_FRAME,
        topPos + DeskGridView.Y - GRID_FRAME,
        gridSide,
        gridSide);
    long tick = minecraft.player == null ? 0 : minecraft.player.tickCount;
    grid.render(graphics, leftPos, topPos, chosenMarks(), menu.marks(), tick);
    BlueprintPainter.bar(
        graphics,
        new ScreenPainter.Box(leftPos + ARROW_X, topPos + ARROW_Y, ARROW_WIDTH, ARROW_HEIGHT),
        menu.progressPercent(),
        PERCENT,
        BlueprintPainter.CRAFTING);
    BlueprintPainter.slot(graphics, leftPos + RESULT_X, topPos + RESULT_Y);
    graphics.renderFakeItem(chosenResult(), leftPos + RESULT_X + 1, topPos + RESULT_Y + 1);
  }

  private void renderEnergy(GuiGraphics graphics) {
    BlueprintPainter.bar(
        graphics,
        new ScreenPainter.Box(leftPos + MARGIN, topPos + BAR_Y, BAR_WIDTH, BAR_HEIGHT),
        menu.stock().amount(),
        Math.max(1, menu.capacity().amount()),
        BlueprintPainter.ENERGY);
    BlueprintPainter.slot(
        graphics,
        leftPos + EngineeringDeskMenu.ENERGY_SLOT_X - 1,
        topPos + EngineeringDeskMenu.ENERGY_SLOT_Y - 1);
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
    if (storageOpen) {
      DeskScreenFrames.storageLabels(graphics, font, LABEL_Y);
    } else {
      renderProjectLabels(graphics);
    }
  }

  private void renderProjectLabels(GuiGraphics graphics) {
    Component order = Component.translatable(DeskScreenKeys.ORDER, menu.done(), menu.total());
    graphics.drawCenteredString(font, order, ORDER_TEXT_X, ORDER_Y + 2, BlueprintPainter.TEXT);
    Component label = Component.translatable(DeskScreenKeys.STORED_ENERGY);
    graphics.drawString(font, label, MARGIN, ENERGY_LABEL_Y, BlueprintPainter.TEXT, false);
    Component amount =
        Component.translatable(
            ScreenKeys.ENERGY_AMOUNT,
            EnergyFormat.compact(menu.stock()),
            EnergyFormat.compact(menu.capacity()));
    int amountX = MARGIN + BAR_WIDTH - font.width(amount);
    graphics.drawString(font, amount, amountX, ENERGY_LABEL_Y, BlueprintPainter.TEXT_SOFT, false);
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
    if (ScreenTabStrip.contains(tabX, tabY, PROJECT_Y)
        || ScreenTabStrip.contains(tabX, tabY, STORAGE_Y)) {
      storageOpen = ScreenTabStrip.contains(tabX, tabY, STORAGE_Y);
      showTab();
      return true;
    }
    if (!storageOpen && clickedProject(mouseX, mouseY)) {
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  private boolean clickedProject(double mouseX, double mouseY) {
    if (catalog.clickCategory(mouseX, mouseY, leftPos, topPos)) {
      return true;
    }
    var recipe = catalog.at(mouseX, mouseY, leftPos, topPos);
    if (recipe.isPresent()) {
      if (DeskCatalog.unlocked(catalog.recipes().get(recipe.get()))) {
        send(DeskControl.choose(recipe.get()));
      }
      return true;
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
    if (!storageOpen && catalog.isOver(mouseX, mouseY, leftPos, topPos)) {
      catalog.scroll(scrollY);
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }
}
