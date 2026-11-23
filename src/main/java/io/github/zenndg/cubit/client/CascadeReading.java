package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.CascadeLaunch;
import io.github.zenndg.cubit.block.CascadeRecipes;
import io.github.zenndg.cubit.domain.desk.CascadeBook;
import io.github.zenndg.cubit.domain.desk.CascadeCheck;
import io.github.zenndg.cubit.domain.desk.CascadeCost;
import io.github.zenndg.cubit.domain.desk.CascadePlan;
import io.github.zenndg.cubit.domain.desk.CascadePlanner;
import io.github.zenndg.cubit.domain.desk.CascadeRun;
import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.CascadeTable;
import io.github.zenndg.cubit.menu.CascadeMenuPart;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * What the Cascade tab shows: the plan of what this player chose or the one on its way. It only
 * shows: the server decides again (rule 19).
 */
final class CascadeReading {
  private static final String GONE = "";

  private final EngineeringDeskMenu menu;
  private final DeskCatalog catalog;
  private Optional<CascadePlan> plan = Optional.empty();
  private CascadeTable table = CascadeTable.EMPTY;
  private CascadeStart start = CascadeStart.NO_PLAN;
  private Map<String, Integer> input = Map.of();

  CascadeReading(EngineeringDeskMenu menu, DeskCatalog catalog) {
    this.menu = menu;
    this.catalog = catalog;
  }

  /** Once per tick, because the plan reads every recipe of the desk. */
  void tick() {
    plan = Optional.empty();
    table = CascadeTable.EMPTY;
    start = CascadeStart.NO_PLAN;
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level == null) {
      return;
    }
    input = menu.inputCounts();
    CascadeBook book = CascadeRecipes.book(minecraft.level, input);
    if (choosing()) {
      readChoice(book);
    } else {
      table = run().map(run -> CascadeTable.of(run, book)).orElse(CascadeTable.EMPTY);
    }
  }

  private void readChoice(CascadeBook book) {
    Optional<Item> item = chosen();
    if (item.isEmpty()) {
      return;
    }
    String id = CascadeRecipes.idOf(item.get());
    plan = CascadePlanner.plan(book, id, quantity(), menu.readyCounts());
    table = plan.map(CascadeTable::of).orElse(CascadeTable.EMPTY);
    start =
        CascadeStart.decide(
            new CascadeCheck(
                menu.cascade().allowed(),
                quantity() > 0,
                plan.isPresent(),
                released(book, id),
                plan.map(found -> CascadeLaunch.hasBase(found, input)).orElse(false),
                menu.total() > menu.done(),
                menu.cascade().holdsItems()));
  }

  private static boolean released(CascadeBook book, String item) {
    return CascadePlanner.order(book, item).stream()
        .flatMap(List::stream)
        .allMatch(each -> book.recipeOf(each).map(recipe -> known(recipe.id())).orElse(false));
  }

  private static boolean known(String recipe) {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft.player != null
        && minecraft.player.getRecipeBook().contains(ResourceLocation.parse(recipe));
  }

  /** The plan on its way, or nothing while its numbers are still on the way from the server. */
  private Optional<CascadeRun> run() {
    List<CascadeMenuPart.StepView> steps = menu.cascade().steps();
    boolean arrived =
        !steps.isEmpty()
            && steps.stream()
                .allMatch(
                    step -> step.total() > 0 && step.done() >= 0 && step.done() <= step.total());
    return arrived
        ? Optional.of(
            new CascadeRun(
                steps.stream()
                    .map(step ->
                        new CascadeRun.Step(recipeId(step.recipe()), step.total(), step.done()))
                    .toList()))
        : Optional.empty();
  }

  private String recipeId(int index) {
    return recipeAt(index).map(recipe -> recipe.id().toString()).orElse(GONE);
  }

  private Optional<RecipeHolder<CraftingRecipe>> recipeAt(int index) {
    List<RecipeHolder<CraftingRecipe>> all = catalog.recipes();
    return index < 0 || index >= all.size() ? Optional.empty() : Optional.of(all.get(index));
  }

  Optional<Item> chosen() {
    return recipeAt(menu.cascade().recipe()).map(recipe -> DeskCatalog.resultOf(recipe).getItem());
  }

  int quantity() {
    return menu.cascade().quantity();
  }

  boolean choosing() {
    return menu.cascade().state() == CascadeMenuPart.State.NONE;
  }

  Optional<CascadePlan> plan() {
    return plan;
  }

  CascadeTable table() {
    return table;
  }

  CascadeCost cost() {
    return menu.cascade().cost(table.totalCrafts());
  }

  int inInput(String item) {
    return input.getOrDefault(item, 0);
  }

  boolean showsPlan() {
    return choosing() && start.showsPlan();
  }

  boolean mayStart() {
    return choosing() && start == CascadeStart.READY;
  }

  /** Why the plan cannot be seen or started, for the buttons. */
  Component reason() {
    return Component.translatable(DeskScreenKeys.cascadeStart(start));
  }
}
