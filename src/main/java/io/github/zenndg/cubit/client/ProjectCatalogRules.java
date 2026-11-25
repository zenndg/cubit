package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The Project tab: dark without the materials of one, and never locked (1987 and 2400). While an
 * order holds its recipe, every other one is dark and says why (answer 2476).
 */
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
  public Look look(int place, RecipeHolder<CraftingRecipe> recipe) {
    return !isHeld(place) && menu.shortage(recipe).isEmpty() ? Look.READY : Look.DARK;
  }

  @Override
  public List<Component> hints(int place, RecipeHolder<CraftingRecipe> recipe) {
    return isHeld(place)
        ? List.of(Component.translatable(DeskScreenKeys.ORDER_HOLDS).withStyle(ChatFormatting.RED))
        : List.of();
  }

  private boolean isHeld(int place) {
    return menu.holdsRecipe() && place != chosen();
  }
}
