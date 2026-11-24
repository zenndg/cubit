package io.github.zenndg.cubit.domain.dock;

import java.util.Locale;
import java.util.Optional;

/** What a Dock does with the XP it gets (answers 1831 and 2100). The keys are a save contract. */
public enum DockXpMode {
  DELETE_ALL,
  STORE_DELETE_EXCESS,
  STORE_CONVERT_EXCESS,
  CONVERT_ALL;

  /** Of a new Dock, and of one from before the XP (answer 1832). */
  public static final DockXpMode INITIAL = STORE_CONVERT_EXCESS;

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Optional<DockXpMode> fromKey(String key) {
    for (DockXpMode mode : values()) {
      if (mode.key().equals(key)) {
        return Optional.of(mode);
      }
    }
    return Optional.empty();
  }

  boolean converts() {
    return this == STORE_CONVERT_EXCESS || this == CONVERT_ALL;
  }
}
