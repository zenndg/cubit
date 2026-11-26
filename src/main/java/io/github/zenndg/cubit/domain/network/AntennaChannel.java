package io.github.zenndg.cubit.domain.network;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * The 16 channels of a Network Server, one per antenna, in the fixed order of their ids in English
 * (answer 2269). A tier opens the first 4, 8, 12 or 16. The player picks a free open one on the
 * screen of the antenna (answer 2309), and a placed antenna goes back to its last channel while it
 * is free (answer 2547).
 */
public enum AntennaChannel {
  BLACK,
  BLUE,
  BROWN,
  CYAN,
  GRAY,
  GREEN,
  LIGHT_BLUE,
  LIGHT_GRAY,
  LIME,
  MAGENTA,
  ORANGE,
  PINK,
  PURPLE,
  RED,
  WHITE,
  YELLOW;

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  /** A saved key, or nothing for one a save does not know (rule 17). */
  public static Optional<AntennaChannel> of(String key) {
    return Arrays.stream(values()).filter(channel -> channel.key().equals(key)).findFirst();
  }

  public boolean isOpen(int open) {
    return ordinal() < open;
  }

  /** The last channel, while it is open and free, or else the first free open one. */
  public static Optional<AntennaChannel> pick(
      Optional<AntennaChannel> last, int open, Set<AntennaChannel> taken) {
    Optional<AntennaChannel> kept =
        last.filter(channel -> channel.isOpen(open) && !taken.contains(channel));
    return kept.isPresent() ? kept : firstFree(open, taken);
  }

  private static Optional<AntennaChannel> firstFree(int open, Set<AntennaChannel> taken) {
    return Arrays.stream(values())
        .filter(channel -> channel.isOpen(open) && !taken.contains(channel))
        .findFirst();
  }

  /**
   * The next free open channel to one side, round the open ones, for the arrows of the screen, or
   * the same one when no other is free.
   */
  public AntennaChannel step(int direction, int open, Set<AntennaChannel> taken) {
    int count = Math.max(1, Math.min(open, values().length));
    for (int moved = 1; moved < count; moved++) {
      AntennaChannel next = values()[Math.floorMod(ordinal() + direction * moved, count)];
      if (!taken.contains(next)) {
        return next;
      }
    }
    return this;
  }
}
