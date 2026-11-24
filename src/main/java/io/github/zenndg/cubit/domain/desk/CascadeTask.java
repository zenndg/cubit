package io.github.zenndg.cubit.domain.desk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A line of the plan: a recipe, how many crafts of it and the places of the tasks that must end
 * before it (answers 2012 and 2024).
 */
public record CascadeTask(CascadeRecipe recipe, int crafts, Set<Integer> needs) {
  private static final String NO_CRAFTS = "A task has at least one craft: ";

  public CascadeTask {
    if (crafts < 1) {
      throw new IllegalArgumentException(NO_CRAFTS + recipe.id());
    }
    needs = Set.copyOf(needs);
  }

  /** The places, in a list of items made in order, of the ones a recipe takes. */
  static Set<Integer> needsOf(CascadeRecipe recipe, List<String> made) {
    return recipe.materials().keySet().stream()
        .map(made::indexOf)
        .filter(index -> index >= 0)
        .collect(Collectors.toUnmodifiableSet());
  }

  /** The items the task takes in all. */
  public int reserved() {
    return crafts * recipe.size();
  }

  public int made() {
    return crafts * recipe.yield();
  }
}
