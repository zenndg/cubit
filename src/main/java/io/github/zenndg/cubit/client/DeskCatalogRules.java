package io.github.zenndg.cubit.client;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/** What a catalog of the desk shows of each recipe, because each tab has its own rule (2038). */
interface DeskCatalogRules {
  /** How an item shows: as it is, dark, or dark with a padlock. */
  enum Look {
    READY,
    DARK,
    LOCKED
  }

  boolean shows(RecipeHolder<CraftingRecipe> recipe);

  /** The chosen recipe, as its place in the whole catalog. */
  int chosen();

  /** The place is the one in the whole catalog. */
  Look look(int place, RecipeHolder<CraftingRecipe> recipe);

  /** The lines under the name, with why it has a padlock or cannot be chosen now. */
  List<Component> hints(int place, RecipeHolder<CraftingRecipe> recipe);
}
