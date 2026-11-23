package io.github.zenndg.cubit.domain.desk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Turns an item and a quantity into a plan: from the item down to the ones without a recipe, each
 * item once, with the parts before what takes them (answers 2006 to 2009 and 2031).
 */
public final class CascadePlanner {
  private final CascadeBook book;
  private final Map<String, Integer> ready;
  private final Map<String, Integer> demand = new HashMap<>();
  private final Map<String, Integer> crafts = new HashMap<>();
  private final Map<String, Integer> base = new LinkedHashMap<>();
  private final Map<String, Integer> reused = new LinkedHashMap<>();

  private CascadePlanner(CascadeBook book, Map<String, Integer> ready) {
    this.book = book;
    this.ready = ready;
  }

  /** Nothing for an item without a recipe, or with recipes in a cycle (answer 2034). */
  public static Optional<CascadePlan> plan(
      CascadeBook book, String item, int quantity, Map<String, Integer> ready) {
    if (quantity < 1 || book.recipeOf(item).isEmpty()) {
      return Optional.empty();
    }
    return order(book, item)
        .map(order -> new CascadePlanner(book, ready).plan(item, quantity, order));
  }

  /** The items with a recipe under an item, with the parts first, or nothing with a cycle. */
  public static Optional<List<String>> order(CascadeBook book, String item) {
    Set<String> ordered = new LinkedHashSet<>();
    return visit(book, item, new HashSet<>(), ordered)
        ? Optional.of(List.copyOf(ordered))
        : Optional.empty();
  }

  private static boolean visit(
      CascadeBook book, String item, Set<String> open, Set<String> ordered) {
    Optional<CascadeRecipe> recipe = book.recipeOf(item);
    if (recipe.isEmpty() || ordered.contains(item)) {
      return true;
    }
    if (!open.add(item)) {
      return false;
    }
    for (String material : recipe.get().materials().keySet().stream().sorted().toList()) {
      if (!visit(book, material, open, ordered)) {
        return false;
      }
    }
    open.remove(item);
    ordered.add(item);
    return true;
  }

  private CascadePlan plan(String item, int quantity, List<String> order) {
    demand.put(item, quantity);
    for (String each : order.reversed()) {
      spread(each, each.equals(item));
    }
    return new CascadePlan(item, quantity, tasks(order), base, reused);
  }

  /**
   * The demand of an item, less what is ready of it, becomes crafts and the demand of its parts.
   */
  private void spread(String item, boolean asked) {
    int wanted = demand.getOrDefault(item, 0);
    int reuse = asked ? 0 : Math.min(wanted, ready.getOrDefault(item, 0));
    if (reuse > 0) {
      reused.put(item, reuse);
    }
    CascadeRecipe recipe = book.recipeOf(item).orElseThrow();
    int count = Math.ceilDiv(wanted - reuse, recipe.yield());
    if (count == 0) {
      return;
    }
    crafts.put(item, count);
    recipe.materials().forEach((material, each) -> ask(material, each * count));
  }

  private void ask(String material, int count) {
    Map<String, Integer> where = book.recipeOf(material).isPresent() ? demand : base;
    where.merge(material, count, Integer::sum);
  }

  private List<CascadeTask> tasks(List<String> order) {
    List<String> made = order.stream().filter(crafts::containsKey).toList();
    List<CascadeTask> tasks = new ArrayList<>();
    for (String item : made) {
      CascadeRecipe recipe = book.recipeOf(item).orElseThrow();
      tasks.add(new CascadeTask(recipe, crafts.get(item), CascadeTask.needsOf(recipe, made)));
    }
    return tasks;
  }
}
