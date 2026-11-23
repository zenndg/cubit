package io.github.zenndg.cubit.block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The release of the recipes of the desk for who opens it, on the server. A recipe that no
 * advancement gives, or whose advancement is done, is released even when the recipe book lost it,
 * and the items inside the desk count as the ones of the player (answers 1969 and 1974). Only the
 * recipes still locked are kept, so the check is light.
 */
public final class DeskUnlocks {
  private final ServerPlayer player;
  private final List<Gate> gates;

  /** A locked recipe and the advancements that give it. */
  private record Gate(RecipeHolder<CraftingRecipe> recipe, List<AdvancementHolder> advancements) {}

  private DeskUnlocks(ServerPlayer player, List<Gate> gates) {
    this.player = player;
    this.gates = gates;
  }

  public static DeskUnlocks of(Player player) {
    return of(player, DeskRecipes.all(player.level().getRecipeManager()));
  }

  public static DeskUnlocks of(Player player, List<RecipeHolder<CraftingRecipe>> recipes) {
    if (!(player instanceof ServerPlayer server)) {
      return new DeskUnlocks(null, new ArrayList<>());
    }
    List<Gate> gates = new ArrayList<>();
    for (RecipeHolder<CraftingRecipe> recipe : recipes) {
      if (!DeskRecipes.unlocked(server, recipe)) {
        gates.add(new Gate(recipe, givers(server, recipe.id())));
      }
    }
    return new DeskUnlocks(server, gates);
  }

  private static List<AdvancementHolder> givers(ServerPlayer player, ResourceLocation recipe) {
    return player.server.getAdvancements().getAllAdvancements().stream()
        .filter(holder -> holder.value().rewards().recipes().contains(recipe))
        .toList();
  }

  /** Releases what it can, with the items of the input and of the output. */
  public void check(Container input, Container output) {
    if (gates.isEmpty()) {
      return;
    }
    List<ItemStack> items =
        Stream.of(input, output)
            .flatMap(container ->
                IntStream.range(0, container.getContainerSize()).mapToObj(container::getItem))
            .filter(stack -> !stack.isEmpty())
            .toList();
    gates.removeIf(gate -> release(gate, items));
  }

  private boolean release(Gate gate, List<ItemStack> items) {
    gate.advancements().forEach(advancement -> countItems(advancement, items));
    if (!DeskRecipes.unlocked(player, gate.recipe()) && isFree(gate)) {
      player.awardRecipes(List.of(gate.recipe()));
    }
    return DeskRecipes.unlocked(player, gate.recipe());
  }

  private boolean isFree(Gate gate) {
    return gate.advancements().isEmpty()
        || gate.advancements().stream()
            .anyMatch(holder -> player.getAdvancements().getOrStartProgress(holder).isDone());
  }

  private void countItems(AdvancementHolder advancement, List<ItemStack> items) {
    advancement
        .value()
        .criteria()
        .forEach((name, criterion) -> {
          if (has(criterion, items)) {
            player.getAdvancements().award(advancement, name);
          }
        });
  }

  /** Only a plain criterion of having items, as the ones of the recipes, counts here. */
  private static boolean has(Criterion<?> criterion, List<ItemStack> items) {
    return criterion.triggerInstance() instanceof InventoryChangeTrigger.TriggerInstance trigger
        && trigger.player().isEmpty()
        && !trigger.items().isEmpty()
        && trigger.items().stream().allMatch(wanted -> items.stream().anyMatch(wanted));
  }

  /** The items that release each recipe still locked, for the hint of the screen (answer 1970). */
  public Map<ResourceLocation, List<Item>> openers() {
    Map<ResourceLocation, List<Item>> openers = new LinkedHashMap<>();
    gates.forEach(gate -> openers.put(gate.recipe().id(), itemsOf(gate)));
    return openers;
  }

  private static List<Item> itemsOf(Gate gate) {
    return gate.advancements().stream()
        .flatMap(holder -> holder.value().criteria().values().stream())
        .map(Criterion::triggerInstance)
        .filter(InventoryChangeTrigger.TriggerInstance.class::isInstance)
        .map(InventoryChangeTrigger.TriggerInstance.class::cast)
        .flatMap(trigger -> trigger.items().stream())
        .flatMap(wanted -> wanted.items().stream())
        .flatMap(holders -> holders.stream())
        .map(Holder::value)
        .distinct()
        .toList();
  }
}
