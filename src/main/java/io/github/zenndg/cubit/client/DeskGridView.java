package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.DeskGrid;
import io.github.zenndg.cubit.block.DeskShortage;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * The 3x3 grid of the desk. It shows the marks of the chosen recipe, or the ones the player puts by
 * hand, never real items (answer 1426). The marks the desk misses show in red, and the others in
 * green (1996).
 */
final class DeskGridView {
  static final int X = 106;
  static final int Y = 30;
  private static final int CELL = ScreenPainter.SLOT_SIZE;
  private static final int ICON_INSET = 1;
  private static final int MARK_VEIL = 0x60C6C6C6;
  private static final int MISSING_VEIL = 0x60C62828;
  private static final int READY_VEIL = 0x602E7D32;
  private static final int CYCLE_TICKS = 20;

  void render(
      GuiGraphics graphics,
      int left,
      int top,
      List<Ingredient> recipe,
      NonNullList<ItemStack> hand,
      DeskShortage shortage,
      long tick) {
    for (int cell = 0; cell < DeskGrid.CELLS; cell++) {
      int x = left + X + (cell % DeskGrid.SIDE) * CELL;
      int y = top + Y + (cell / DeskGrid.SIDE) * CELL;
      BlueprintPainter.slot(graphics, x, y);
      ItemStack mark = markAt(cell, recipe, hand, tick);
      if (!mark.isEmpty()) {
        graphics.renderFakeItem(mark, x + ICON_INSET, y + ICON_INSET);
        int veil = veil(recipe, shortage, cell);
        graphics.fill(x + ICON_INSET, y + ICON_INSET, x + CELL - 1, y + CELL - 1, veil);
      }
    }
  }

  /** Weak red without the item and weak green with it; marks put by hand are only gray (1996). */
  private static int veil(List<Ingredient> recipe, DeskShortage shortage, int cell) {
    if (recipe.isEmpty()) {
      return MARK_VEIL;
    }
    return shortage.misses(cell) ? MISSING_VEIL : READY_VEIL;
  }

  /** The mark of a cell: the item the recipe shows now, or the one the player put by hand. */
  static ItemStack markAt(
      int cell, List<Ingredient> recipe, NonNullList<ItemStack> hand, long tick) {
    return recipe.isEmpty() ? hand.get(cell) : shown(recipe.get(cell), tick);
  }

  /** An ingredient with many items shows each in turn. */
  private static ItemStack shown(Ingredient ingredient, long tick) {
    ItemStack[] items = ingredient.getItems();
    return items.length == 0 ? ItemStack.EMPTY : items[(int) (tick / CYCLE_TICKS % items.length)];
  }

  Optional<Integer> at(double mouseX, double mouseY, int left, int top) {
    int column = (int) Math.floor((mouseX - left - X) / CELL);
    int row = (int) Math.floor((mouseY - top - Y) / CELL);
    boolean inside = column >= 0 && column < DeskGrid.SIDE && row >= 0 && row < DeskGrid.SIDE;
    return inside ? Optional.of(row * DeskGrid.SIDE + column) : Optional.empty();
  }
}
