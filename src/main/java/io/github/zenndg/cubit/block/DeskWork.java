package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskCheck;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/**
 * One tick of work of a desk: it reads the materials, the energy and the room in the output, and
 * ends a craft at once, taking the materials and the energy only then (rule 13, answers 1435 and
 * 1443). The domain decides the status.
 */
final class DeskWork {
  private DeskWork() {}

  static DeskStatus tick(EngineeringDeskBlockEntity desk, Level level, boolean redstone) {
    Optional<RecipeHolder<CraftingRecipe>> recipe = desk.recipe();
    if (recipe.isEmpty() || !desk.order().isActive()) {
      return DeskStatus.IDLE;
    }
    List<Ingredient> grid = DeskGrid.of(recipe.get().value());
    Optional<DeskMaterials.Pick> pick = DeskMaterials.pick(grid, desk.input(), desk.slots());
    if (pick.isEmpty() && !redstone) {
      DeskCascade.pull(grid, desk.input(), desk.output(), desk.slots());
      pick = DeskMaterials.pick(grid, desk.input(), desk.slots());
    }
    Optional<DeskCraft> plan =
        pick.flatMap(found -> DeskCraft.of(recipe.get().value(), found, level));
    boolean fits =
        plan.map(found -> DeskOutput.fits(desk.output(), desk.slots(), found.outputs()))
            .orElse(false);
    DeskCheck check =
        new DeskCheck(true, redstone, pick.isPresent(), plan.isPresent(), desk.canPayCraft(), fits);
    DeskStatus status = DeskStatus.decide(check);
    if (status.isWorking() && desk.advance()) {
      finish(desk, plan.orElseThrow());
    }
    return status;
  }

  private static void finish(EngineeringDeskBlockEntity desk, DeskCraft plan) {
    DeskMaterials.remove(desk.input(), plan.pick().taken());
    DeskOutput.insert(desk.output(), desk.slots(), plan.outputs());
    desk.crafted();
  }
}
