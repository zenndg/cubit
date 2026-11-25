package io.github.zenndg.cubit.menu;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * What is ready of the result of the chosen recipe. It is a window to the output, where the items
 * are, so nothing is ever in two places (rule 13 and answer 2479). An item counts by its kind, as a
 * repaired Cubit keeps its own name and data.
 *
 * @param first the first item ready, or nothing
 * @param count how many are ready
 */
public record DeskResult(ItemStack first, int count) {
  static DeskResult of(Container output, ItemStack result) {
    ItemStack first = ItemStack.EMPTY;
    int count = 0;
    for (int slot = 0; slot < output.getContainerSize(); slot++) {
      ItemStack stack = output.getItem(slot);
      if (matches(stack, result)) {
        first = first.isEmpty() ? stack : first;
        count += stack.getCount();
      }
    }
    return new DeskResult(first, count);
  }

  static boolean matches(ItemStack stack, ItemStack result) {
    return !stack.isEmpty() && stack.is(result.getItem());
  }

  public boolean isEmpty() {
    return count == 0;
  }

  /** What the cursor holds after a click: up to one stack, of items that stack with each other. */
  static ItemStack take(Container output, ItemStack result, ItemStack carried) {
    ItemStack kind = carried.isEmpty() ? of(output, result).first().copy() : carried;
    if (!matches(kind, result)) {
      return carried;
    }
    ItemStack taken = kind.copyWithCount(carried.getCount());
    for (int slot = 0; slot < output.getContainerSize(); slot++) {
      int room = kind.getMaxStackSize() - taken.getCount();
      if (room > 0 && ItemStack.isSameItemSameComponents(output.getItem(slot), kind)) {
        taken.grow(output.removeItem(slot, room).getCount());
      }
    }
    return taken;
  }
}
