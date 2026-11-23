package io.github.zenndg.cubit.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Finds the real items of the input for the marks of a recipe: each mark takes one item, the first
 * that serves in the order of the slots (answer 1427). Nothing leaves the input here.
 */
final class DeskMaterials {
  private DeskMaterials() {}

  /** The items placed as the recipe asks, and how many leave each slot when the craft ends. */
  record Pick(CraftingInput input, int[] taken) {}

  static Optional<Pick> pick(List<Ingredient> grid, Container inventory, int slots) {
    int[] taken = new int[slots];
    List<ItemStack> cells = new ArrayList<>(grid.size());
    for (Ingredient mark : grid) {
      if (mark.isEmpty()) {
        cells.add(ItemStack.EMPTY);
        continue;
      }
      int slot = firstServing(mark, inventory, taken);
      if (slot < 0) {
        return Optional.empty();
      }
      taken[slot]++;
      cells.add(inventory.getItem(slot).copyWithCount(1));
    }
    return Optional.of(new Pick(CraftingInput.of(DeskGrid.SIDE, DeskGrid.SIDE, cells), taken));
  }

  /** Counts one item for the mark in the slot that serves, if any. Nothing leaves the slot. */
  static boolean take(Ingredient mark, Container inventory, int[] taken) {
    return takeUpTo(mark, inventory, taken, 1) > 0;
  }

  /** Counts up to the wanted items for the mark, in the order of the slots, and tells how many. */
  static int takeUpTo(Ingredient mark, Container inventory, int[] taken, int wanted) {
    int found = 0;
    for (int slot = 0; slot < taken.length && found < wanted; slot++) {
      ItemStack stack = inventory.getItem(slot);
      int free = stack.getCount() - taken[slot];
      if (free > 0 && mark.test(stack)) {
        int count = Math.min(free, wanted - found);
        taken[slot] += count;
        found += count;
      }
    }
    return found;
  }

  private static int firstServing(Ingredient mark, Container inventory, int[] taken) {
    for (int slot = 0; slot < taken.length; slot++) {
      ItemStack stack = inventory.getItem(slot);
      if (stack.getCount() > taken[slot] && mark.test(stack)) {
        return slot;
      }
    }
    return -1;
  }

  static void remove(Container inventory, int[] taken) {
    for (int slot = 0; slot < taken.length; slot++) {
      if (taken[slot] > 0) {
        inventory.removeItem(slot, taken[slot]);
      }
    }
  }
}
