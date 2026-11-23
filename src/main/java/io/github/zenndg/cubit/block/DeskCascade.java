package io.github.zenndg.cubit.block;

import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * The cascade of the desk: when the input misses materials and the input and the output together
 * make one item of the recipe, the desk pulls what is missing from the output, as far as it fits
 * (answers 1500 to 1503). Items only move between its inventories, so nothing is created or lost.
 */
final class DeskCascade {
  private DeskCascade() {}

  static void pull(List<Ingredient> grid, Container input, Container output, int slots) {
    int[] fromInput = new int[slots];
    int[] fromOutput = new int[slots];
    for (Ingredient mark : grid) {
      if (mark.isEmpty()) {
        continue;
      }
      if (!DeskMaterials.take(mark, input, fromInput)
          && !DeskMaterials.take(mark, output, fromOutput)) {
        return;
      }
    }
    move(output, input, slots, fromOutput);
  }

  /** Each item leaves the output only after the input took it. */
  private static void move(Container output, Container input, int slots, int[] wanted) {
    for (int slot = 0; slot < slots; slot++) {
      for (int count = 0; count < wanted[slot]; count++) {
        ItemStack one = output.getItem(slot).copyWithCount(1);
        if (one.isEmpty() || !DeskOutput.fits(input, slots, List.of(one))) {
          break;
        }
        DeskOutput.insert(input, slots, List.of(one));
        output.removeItem(slot, 1);
      }
    }
  }
}
