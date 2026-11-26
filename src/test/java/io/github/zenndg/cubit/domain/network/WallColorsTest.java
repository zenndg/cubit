package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WallColorsTest {
  private static final int MANY = 100;
  private static final int LAST = WallColors.COUNT - 1;
  private static final int NEGATIVE = -7;

  @Test
  void aNewChunkNeverRepeatsTheColorOfTheLastOne() {
    for (int previous = 0; previous < WallColors.COUNT; previous++) {
      Set<Integer> seen = new HashSet<>();
      for (int drawn = 0; drawn < MANY; drawn++) {
        int next = WallColors.next(previous, drawn);
        assertNotEquals(previous, next);
        seen.add(next);
      }
      assertEquals(LAST, seen.size());
    }
  }

  @Test
  void aWallJustOnTakesAnyOfTheSix() {
    Set<Integer> seen = new HashSet<>();
    for (int drawn = 0; drawn < MANY; drawn++) {
      seen.add(WallColors.next(WallColors.NONE, drawn));
    }
    assertEquals(WallColors.COUNT, seen.size());
  }

  @Test
  void anyDrawnNumberGivesAColor() {
    int next = WallColors.next(WallColors.NONE, NEGATIVE);
    assertTrue(next >= 0 && next < WallColors.COUNT);
    assertTrue(WallColors.next(LAST, NEGATIVE) < LAST);
  }
}
