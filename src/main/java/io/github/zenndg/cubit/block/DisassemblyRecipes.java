package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
import io.github.zenndg.cubit.domain.desk.DisassemblyReturn;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/**
 * How a desk takes an item apart: the first recipe of the desk in the order of the catalog that
 * makes it, without the special ones, and what comes back of it (answers 2526 and 2530).
 */
public final class DisassemblyRecipes {
  private static final Map<RobotTier, Supplier<Item>> MODULES =
      Map.of(
          RobotTier.ADVANCED, CubitItems.ADVANCED_MODULE,
          RobotTier.ELITE, CubitItems.ELITE_MODULE,
          RobotTier.ULTIMATE, CubitItems.ULTIMATE_MODULE);
  private static final Predicate<String> FROM_MOD =
      id -> CubitMod.MOD_ID.equals(ResourceLocation.parse(id).getNamespace());

  private DisassemblyRecipes() {}

  /** How many items one unit takes, which the recipe makes, and what comes back of them. */
  public record Plan(int yield, List<ItemStack> parts) {}

  public static Optional<Plan> plan(Level level, ItemStack stack) {
    if (stack.isEmpty()) {
      return Optional.empty();
    }
    for (RecipeHolder<CraftingRecipe> holder : DeskRecipes.all(level.getRecipeManager())) {
      ItemStack result = holder.value().getResultItem(level.registryAccess());
      if (CascadeRecipes.isFixed(holder) && result.is(stack.getItem())) {
        return planOf(holder.value(), result.getCount(), stack);
      }
    }
    return Optional.empty();
  }

  /** Nothing for a recipe without a part of the mod, which has nothing to give back. */
  private static Optional<Plan> planOf(CraftingRecipe recipe, int yield, ItemStack stack) {
    List<List<String>> marks =
        DeskGrid.of(recipe).stream()
            .filter(mark -> !mark.isEmpty())
            .map(DisassemblyRecipes::options)
            .toList();
    if (!DisassemblyReturn.hasParts(marks, FROM_MOD)) {
      return Optional.empty();
    }
    List<String> modules =
        DisassemblyReturn.modulesUpTo(tierOf(stack)).stream()
            .map(tier -> CascadeRecipes.idOf(moduleOf(tier)))
            .toList();
    List<ItemStack> parts =
        DisassemblyReturn.of(marks, FROM_MOD, modules).stream()
            .map(part -> new ItemStack(CascadeRecipes.itemOf(part.item()), part.count()))
            .toList();
    return Optional.of(new Plan(yield, parts));
  }

  private static List<String> options(Ingredient mark) {
    return Arrays.stream(mark.getItems()).map(item -> CascadeRecipes.idOf(item.getItem())).toList();
  }

  /** The tier a Cubit, a dock or a Network Server keeps; anything else is a Base one. */
  private static RobotTier tierOf(ItemStack stack) {
    if (stack.has(CubitDataComponents.ROBOT_STATE.get())) {
      return TierLook.tierOf(stack);
    }
    SavedNetwork network = stack.get(CubitDataComponents.NETWORK_STATE.get());
    if (network != null) {
      return network.tier();
    }
    return TierLook.dockTierOf(stack);
  }

  /** The Module that took an item to a tier above the Base. */
  private static Item moduleOf(RobotTier tier) {
    return MODULES.get(tier).get();
  }

  /** Why the desk does not take the item of its slot apart now, or none (answer 2524). */
  public static DisassemblyRefusal refusal(EngineeringDeskBlockEntity desk, Level level) {
    ItemStack stack = desk.disassembly().item();
    Optional<Plan> plan = plan(level, stack);
    return DisassemblyRefusal.decide(
        new DisassemblyRefusal.Check(
            desk.spec().disassembly(),
            stack.isEmpty(),
            plan.isPresent(),
            plan.map(found -> stack.getCount() >= found.yield()).orElse(false),
            DisassemblyContents.has(stack),
            !desk.jobs().allowsDisassembly()));
  }
}
