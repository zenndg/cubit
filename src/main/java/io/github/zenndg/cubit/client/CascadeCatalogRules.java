package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.CascadeRecipes;
import io.github.zenndg.cubit.domain.desk.CascadeBook;
import io.github.zenndg.cubit.domain.desk.CascadePlanner;
import io.github.zenndg.cubit.domain.desk.CascadeRecipe;
import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The Cascade tab: an item shows as it is with its whole chain released, also without materials,
 * because the desk makes the parts, and with a padlock otherwise (answers 2009, 2034 and 2038).
 */
final class CascadeCatalogRules implements DeskCatalogRules {
  private final EngineeringDeskMenu menu;

  /** Why each locked item is locked, read once per tick. */
  private Map<ResourceLocation, List<Component>> locked = Map.of();

  CascadeCatalogRules(EngineeringDeskMenu menu) {
    this.menu = menu;
  }

  void tick(List<RecipeHolder<CraftingRecipe>> recipes) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level == null) {
      return;
    }
    CascadeBook book = CascadeRecipes.book(minecraft.level, Map.of());
    Map<ResourceLocation, RecipeHolder<CraftingRecipe>> byId = new HashMap<>();
    recipes.forEach(recipe -> byId.put(recipe.id(), recipe));
    Map<ResourceLocation, List<Component>> found = new HashMap<>();
    for (RecipeHolder<CraftingRecipe> recipe : recipes) {
      if (shows(recipe)) {
        List<Component> why = why(book, byId, recipe);
        if (!why.isEmpty()) {
          found.put(recipe.id(), why);
        }
      }
    }
    locked = found;
  }

  private static List<Component> why(
      CascadeBook book,
      Map<ResourceLocation, RecipeHolder<CraftingRecipe>> byId,
      RecipeHolder<CraftingRecipe> recipe) {
    String item = CascadeRecipes.idOf(DeskCatalog.resultOf(recipe).getItem());
    Optional<List<String>> order = CascadePlanner.order(book, item);
    if (order.isEmpty()) {
      return List.of(
          Component.translatable(DeskScreenKeys.cascadeStart(CascadeStart.NO_PLAN))
              .withStyle(ChatFormatting.RED));
    }
    List<Component> lines = new ArrayList<>();
    for (String each : order.get()) {
      book.recipeOf(each)
          .map(CascadeRecipe::id)
          .map(ResourceLocation::parse)
          .map(byId::get)
          .filter(part -> !DeskCatalog.unlocked(part))
          .ifPresent(part ->
              lines.add(
                  Component.translatable(
                          DeskScreenKeys.CASCADE_PART_LOCKED,
                          DeskCatalog.resultOf(part).getHoverName())
                      .withStyle(ChatFormatting.RED)));
    }
    return lines;
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
    return locked.containsKey(recipe.id()) ? Look.LOCKED : Look.READY;
  }

  @Override
  public List<Component> hints(RecipeHolder<CraftingRecipe> recipe) {
    return locked.getOrDefault(recipe.id(), List.of());
  }
}
