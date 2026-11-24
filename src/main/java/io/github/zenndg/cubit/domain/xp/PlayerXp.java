package io.github.zenndg.cubit.domain.xp;

/**
 * The XP of a player in whole points: the level and the points of its bar, by the numbers of the
 * game, so a trade with a tank never loses or makes a point (answer 2076 and rule 13).
 */
public record PlayerXp(int level, int pointsInto) {
  private static final int FIRST_RANGE = 15;
  private static final int SECOND_RANGE = 30;
  private static final int FIRST_BASE = 7;
  private static final int FIRST_STEP = 2;
  private static final int SECOND_BASE = 37;
  private static final int SECOND_STEP = 5;
  private static final int THIRD_BASE = 112;
  private static final int THIRD_STEP = 9;
  private static final String INVALID = "Invalid XP of a player: ";
  private static final String AT_LEVEL = " at level ";

  public PlayerXp {
    if (level < 0 || pointsInto < 0 || pointsInto >= neededAt(level)) {
      throw new IllegalArgumentException(INVALID + pointsInto + AT_LEVEL + level);
    }
  }

  /**
   * From the level and the bar of the game, a fraction that rounding may leave just under a whole
   * level, which is then the next one with an empty bar.
   */
  public static PlayerXp of(int level, float bar) {
    int safe = Math.max(0, level);
    int needed = neededAt(safe);
    int into = Math.clamp(Math.round(bar * needed), 0, needed);
    return into == needed ? new PlayerXp(safe + 1, 0) : new PlayerXp(safe, into);
  }

  /** The points from a level to the next one. */
  public static int neededAt(int level) {
    long needed;
    if (level >= SECOND_RANGE) {
      needed = THIRD_BASE + (long) (level - SECOND_RANGE) * THIRD_STEP;
    } else if (level >= FIRST_RANGE) {
      needed = SECOND_BASE + (long) (level - FIRST_RANGE) * SECOND_STEP;
    } else {
      needed = FIRST_BASE + (long) level * FIRST_STEP;
    }
    return (int) Math.min(needed, Integer.MAX_VALUE);
  }

  /** What is missing to go up a level. */
  public int toNextLevel() {
    return neededAt(level) - pointsInto;
  }

  /** The part of this level or, with an empty bar, the whole one before. */
  public int toLevelDown() {
    if (pointsInto > 0) {
      return pointsInto;
    }
    return level == 0 ? 0 : neededAt(level - 1);
  }

  /** Every point since level zero, to compare before and after a trade. */
  public long total() {
    return pointsInto
        + sum(Math.min(level, FIRST_RANGE), FIRST_BASE, FIRST_STEP)
        + sum(within(FIRST_RANGE, SECOND_RANGE), SECOND_BASE, SECOND_STEP)
        + sum(Math.max(0, level - SECOND_RANGE), THIRD_BASE, THIRD_STEP);
  }

  private int within(int from, int to) {
    return Math.max(0, Math.min(level, to) - from);
  }

  private static long sum(long levels, int base, int step) {
    return levels * base + levels * (levels - 1) / 2 * step;
  }
}
