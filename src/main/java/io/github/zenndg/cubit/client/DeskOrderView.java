package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.DeskShortage;
import io.github.zenndg.cubit.domain.desk.DeskOrder;
import io.github.zenndg.cubit.domain.desk.DeskOrderGate;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Whether the plus of the desk may start an order, and why not: what the desk misses (answers 1988
 * and 2400). It only shows: the server checks again.
 */
final class DeskOrderView {
  private final EngineeringDeskMenu menu;
  private final DeskCatalog catalog;

  DeskOrderView(EngineeringDeskMenu menu, DeskCatalog catalog) {
    this.menu = menu;
    this.catalog = catalog;
  }

  private Optional<RecipeHolder<CraftingRecipe>> chosen() {
    var all = catalog.recipes();
    int index = menu.recipeIndex();
    return index < 0 || index >= all.size() ? Optional.empty() : Optional.of(all.get(index));
  }

  boolean mayRaise() {
    return chosen()
        .map(recipe ->
            new DeskOrderGate(menu.shortage(recipe).isEmpty()).allows(menu.total() > menu.done()))
        .orElse(false);
  }

  /** What is missing for what is left of the order, or for one item without an order (1997). */
  DeskShortage shortage() {
    int units = Math.max(DeskOrder.STEP, menu.total() - menu.done());
    return chosen().map(recipe -> menu.shortage(recipe, units)).orElse(DeskShortage.NONE);
  }

  List<Component> reasons() {
    return missing(shortage());
  }

  /** One line for each missing item, with how many. */
  private static List<Component> missing(DeskShortage shortage) {
    Map<String, Integer> counts = new LinkedHashMap<>();
    for (DeskShortage.Lack lack : shortage.lacks().values()) {
      ItemStack[] items = lack.mark().getItems();
      if (items.length > 0) {
        counts.merge(items[0].getHoverName().getString(), lack.count(), Integer::sum);
      }
    }
    List<Component> lines = new ArrayList<>();
    counts.forEach((name, count) ->
        lines.add(
            Component.translatable(DeskScreenKeys.MISSING, count, name)
                .withStyle(ChatFormatting.RED)));
    return lines;
  }
}
