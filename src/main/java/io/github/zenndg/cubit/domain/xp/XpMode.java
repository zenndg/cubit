package io.github.zenndg.cubit.domain.xp;

import java.util.Locale;
import java.util.Optional;

/** What a Cubit does with the XP (answer 1827). The keys are a contract of the save. */
public enum XpMode {
  IGNORE,
  STORE,
  RETURN_TO_DOCK;

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Optional<XpMode> fromKey(String key) {
    for (XpMode mode : values()) {
      if (mode.key().equals(key)) {
        return Optional.of(mode);
      }
    }
    return Optional.empty();
  }
}
