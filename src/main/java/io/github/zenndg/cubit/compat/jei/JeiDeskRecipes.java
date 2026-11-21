package io.github.zenndg.cubit.compat.jei;

import io.github.zenndg.cubit.CubitMod;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/** The type of the recipes of the desk in JEI, the same id as the type of the game. */
final class JeiDeskRecipes {
  private static final String PATH = "engineering";

  static final RecipeType<RecipeHolder<CraftingRecipe>> TYPE =
      RecipeType.createRecipeHolderType(
          ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));

  private JeiDeskRecipes() {}
}
