package io.github.zenndg.cubit.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * The marks of a recipe the desk has no item for, with the input and the output together, as in the
 * cascade (answers 1502 and 1984). The server holds the order with it, and the screen shows it.
 *
 * @param cells the places of the marks in the grid
 * @param missing the marks, in the order of the grid
 */
public record DeskShortage(Set<Integer> cells, List<Ingredient> missing) {
  public static final DeskShortage NONE = new DeskShortage(Set.of(), List.of());

  public static DeskShortage of(
      List<Ingredient> grid, Container input, Container output, int slots) {
    int[] fromInput = new int[slots];
    int[] fromOutput = new int[slots];
    Set<Integer> cells = new TreeSet<>();
    List<Ingredient> missing = new ArrayList<>();
    for (int cell = 0; cell < grid.size(); cell++) {
      Ingredient mark = grid.get(cell);
      if (!mark.isEmpty()
          && !DeskMaterials.take(mark, input, fromInput)
          && !DeskMaterials.take(mark, output, fromOutput)) {
        cells.add(cell);
        missing.add(mark);
      }
    }
    return new DeskShortage(cells, missing);
  }

  public boolean isEmpty() {
    return cells.isEmpty();
  }
}
