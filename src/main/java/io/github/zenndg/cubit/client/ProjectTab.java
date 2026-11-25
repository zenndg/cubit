package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.DeskGrid;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** The right side of the Project tab: the grid of marks, the item it makes and the order (1473). */
final class ProjectTab {
  private static final int GRID_FRAME = 3;
  private static final int ARROW_X = 166;
  private static final int ARROW_Y = 50;
  private static final int ARROW_WIDTH = 22;
  private static final int ARROW_HEIGHT = 6;
  private static final int RESULT_X = 194;
  private static final int RESULT_Y = 44;
  private static final int ORDER_Y = 92;
  private static final int CANCEL_Y = 108;
  private static final int BUTTON_HEIGHT = 12;
  private static final int SMALL_BUTTON = 14;
  private static final int ORDER_GAP = 2;

  /** The dashed frame of the grid, which the row of the order follows (answer 2040). */
  private static final int FRAME_X = DeskGridView.X - GRID_FRAME;

  private static final int FRAME_WIDTH = DeskGrid.SIDE * ScreenPainter.SLOT_SIZE + GRID_FRAME * 2;
  private static final int PLUS_X = FRAME_X + FRAME_WIDTH - SMALL_BUTTON;
  private static final int ORDER_TEXT_X = FRAME_X + FRAME_WIDTH / 2;
  private static final int ORDER_ROOM = FRAME_WIDTH - SMALL_BUTTON * 2 - ORDER_GAP * 2;
  private static final int PERCENT = 100;
  private static final String MINUS_LABEL = "-";
  private static final String PLUS_LABEL = "+";

  private final EngineeringDeskMenu menu;
  private final DeskCatalog catalog;
  private final DeskOrderView orderView;
  private final IntConsumer send;
  private final DeskGridView grid = new DeskGridView();
  private int left;
  private int top;
  private BlueprintButton minus;
  private BlueprintButton plus;
  private BlueprintButton cancel;

  ProjectTab(EngineeringDeskMenu menu, DeskCatalog catalog, IntConsumer send) {
    this.menu = menu;
    this.catalog = catalog;
    this.send = send;
    this.orderView = new DeskOrderView(menu, catalog);
  }

  void init(int newLeft, int newTop, Consumer<BlueprintButton> add) {
    left = newLeft;
    top = newTop;
    Component minusLabel = Component.literal(MINUS_LABEL);
    Component plusLabel = Component.literal(PLUS_LABEL);
    minus = small(FRAME_X, minusLabel, () -> order(false));
    plus = small(PLUS_X, plusLabel, () -> order(true));
    cancel =
        new BlueprintButton(
            left + FRAME_X,
            top + CANCEL_Y,
            FRAME_WIDTH,
            BUTTON_HEIGHT,
            Component.translatable(DeskScreenKeys.CANCEL),
            BlueprintPainter.CANCEL,
            () -> send.accept(DeskControl.CANCEL));
    List.of(minus, plus, cancel).forEach(add);
  }

  private BlueprintButton small(int x, Component label, Runnable action) {
    return new BlueprintButton(
        left + x, top + ORDER_Y, SMALL_BUTTON, BUTTON_HEIGHT, label, BlueprintPainter.CELL, action);
  }

  private void order(boolean up) {
    boolean shift = Screen.hasShiftDown();
    int plusId = shift ? DeskControl.PLUS_SHIFT : DeskControl.PLUS;
    int minusId = shift ? DeskControl.MINUS_SHIFT : DeskControl.MINUS;
    send.accept(up ? plusId : minusId);
  }

  void show(boolean shown) {
    minus.visible = shown;
    plus.visible = shown;
    cancel.visible = shown;
  }

  /** Before the buttons are drawn, whether the plus is off (answer 1988). */
  void update() {
    plus.active = orderView.mayRaise();
  }

  /** The grid of marks in a dashed frame, the arrow of the progress and the item it makes. */
  void render(GuiGraphics graphics) {
    BlueprintPainter.dashed(
        graphics, left + FRAME_X, top + DeskGridView.Y - GRID_FRAME, FRAME_WIDTH, FRAME_WIDTH);
    grid.render(
        graphics, left, top, chosenMarks(), menu.marks(), orderView.shortage(), playerTicks());
    BlueprintPainter.bar(
        graphics,
        new ScreenPainter.Box(left + ARROW_X, top + ARROW_Y, ARROW_WIDTH, ARROW_HEIGHT),
        menu.progressPercent(),
        PERCENT,
        BlueprintPainter.CRAFTING);
    BlueprintPainter.slot(graphics, left + RESULT_X, top + RESULT_Y);
    graphics.renderFakeItem(chosenResult(), left + RESULT_X + 1, top + RESULT_Y + 1);
  }

  void renderLabels(GuiGraphics graphics, Font font) {
    Component order = Component.translatable(DeskScreenKeys.ORDER, menu.done(), menu.total());
    BlueprintPainter.fitted(
        graphics, font, order, ORDER_TEXT_X, ORDER_Y + 2, ORDER_ROOM, BlueprintPainter.TEXT);
  }

  void tooltips(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    recipeTooltip(graphics, font, mouseX, mouseY);
    plusTooltip(graphics, font, mouseX, mouseY);
  }

  /**
   * The name of an item of the recipe, in the grid or in the result, as in a slot (answer 1548).
   */
  private void recipeTooltip(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    ItemStack item =
        grid.at(mouseX, mouseY, left, top)
            .map(cell -> DeskGridView.markAt(cell, chosenMarks(), menu.marks(), playerTicks()))
            .orElseGet(() -> overResult(mouseX, mouseY) ? chosenResult() : ItemStack.EMPTY);
    if (!item.isEmpty()) {
      graphics.renderTooltip(font, item, mouseX, mouseY);
    }
  }

  /** Why the plus is off, one reason a line (answer 1988). */
  private void plusTooltip(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    List<Component> reasons = plus.isHovered() && !plus.active ? orderView.reasons() : List.of();
    if (!reasons.isEmpty()) {
      graphics.renderComponentTooltip(font, reasons, mouseX, mouseY);
    }
  }

  private boolean overResult(int mouseX, int mouseY) {
    int x = left + RESULT_X;
    int y = top + RESULT_Y;
    return mouseX >= x
        && mouseX < x + ScreenPainter.SLOT_SIZE
        && mouseY >= y
        && mouseY < y + ScreenPainter.SLOT_SIZE;
  }

  private static long playerTicks() {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft.player == null ? 0 : minecraft.player.tickCount;
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

  /**
   * Puts the carried item as a mark, unless an order holds the recipe. True when the click was on
   * the grid.
   */
  boolean click(double mouseX, double mouseY) {
    Optional<Integer> cell = grid.at(mouseX, mouseY, left, top);
    if (cell.isPresent() && !menu.holdsRecipe()) {
      menu.marks().set(cell.get(), menu.getCarried().copyWithCount(1));
      send.accept(DeskControl.mark(cell.get()));
    }
    return cell.isPresent();
  }
}
