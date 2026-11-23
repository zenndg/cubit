package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.CascadeBook;
import io.github.zenndg.cubit.domain.desk.CascadeChoice;
import io.github.zenndg.cubit.domain.desk.CascadeRecipe;
import io.github.zenndg.cubit.item.EngineeringRecipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/**
 * The recipes of the desk as the cascade sees them: only the ones with a fixed result, without the
 * upgrade of a Power Bank, the recharge and the repair (answer 2009).
 */
public final class CascadeRecipes {
  private CascadeRecipes() {}

  public static boolean isFixed(RecipeHolder<CraftingRecipe> holder) {
    return holder.value() instanceof EngineeringRecipe;
  }

  /** The book for what the input has, which picks between the items of a mark (answer 2033). */
  public static CascadeBook book(Level level, Map<String, Integer> input) {
    List<CascadeRecipe> recipes = new ArrayList<>();
    for (RecipeHolder<CraftingRecipe> holder : DeskRecipes.all(level.getRecipeManager())) {
      if (isFixed(holder)) {
        read(level, holder, input).ifPresent(recipes::add);
      }
    }
    return CascadeBook.of(recipes);
  }

  /** Nothing for a recipe a pack left without a result or without materials (rule 17). */
  private static Optional<CascadeRecipe> read(
      Level level, RecipeHolder<CraftingRecipe> holder, Map<String, Integer> input) {
    ItemStack result = holder.value().getResultItem(level.registryAccess());
    Map<String, Integer> materials = new HashMap<>();
    for (Ingredient mark : DeskGrid.of(holder.value())) {
      if (mark.isEmpty()) {
        continue;
      }
      List<String> options =
          Arrays.stream(mark.getItems()).map(stack -> idOf(stack.getItem())).toList();
      if (options.isEmpty()) {
        return Optional.empty();
      }
      materials.merge(CascadeChoice.of(options, input), 1, Integer::sum);
    }
    if (result.isEmpty() || materials.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        new CascadeRecipe(
            holder.id().toString(), idOf(result.getItem()), result.getCount(), materials));
  }

  public static String idOf(Item item) {
    return BuiltInRegistries.ITEM.getKey(item).toString();
  }

  public static Item itemOf(String id) {
    return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
  }

  /** How many of each item a container has. */
  public static Map<String, Integer> counts(Container container) {
    Map<String, Integer> counts = new HashMap<>();
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stack = container.getItem(slot);
      if (!stack.isEmpty()) {
        counts.merge(idOf(stack.getItem()), stack.getCount(), Integer::sum);
      }
    }
    return counts;
  }

  /** The parts ready in the input and in the output (answer 2008). */
  public static Map<String, Integer> ready(Container input, Container output) {
    Map<String, Integer> ready = counts(input);
    counts(output).forEach((item, count) -> ready.merge(item, count, Integer::sum));
    return ready;
  }
}
