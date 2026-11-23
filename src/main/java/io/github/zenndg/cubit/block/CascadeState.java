package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.CascadePlan;
import io.github.zenndg.cubit.domain.desk.CascadeRun;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * The plan of a desk: what was asked, the tasks, the items reserved for them and the finished ones
 * that wait for room in the output. It stays after the end to show the result (answer 2019).
 */
public final class CascadeState {
  private final CascadeStock reserved = new CascadeStock();
  private final CascadeStock waiting = new CascadeStock();
  private Item item;
  private int quantity;
  private CascadeRun run;

  /** A plan on its way, which locks the desk (answer 2006). */
  public boolean isRunning() {
    return run != null && !run.isDone();
  }

  public Optional<CascadeRun> run() {
    return Optional.ofNullable(run);
  }

  public Optional<Item> item() {
    return Optional.ofNullable(item);
  }

  public int quantity() {
    return quantity;
  }

  /** Items of a plan that still leave the desk, so no other plan starts (answer 2028). */
  public boolean holdsItems() {
    return !reserved.isEmpty() || !waiting.isEmpty();
  }

  CascadeStock reserved() {
    return reserved;
  }

  CascadeStock waiting() {
    return waiting;
  }

  void start(Item asked, CascadePlan plan) {
    item = asked;
    quantity = plan.quantity();
    run = CascadeRun.of(plan);
  }

  /** The recipe of the task in progress. */
  Optional<ResourceLocation> currentRecipe() {
    OptionalInt current = run == null ? OptionalInt.empty() : run.current();
    return current.isEmpty()
        ? Optional.empty()
        : Optional.ofNullable(
            ResourceLocation.tryParse(run.steps().get(current.getAsInt()).recipe()));
  }

  /** The order the clients see: the task in progress, for the hologram. */
  Optional<SavedDeskOrder> shownOrder() {
    return currentRecipe()
        .map(id -> {
          CascadeRun.Step step = run.steps().get(run.current().orElseThrow());
          return new SavedDeskOrder(id, step.total(), step.done());
        });
  }

  void crafted() {
    run = run.crafted();
    if (run.isDone()) {
      reserved.moveTo(waiting);
    }
  }

  /** The plan became impossible: everything goes back (answer 2010). */
  void abort() {
    reserved.moveTo(waiting);
    clear();
  }

  /** The result was seen, and another plan may be chosen (answer 2019). */
  public void forget() {
    if (!isRunning()) {
      clear();
    }
  }

  private void clear() {
    run = null;
    item = null;
    quantity = 0;
  }

  Optional<SavedCascade> saved() {
    if (run == null && !holdsItems()) {
      return Optional.empty();
    }
    List<SavedCascade.SavedStep> steps =
        run == null
            ? List.of()
            : run.steps().stream()
                .map(step ->
                    new SavedCascade.SavedStep(
                        ResourceLocation.parse(step.recipe()), step.total(), step.done()))
                .toList();
    Item kept = item == null ? Items.AIR : item;
    return Optional.of(new SavedCascade(kept, quantity, steps, reserved.saved(), waiting.saved()));
  }

  void load(Optional<SavedCascade> saved) {
    clear();
    reserved.load(saved.map(SavedCascade::reserved).orElse(List.of()));
    waiting.load(saved.map(SavedCascade::waiting).orElse(List.of()));
    run = saved.flatMap(SavedCascade::run).orElse(null);
    if (run == null || run.isDone()) {
      reserved.moveTo(waiting);
    }
    if (run != null) {
      item = saved.map(SavedCascade::item).orElse(null);
      quantity = saved.map(SavedCascade::quantity).orElse(0);
    }
  }
}
