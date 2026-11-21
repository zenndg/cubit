package io.github.zenndg.cubit.compat.jei;

import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.registry.CubitMenus;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The plus button of JEI on the screen of the desk: it picks the recipe in the catalog with the
 * same request as a click there, which the server checks, with the order at zero. No item moves,
 * and a locked recipe is refused with its reason (answers 1506 and 1533).
 */
final class DeskTransferHandler
    implements IRecipeTransferHandler<EngineeringDeskMenu, RecipeHolder<CraftingRecipe>> {
  private static final String REMOVAL = "removal";

  private final IRecipeTransferHandlerHelper helper;

  DeskTransferHandler(IRecipeTransferHandlerHelper helper) {
    this.helper = helper;
  }

  @Override
  public Class<? extends EngineeringDeskMenu> getContainerClass() {
    return EngineeringDeskMenu.class;
  }

  @Override
  public Optional<MenuType<EngineeringDeskMenu>> getMenuType() {
    return Optional.of(CubitMenus.ENGINEERING_DESK.get());
  }

  @Override
  public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
    return JeiDeskRecipes.TYPE;
  }

  /**
   * JEI 19 still calls this method, which every handler must write, and its replacement is only a
   * default that calls it back.
   */
  @SuppressWarnings(REMOVAL)
  @Override
  public IRecipeTransferError transferRecipe(
      EngineeringDeskMenu menu,
      RecipeHolder<CraftingRecipe> recipe,
      IRecipeSlotsView slots,
      Player player,
      boolean maxTransfer,
      boolean doTransfer) {
    Minecraft minecraft = Minecraft.getInstance();
    Optional<Integer> index = indexOf(minecraft, recipe);
    if (index.isEmpty()) {
      return helper.createInternalError();
    }
    if (minecraft.player == null || !minecraft.player.getRecipeBook().contains(recipe)) {
      return helper.createUserErrorWithTooltip(Component.translatable(DeskScreenKeys.LOCKED));
    }
    if (doTransfer && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(
          menu.containerId, DeskControl.choose(index.get()));
    }
    return null;
  }

  /** The place of the recipe in the whole catalog, as the screen of the desk sends it. */
  private static Optional<Integer> indexOf(
      Minecraft minecraft, RecipeHolder<CraftingRecipe> recipe) {
    if (minecraft.level == null) {
      return Optional.empty();
    }
    List<RecipeHolder<CraftingRecipe>> all = DeskRecipes.all(minecraft.level.getRecipeManager());
    for (int index = 0; index < all.size(); index++) {
      if (all.get(index).id().equals(recipe.id())) {
        return Optional.of(index);
      }
    }
    return Optional.empty();
  }
}
