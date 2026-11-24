package io.github.zenndg.cubit.domain.desk;

import java.util.Locale;
import java.util.Optional;

/**
 * The categories of the catalog of the Engineering Desk, in the order of their icons. A recipe
 * without one goes to the tools (answers 1474, 1521 and 1522). The ids are part of the JSON of the
 * recipes, so they cannot change without a migration (rule 18).
 */
public enum DeskCategory {
  ROBOT,
  PARTS,
  ENERGY,
  DOCK,
  MODULES,
  CARDS,
  TOOLS;

  public static final DeskCategory DEFAULT = TOOLS;

  public String id() {
    return name().toLowerCase(Locale.ROOT);
  }

  /** Nothing for an id that is not a category, so the caller decides what to do with it. */
  public static Optional<DeskCategory> byId(String id) {
    for (DeskCategory category : values()) {
      if (category.id().equals(id)) {
        return Optional.of(category);
      }
    }
    return Optional.empty();
  }
}
