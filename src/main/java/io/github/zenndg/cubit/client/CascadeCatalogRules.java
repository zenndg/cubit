package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.CascadeRecipes;
import io.github.zenndg.cubit.domain.desk.CascadeBook;
import io.github.zenndg.cubit.domain.desk.CascadePlanner;
import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The Cascade tab: an item shows as it is, also without materials, because the desk makes the
 * parts, and with a padlock only when the recipes of a pack are in a cycle (answers 2034, 2038 and
 * 2400).
 */
final class CascadeCatalogRules implements DeskCatalogRules {
  private final EngineeringDeskMenu menu;

  /** The recipes without a plan, read once per tick. */
  private Set<ResourceLocation> inCycle = Set.of();

  CascadeCatalogRules(EngineeringDeskMenu menu) {
    this.menu = menu;
  }

  void tick(List<RecipeHolder<CraftingRecipe>> recipes) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level == null) {
      return;
    }
    CascadeBook book = CascadeRecipes.book(minecraft.level, Map.of());
    inCycle =
        recipes.stream()
            .filter(this::shows)
            .filter(recipe -> isInCycle(book, recipe))
            .map(RecipeHolder::id)
            .collect(Collectors.toSet());
  }

  private static boolean isInCycle(CascadeBook book, RecipeHolder<CraftingRecipe> recipe) {
    Item item = DeskCatalog.resultOf(recipe).getItem();
    return CascadePlanner.order(book, CascadeRecipes.idOf(item)).isEmpty();
  }

  @Override
  public boolean shows(RecipeHolder<CraftingRecipe> recipe) {
    return CascadeRecipes.isFixed(recipe);
  }

  @Override
  public int chosen() {
    return menu.cascade().recipe();
  }

  @Override
  public Look look(RecipeHolder<CraftingRecipe> recipe) {
    return inCycle.contains(recipe.id()) ? Look.LOCKED : Look.READY;
  }

  @Override
  public List<Component> hints(RecipeHolder<CraftingRecipe> recipe) {
    return inCycle.contains(recipe.id())
        ? List.of(
            Component.translatable(DeskScreenKeys.cascadeStart(CascadeStart.NO_PLAN))
                .withStyle(ChatFormatting.RED))
        : List.of();
  }
}
