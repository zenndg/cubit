package io.github.zenndg.cubit.block;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * The marks of a recipe the desk has no items for, with the input and the output together, as in
 * the cascade (answers 1502 and 1984). Each mark needs its item for every unit, in the order of the
 * grid, so with items for part of them only the last ones miss (answers 1997 and 1998). The server
 * holds the order with one unit, and the screen shows what is left of it.
 *
 * @param lacks what each place of the grid misses
 */
public record DeskShortage(Map<Integer, Lack> lacks) {
  public static final DeskShortage NONE = new DeskShortage(Map.of());

  /** A mark without enough items, and how many it misses. */
  public record Lack(Ingredient mark, int count) {}

  public static DeskShortage of(
      List<Ingredient> grid, Container input, Container output, int slots, int units) {
    int[] fromInput = new int[slots];
    int[] fromOutput = new int[slots];
    Map<Integer, Lack> lacks = new TreeMap<>();
    for (int cell = 0; cell < grid.size(); cell++) {
      Ingredient mark = grid.get(cell);
      if (mark.isEmpty()) {
        continue;
      }
      int left = units - DeskMaterials.takeUpTo(mark, input, fromInput, units);
      left -= DeskMaterials.takeUpTo(mark, output, fromOutput, left);
      if (left > 0) {
        lacks.put(cell, new Lack(mark, left));
      }
    }
    return new DeskShortage(lacks);
  }

  public boolean isEmpty() {
    return lacks.isEmpty();
  }

  public boolean misses(int cell) {
    return lacks.containsKey(cell);
  }
}
