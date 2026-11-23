package io.github.zenndg.cubit.domain.desk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * The table of a plan, the same before and after the start (answer 2024). A running plan travels
 * only as recipes and crafts, and the rest comes back from the recipes.
 */
public record CascadeTable(List<CascadeLine> lines) {
  public static final CascadeTable EMPTY = new CascadeTable(List.of());
  private static final String GONE = "";

  public CascadeTable {
    lines = List.copyOf(lines);
  }

  public static CascadeTable of(CascadePlan plan) {
    return new CascadeTable(
        plan.tasks().stream().map(task -> line(task, 0, CascadeTaskStatus.WAITING)).toList());
  }

  public static CascadeTable of(CascadeRun run, CascadeBook book) {
    List<Optional<CascadeRecipe>> recipes =
        run.steps().stream().map(step -> book.recipeWithId(step.recipe())).toList();
    List<String> made =
        recipes.stream().map(recipe -> recipe.map(CascadeRecipe::result).orElse(GONE)).toList();
    return new CascadeTable(
        IntStream.range(0, recipes.size())
            .mapToObj(index -> line(recipes.get(index), made, run, index))
            .toList());
  }

  private static CascadeLine line(
      Optional<CascadeRecipe> recipe, List<String> made, CascadeRun run, int index) {
    CascadeRun.Step step = run.steps().get(index);
    CascadeTaskStatus status = run.statusOf(index);
    return recipe
        .map(found ->
            line(
                new CascadeTask(found, step.total(), CascadeTask.needsOf(found, made)),
                step.done(),
                status))
        .orElseGet(() ->
            new CascadeLine(
                GONE, step.total(), Map.of(), Set.of(), step.total(), step.done(), status));
  }

  private static CascadeLine line(CascadeTask task, int done, CascadeTaskStatus status) {
    Map<String, Integer> takes = new HashMap<>();
    task.recipe().materials().forEach((item, each) -> takes.put(item, each * task.crafts()));
    return new CascadeLine(
        task.recipe().result(), task.made(), takes, task.needs(), task.crafts(), done, status);
  }

  public int totalCrafts() {
    return lines.stream().mapToInt(CascadeLine::total).sum();
  }
}
