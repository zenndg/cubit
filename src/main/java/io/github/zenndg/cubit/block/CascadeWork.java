package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.desk.DeskCheck;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * One tick of a plan: a craft of the task in progress, with the items of the stock, the time and
 * the energy of the desk (answers 2006 to 2014). A full output never stops it, because what does
 * not fit waits (answer 2028), and a plan that cannot go on gives everything back (answer 2010).
 */
final class CascadeWork {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String IMPOSSIBLE = "The plan of the desk at {} cannot go on, at recipe {}.";

  private CascadeWork() {}

  static DeskStatus tick(EngineeringDeskBlockEntity desk, Level level, boolean redstone) {
    CascadeState cascade = desk.cascade();
    Optional<RecipeHolder<CraftingRecipe>> recipe =
        cascade.currentRecipe().flatMap(id -> DeskRecipes.find(level.getRecipeManager(), id));
    Optional<DeskCraft> craft = recipe.flatMap(found -> craft(cascade, found, level));
    if (craft.isEmpty()) {
      LOGGER.warn(IMPOSSIBLE, desk.getBlockPos(), cascade.currentRecipe().orElse(null));
      cascade.abort();
      desk.cascadeChanged();
      return DeskStatus.IDLE;
    }
    DeskCheck check = new DeskCheck(true, redstone, true, true, desk.canPayCraft(), true);
    DeskStatus status = DeskStatus.decide(check);
    if (status.isWorking() && desk.advance()) {
      finish(desk, craft.get());
    }
    return status;
  }

  private static Optional<DeskCraft> craft(
      CascadeState cascade, RecipeHolder<CraftingRecipe> recipe, Level level) {
    List<Ingredient> grid = DeskGrid.of(recipe.value());
    Container view = cascade.reserved().view();
    return DeskMaterials.pick(grid, view, view.getContainerSize())
        .flatMap(pick -> DeskCraft.of(recipe.value(), pick, level));
  }

  /** The asked item goes to the output or waits for room, and the parts stay reserved. */
  private static void finish(EngineeringDeskBlockEntity desk, DeskCraft craft) {
    CascadeState cascade = desk.cascade();
    DeskMaterials.remove(cascade.reserved().view(), craft.pick().taken());
    cascade.reserved().prune();
    for (ItemStack made : craft.outputs()) {
      boolean asked = cascade.item().map(made::is).orElse(false);
      (asked ? cascade.waiting() : cascade.reserved()).add(made);
    }
    desk.craftedInCascade();
  }

  /**
   * What waits goes to the output as it has room. What came back from an impossible plan and is a
   * material goes to the input first (answer 2010).
   */
  static void unload(EngineeringDeskBlockEntity desk, Level level) {
    CascadeStock waiting = desk.cascade().waiting();
    while (!waiting.isEmpty() && moveOne(desk, level, waiting.first())) {
      waiting.prune();
    }
  }

  private static boolean moveOne(EngineeringDeskBlockEntity desk, Level level, ItemStack stack) {
    List<ItemStack> one = List.of(stack.copyWithCount(1));
    boolean material =
        desk.cascade().run().isEmpty()
            && DeskMaterialFilter.accepts(level.getRecipeManager(), stack);
    Container target = desk.output();
    if (material && DeskOutput.fits(desk.input(), desk.slots(), one)) {
      target = desk.input();
    } else if (!DeskOutput.fits(desk.output(), desk.slots(), one)) {
      return false;
    }
    DeskOutput.insert(target, desk.slots(), one);
    stack.shrink(1);
    desk.setChanged();
    return true;
  }
}
