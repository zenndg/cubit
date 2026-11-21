package io.github.zenndg.cubit.item;

import io.github.zenndg.cubit.domain.desk.DeskCategory;
import net.minecraft.world.item.crafting.Recipe;

/**
 * A recipe of the Engineering Desk with its category in the catalog (answer 1521). The datagen
 * gives each recipe of the mod its category, and a recipe without one goes to the tools.
 */
public interface DeskCategorized {
  DeskCategory deskCategory();

  /** The same recipe in another category, for the datagen. */
  Recipe<?> withDeskCategory(DeskCategory category);
}
