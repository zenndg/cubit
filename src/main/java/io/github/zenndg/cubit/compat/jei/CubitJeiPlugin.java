package io.github.zenndg.cubit.compat.jei;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.registry.CubitItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * Found by JEI through its annotation, so nothing in the mod references this package and the mod
 * opens without JEI, as with Jade (rule 24, answers 1461, 1486 and 1534). The recipes of the mod
 * left the crafting table for the desk, and this plugin shows them again, all of them, locked or
 * not (answer 1532).
 */
@JeiPlugin
public final class CubitJeiPlugin implements IModPlugin {
  private static final String PATH = "jei_plugin";
  private static final ResourceLocation UID =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH);

  @Override
  public ResourceLocation getPluginUid() {
    return UID;
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registration) {
    registration.addRecipeCategories(
        new DeskRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registration) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level != null) {
      registration.addRecipes(
          JeiDeskRecipes.TYPE, DeskRecipes.all(minecraft.level.getRecipeManager()));
    }
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(CubitItems.ENGINEERING_DESK.get(), JeiDeskRecipes.TYPE);
  }

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    registration.addRecipeTransferHandler(
        new DeskTransferHandler(registration.getTransferHelper()), JeiDeskRecipes.TYPE);
  }
}
