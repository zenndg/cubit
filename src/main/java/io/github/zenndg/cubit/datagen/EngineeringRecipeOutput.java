package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.item.DeskCategorized;
import io.github.zenndg.cubit.item.EngineeringRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Turns the recipes the game builders make into recipes of the Engineering Desk, keeping the IDs,
 * the JSON and the unlocks (answers 1438 and 1459), each in its category of the catalog (answer
 * 1521). The special recipes of the mod already run in the desk and only take the category.
 */
final class EngineeringRecipeOutput implements RecipeOutput {
  private final RecipeOutput output;

  EngineeringRecipeOutput(RecipeOutput output) {
    this.output = output;
  }

  @Override
  public Advancement.Builder advancement() {
    return output.advancement();
  }

  @Override
  public void accept(
      ResourceLocation id,
      Recipe<?> recipe,
      AdvancementHolder advancement,
      ICondition... conditions) {
    output.accept(id, toDesk(recipe, DeskCategoryData.of(id)), advancement, conditions);
  }

  private static Recipe<?> toDesk(Recipe<?> recipe, DeskCategory category) {
    if (recipe.getClass() == ShapedRecipe.class) {
      return new EngineeringRecipe((ShapedRecipe) recipe, category);
    }
    if (recipe.getClass() == ShapelessRecipe.class) {
      return new EngineeringRecipe((ShapelessRecipe) recipe, category);
    }
    if (recipe instanceof DeskCategorized categorized) {
      return categorized.withDeskCategory(category);
    }
    return recipe;
  }
}
