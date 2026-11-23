package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.CascadeBook;
import io.github.zenndg.cubit.domain.desk.CascadeCheck;
import io.github.zenndg.cubit.domain.desk.CascadePlan;
import io.github.zenndg.cubit.domain.desk.CascadePlanner;
import io.github.zenndg.cubit.domain.desk.CascadeStart;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Starts a plan on the server: it checks everything again and takes the base materials and the
 * reused parts at once to the stock of the plan (answers 2007, 2008 and 2015). Nothing leaves the
 * inventories unless all of it is there (rule 13).
 */
public final class CascadeLaunch {
  private CascadeLaunch() {}

  public static CascadeStart start(
      EngineeringDeskBlockEntity desk, ServerPlayer player, Item item, int quantity) {
    Map<String, Integer> input = CascadeRecipes.counts(desk.input());
    CascadeBook book = CascadeRecipes.book(player.serverLevel(), input);
    int asked = Math.min(quantity, desk.spec().maxOrder());
    Optional<CascadePlan> plan =
        CascadePlanner.plan(
            book,
            CascadeRecipes.idOf(item),
            asked,
            CascadeRecipes.ready(desk.input(), desk.output()));
    CascadeStart start =
        CascadeStart.decide(
            new CascadeCheck(
                desk.spec().cascade(),
                plan.isPresent(),
                released(player, book, item),
                plan.map(found -> hasBase(found, input)).orElse(false),
                desk.order().isActive(),
                desk.cascade().isRunning() || desk.cascade().holdsItems()));
    if (start == CascadeStart.READY) {
      reserve(desk, plan.orElseThrow());
      desk.cascade().start(item, plan.orElseThrow());
      desk.cascadeChanged();
    }
    return start;
  }

  public static boolean hasBase(CascadePlan plan, Map<String, Integer> input) {
    return plan.base().entrySet().stream()
        .allMatch(need -> input.getOrDefault(need.getKey(), 0) >= need.getValue());
  }

  /** Only an item with a plan and with its whole chain released is chosen (2009, 2038). */
  public static boolean mayChoose(ServerPlayer player, RecipeHolder<CraftingRecipe> recipe) {
    if (!CascadeRecipes.isFixed(recipe)) {
      return false;
    }
    CascadeBook book = CascadeRecipes.book(player.serverLevel(), Map.of());
    Item item = recipe.value().getResultItem(player.registryAccess()).getItem();
    return CascadePlanner.order(book, CascadeRecipes.idOf(item)).isPresent()
        && released(player, book, item);
  }

  /** The item and its whole chain, for who starts it (answers 1991, 2009 and 2016). */
  public static boolean released(ServerPlayer player, CascadeBook book, Item item) {
    return CascadePlanner.order(book, CascadeRecipes.idOf(item)).stream()
        .flatMap(order -> order.stream())
        .allMatch(each ->
            book.recipeOf(each)
                .map(recipe -> ResourceLocation.parse(recipe.id()))
                .map(id -> player.getRecipeBook().contains(id))
                .orElse(false));
  }

  private static void reserve(EngineeringDeskBlockEntity desk, CascadePlan plan) {
    CascadeStock stock = desk.cascade().reserved();
    plan.base().forEach((item, count) -> take(desk.input(), item, count, stock));
    plan.reused()
        .forEach((item, count) -> {
          int left = take(desk.input(), item, count, stock);
          take(desk.output(), item, left, stock);
        });
  }

  /** Moves up to the count of an item to the stock, and tells how many are still missing. */
  private static int take(Container from, String item, int count, CascadeStock stock) {
    int left = count;
    for (int slot = 0; slot < from.getContainerSize() && left > 0; slot++) {
      if (CascadeRecipes.idOf(from.getItem(slot).getItem()).equals(item)) {
        ItemStack taken = from.removeItem(slot, left);
        left -= taken.getCount();
        stock.add(taken);
      }
    }
    return left;
  }
}
