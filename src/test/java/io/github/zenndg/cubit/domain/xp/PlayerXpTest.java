package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PlayerXpTest {
  /** The level, the points to the next one and every point before it. */
  private static final int[][] LEVELS = {
    {0, 7, 0},
    {1, 9, 7},
    {14, 35, 280},
    {15, 37, 315},
    {16, 42, 352},
    {29, 107, 1288},
    {30, 112, 1395},
    {31, 121, 1507},
    {40, 202, 2920},
    {100, 742, 30970},
  };

  private static final int LEVEL = 10;
  private static final int NEEDED = 27;
  private static final int INTO = 7;
  private static final int BEFORE = 25;
  private static final float HALF = 0.5f;
  private static final float ALMOST_WHOLE = 0.9999999f;
  private static final int HALF_OF_NEEDED = 14;

  /** The numbers of the game, in its three ranges of level. */
  @Test
  void theLevelsAreTheOnesOfTheGame() {
    for (int[] row : LEVELS) {
      assertEquals(row[1], PlayerXp.neededAt(row[0]));
      assertEquals(row[2], new PlayerXp(row[0], 0).total());
    }
  }

  @Test
  void itKnowsThePointsOfTheBar() {
    assertEquals(new PlayerXp(LEVEL, HALF_OF_NEEDED), PlayerXp.of(LEVEL, HALF));
    assertEquals(new PlayerXp(0, 0), PlayerXp.of(-1, -HALF));
    assertEquals(new PlayerXp(LEVEL + 1, 0), PlayerXp.of(LEVEL, ALMOST_WHOLE));
  }

  /** Taking from the tank completes the level (answer 2076). */
  @Test
  void aLevelUpIsWhatIsMissing() {
    assertEquals(NEEDED - INTO, new PlayerXp(LEVEL, INTO).toNextLevel());
    assertEquals(NEEDED, new PlayerXp(LEVEL, 0).toNextLevel());
  }

  /** Sending gives the part of the level and, with an empty bar, the whole one before (2076). */
  @Test
  void aLevelDownIsThePartOrTheOneBefore() {
    assertEquals(INTO, new PlayerXp(LEVEL, INTO).toLevelDown());
    assertEquals(BEFORE, new PlayerXp(LEVEL, 0).toLevelDown());
    assertEquals(0, new PlayerXp(0, 0).toLevelDown());
  }

  @Test
  void anImpossibleBarIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new PlayerXp(-1, 0));
    assertThrows(IllegalArgumentException.class, () -> new PlayerXp(LEVEL, -1));
    assertThrows(IllegalArgumentException.class, () -> new PlayerXp(LEVEL, NEEDED));
  }
}
