package io.github.zenndg.cubit.domain.desk;

import java.util.Map;

/**
 * A recipe of the desk as the cascade sees it: what it gives, how many, and how many of each item
 * one craft takes (answer 2009).
 */
public record CascadeRecipe(String id, String result, int yield, Map<String, Integer> materials) {
  private static final String NO_YIELD = "A recipe gives at least one item: ";
  private static final String NO_MATERIAL = "A recipe takes at least one item: ";

  public CascadeRecipe {
    if (yield < 1) {
      throw new IllegalArgumentException(NO_YIELD + id);
    }
    if (materials.isEmpty() || materials.values().stream().anyMatch(count -> count < 1)) {
      throw new IllegalArgumentException(NO_MATERIAL + id);
    }
    materials = Map.copyOf(materials);
  }

  /** The items one craft takes in all. */
  public int size() {
    return materials.values().stream().mapToInt(Integer::intValue).sum();
  }
}
