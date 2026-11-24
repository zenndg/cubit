package io.github.zenndg.cubit.domain.desk;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * What the cascade makes to give an item: the tasks in series, the items without a recipe they take
 * and the parts already made they reuse (answers 2006, 2008 and 2012).
 */
public record CascadePlan(
    String item,
    int quantity,
    List<CascadeTask> tasks,
    Map<String, Integer> base,
    Map<String, Integer> reused) {

  public CascadePlan {
    tasks = List.copyOf(tasks);
    base = Map.copyOf(base);
    reused = Map.copyOf(reused);
  }

  /** The base materials and then the ready parts, in a fixed order (answer 2052). */
  public List<CascadeNeed> needs() {
    return Stream.concat(needs(base, false), needs(reused, true)).toList();
  }

  private static Stream<CascadeNeed> needs(Map<String, Integer> counts, boolean ready) {
    return counts.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(need -> new CascadeNeed(need.getKey(), need.getValue(), ready));
  }

  public int totalCrafts() {
    return tasks.stream().mapToInt(CascadeTask::crafts).sum();
  }
}
