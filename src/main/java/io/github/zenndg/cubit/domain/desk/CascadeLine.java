package io.github.zenndg.cubit.domain.desk;

import java.util.Map;
import java.util.Set;

/**
 * A line of the table of a plan: the item and how many, what the task takes in all, the places of
 * the tasks that must end before it and its progress (answers 2012 and 2024). The item of a recipe
 * a pack took away is empty.
 */
public record CascadeLine(
    String item,
    int made,
    Map<String, Integer> takes,
    Set<Integer> needs,
    int total,
    int done,
    CascadeTaskStatus status) {
  private static final String INVALID = "Invalid line: ";

  public CascadeLine {
    if (total < 1 || done < 0 || done > total) {
      throw new IllegalArgumentException(INVALID + item);
    }
    takes = Map.copyOf(takes);
    needs = Set.copyOf(needs);
  }

  /** The items the task takes in all. */
  public int reserved() {
    return takes.values().stream().mapToInt(Integer::intValue).sum();
  }
}
