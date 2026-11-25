package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/** The Project tab: dark without the materials of one, and never locked (1987 and 2400). */
record ProjectCatalogRules(EngineeringDeskMenu menu) implements DeskCatalogRules {
  @Override
  public boolean shows(RecipeHolder<CraftingRecipe> recipe) {
    return true;
  }

  @Override
  public int chosen() {
    return menu.recipeIndex();
  }

  @Override
  public Look look(RecipeHolder<CraftingRecipe> recipe) {
    return menu.shortage(recipe).isEmpty() ? Look.READY : Look.DARK;
  }

  @Override
  public List<Component> hints(RecipeHolder<CraftingRecipe> recipe) {
    return List.of();
  }
}
