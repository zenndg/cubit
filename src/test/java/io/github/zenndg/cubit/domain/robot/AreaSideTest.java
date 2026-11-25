package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AreaSideTest {
  private static final AreaSide ALONG_X = side(8, 3, 2, 3);
  private static final AreaSide CROSSING = side(5, 0, 5, 9);
  private static final AreaSide BESIDE = side(2, 4, 8, 4);
  private static final AreaSide APART = side(2, 5, 8, 5);
  private static final AreaColumn WEST_END = column(2, 3);
  private static final AreaColumn EAST_END = column(8, 3);
  private static final AreaColumn PAST_THE_END = column(9, 3);
  private static final AreaColumn NEXT_ROW = column(5, 4);
  private static final AreaColumn AFTER_THE_FIRST = column(7, 3);
  private static final AreaColumn AFTER_THE_NORTH = column(5, 1);
  private static final double PAST_X = 12;
  private static final double ON_Z = 3.5;
  private static final double GAP = 3;
  private static final double PRECISION = 1e-9;

  @Test
  void aSideGoesAlongOneAxis() {
    assertThrows(IllegalArgumentException.class, () -> side(0, 0, 1, 1));
    assertTrue(AreaSide.straight(column(0, 0), column(0, 1)));
    assertFalse(AreaSide.straight(column(0, 0), column(1, 1)));
  }

  @Test
  void itHoldsItsBlocksWithBothEnds() {
    assertTrue(ALONG_X.holds(WEST_END));
    assertTrue(ALONG_X.holds(EAST_END));
    assertFalse(ALONG_X.holds(PAST_THE_END));
    assertFalse(ALONG_X.holds(NEXT_ROW));
  }

  @Test
  void linesThatShareABlockMeet() {
    assertTrue(ALONG_X.meets(CROSSING));
    assertFalse(ALONG_X.meets(BESIDE));
  }

  @Test
  void linesSideBySideTouch() {
    assertTrue(ALONG_X.touches(BESIDE));
    assertTrue(ALONG_X.touches(CROSSING));
    assertFalse(ALONG_X.touches(APART));
  }

  @Test
  void theBlockAfterTheFirstGoesTowardTheEnd() {
    assertEquals(AFTER_THE_FIRST, ALONG_X.second());
    assertEquals(AFTER_THE_NORTH, CROSSING.second());
  }

  @Test
  void theDistanceIsZeroOnItsBlocks() {
    assertEquals(0, ALONG_X.distance(GAP, ON_Z), PRECISION);
    assertEquals(GAP, ALONG_X.distance(PAST_X, ON_Z), PRECISION);
  }

  private static AreaSide side(int x, int z, int toX, int toZ) {
    return new AreaSide(column(x, z), column(toX, toZ));
  }

  private static AreaColumn column(int x, int z) {
    return new AreaColumn(x, z);
  }
}
