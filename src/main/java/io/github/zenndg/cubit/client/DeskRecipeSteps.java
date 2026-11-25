package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.desk.RecipeTrail;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.Optional;
import java.util.function.IntConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/**
 * Shift and a click on a part of the grid choose its recipe, and an arrow goes back (answers 2470,
 * 2475 and 2478). The steps live only while the screen is open, and choosing anywhere else forgets
 * them. The server checks each choice.
 */
final class DeskRecipeSteps {
  private static final int NO_RECIPE = -1;

  private final EngineeringDeskMenu menu;
  private final DeskCatalog catalog;
  private final IntConsumer send;
  private final IntConsumer reveal;
  private RecipeTrail trail = RecipeTrail.EMPTY;
  private int seen = NO_RECIPE;
  private int expected = NO_RECIPE;

  DeskRecipeSteps(
      EngineeringDeskMenu menu, DeskCatalog catalog, IntConsumer send, IntConsumer reveal) {
    this.menu = menu;
    this.catalog = catalog;
    this.send = send;
    this.reveal = reveal;
  }

  /** The first recipe of the catalog that makes an item of the mod, other than the chosen one. */
  Optional<Integer> recipeOf(ItemStack mark) {
    String namespace = BuiltInRegistries.ITEM.getKey(mark.getItem()).getNamespace();
    if (mark.isEmpty() || !CubitMod.MOD_ID.equals(namespace)) {
      return Optional.empty();
    }
    var all = catalog.recipes();
    for (int place = 0; place < all.size(); place++) {
      if (DeskCatalog.resultOf(all.get(place)).is(mark.getItem())) {
        return place == menu.recipeIndex() ? Optional.empty() : Optional.of(place);
      }
    }
    return Optional.empty();
  }

  void open(ItemStack mark) {
    if (menu.holdsRecipe()) {
      return;
    }
    recipeOf(mark)
        .ifPresent(place -> {
          trail = trail.leaving(menu.recipeIndex());
          goTo(place);
        });
  }

  boolean canGoBack() {
    return !trail.isEmpty() && !menu.holdsRecipe();
  }

  void back() {
    if (canGoBack()) {
      int place = trail.last().orElseThrow();
      trail = trail.back();
      goTo(place);
    }
  }

  private void goTo(int place) {
    expected = place;
    send.accept(DeskControl.choose(place));
    reveal.accept(place);
  }

  /** A recipe that came from the catalog, the grid, JEI or another player ends the steps. */
  void tick() {
    int now = menu.recipeIndex();
    if (now == seen) {
      return;
    }
    seen = now;
    if (now != expected) {
      trail = RecipeTrail.EMPTY;
      expected = now;
    }
  }
}
