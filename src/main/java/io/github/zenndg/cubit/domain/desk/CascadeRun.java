package io.github.zenndg.cubit.domain.desk;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * A plan on its way: the tasks in series, each with the crafts asked and done. It never goes back,
 * because a started plan cannot be canceled (answers 2006 and 2012).
 */
public record CascadeRun(List<Step> steps) {
  private static final String EMPTY = "A run has at least one step";
  private static final String ENDED = "The run already ended";

  /** The crafts of one recipe. */
  public record Step(String recipe, int total, int done) {
    private static final String INVALID = "Invalid step: ";

    public Step {
      if (total < 1 || done < 0 || done > total) {
        throw new IllegalArgumentException(INVALID + recipe);
      }
    }

    boolean isDone() {
      return done >= total;
    }
  }

  public CascadeRun {
    if (steps.isEmpty()) {
      throw new IllegalArgumentException(EMPTY);
    }
    steps = List.copyOf(steps);
  }

  public static CascadeRun of(CascadePlan plan) {
    return new CascadeRun(
        plan.tasks().stream().map(task -> new Step(task.recipe().id(), task.crafts(), 0)).toList());
  }

  /** The task in progress, or nothing at the end. */
  public OptionalInt current() {
    for (int index = 0; index < steps.size(); index++) {
      if (!steps.get(index).isDone()) {
        return OptionalInt.of(index);
      }
    }
    return OptionalInt.empty();
  }

  public boolean isDone() {
    return current().isEmpty();
  }

  public CascadeTaskStatus statusOf(int index) {
    if (steps.get(index).isDone()) {
      return CascadeTaskStatus.DONE;
    }
    return current().orElse(-1) == index ? CascadeTaskStatus.CRAFTING : CascadeTaskStatus.WAITING;
  }

  public CascadeRun crafted() {
    int index = current().orElseThrow(() -> new IllegalStateException(ENDED));
    List<Step> next = new ArrayList<>(steps);
    Step step = steps.get(index);
    next.set(index, new Step(step.recipe(), step.total(), step.done() + 1));
    return new CascadeRun(next);
  }

  public int totalCrafts() {
    return steps.stream().mapToInt(Step::total).sum();
  }

  public int doneCrafts() {
    return steps.stream().mapToInt(Step::done).sum();
  }

  /** What is left of the plan, from 15 down to 0, as an order (answers 1453 and 2022). */
  public int comparatorSignal() {
    return new DeskOrder(totalCrafts(), doneCrafts()).comparatorSignal();
  }
}
