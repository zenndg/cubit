package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/** The Project tab: dark without the materials of one, and locked by the progression (1987). */
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
    if (!DeskCatalog.unlocked(recipe)) {
      return Look.LOCKED;
    }
    return menu.shortage(recipe).isEmpty() ? Look.READY : Look.DARK;
  }

  @Override
  public List<Component> hints(RecipeHolder<CraftingRecipe> recipe) {
    return DeskCatalog.unlocked(recipe) ? List.of() : DeskOrderView.locked(menu, recipe);
  }
}
