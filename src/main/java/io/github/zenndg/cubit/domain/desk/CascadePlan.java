package io.github.zenndg.cubit.domain.desk;

import java.util.List;
import java.util.Map;

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

  public int totalCrafts() {
    return tasks.stream().mapToInt(CascadeTask::crafts).sum();
  }
}
