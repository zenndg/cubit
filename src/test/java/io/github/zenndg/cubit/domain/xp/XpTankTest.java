package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class XpTankTest {
  private static final XpRate RATE = new XpRate(20);
  private static final int CAPACITY = 8_000;
  private static final int CAPACITY_POINTS = 400;
  private static final int NEARLY_FULL = 7_995;
  private static final int OVER_CAPACITY = 9_000;
  private static final int OVER_CAPACITY_POINTS = 450;
  private static final int FIVE = 5;
  private static final int FIVE_POINTS = 100;
  private static final int ODD_FLUID = 47;

  @Test
  void aPointIsWorthTheFluidOfTheRate() {
    assertEquals(FIVE_POINTS, RATE.fluidOf(FIVE));
    assertEquals(2, RATE.pointsIn(ODD_FLUID));
    assertThrows(IllegalArgumentException.class, () -> new XpRate(0));
  }

  @Test
  void fluidAndCapacityAreNeverNegative() {
    assertThrows(IllegalArgumentException.class, () -> new XpTank(-1, CAPACITY));
    assertThrows(IllegalArgumentException.class, () -> new XpTank(0, -1));
  }

  @Test
  void itFillsWithWhatFits() {
    XpTank.Moved filled = new XpTank(0, CAPACITY).fill(CAPACITY_POINTS + FIVE, RATE);
    assertEquals(CAPACITY_POINTS, filled.points());
    assertEquals(new XpTank(CAPACITY, CAPACITY), filled.tank());
  }

  @Test
  void itGivesWholePointsAndKeepsTheRest() {
    XpTank.Moved drained = new XpTank(ODD_FLUID, CAPACITY).drain(FIVE, RATE);
    assertEquals(2, drained.points());
    assertEquals(ODD_FLUID - RATE.fluidOf(2), drained.tank().fluid());
    assertTrue(drained.tank().isEmpty(RATE));
  }

  @Test
  void nothingMovesForNoPoints() {
    XpTank tank = new XpTank(FIVE_POINTS, CAPACITY);
    assertEquals(new XpTank.Moved(tank, 0), tank.fill(0, RATE));
    assertEquals(new XpTank.Moved(tank, 0), tank.fill(-FIVE, RATE));
    assertEquals(new XpTank.Moved(tank, 0), tank.drain(-FIVE, RATE));
  }

  /** Full is when one more point does not fit (detail 3 of the concept). */
  @Test
  void itIsFullWhenAPointDoesNotFit() {
    assertTrue(new XpTank(NEARLY_FULL, CAPACITY).isFull(RATE));
    assertFalse(new XpTank(CAPACITY - RATE.fluidPerPoint(), CAPACITY).isFull(RATE));
  }

  /** A capacity the config made smaller loses nothing (answer 1937). */
  @Test
  void aSmallerCapacityKeepsTheFluid() {
    XpTank over = new XpTank(OVER_CAPACITY, CAPACITY);
    assertEquals(OVER_CAPACITY_POINTS, over.points(RATE));
    assertTrue(over.isFull(RATE));
    assertEquals(0, over.fill(1, RATE).points());
    assertEquals(OVER_CAPACITY, over.fill(1, RATE).tank().fluid());
  }

  @Test
  void aHugeAmountDoesNotOverflow() {
    XpTank.Moved filled = new XpTank(0, CAPACITY).fill(Integer.MAX_VALUE, RATE);
    assertEquals(CAPACITY_POINTS, filled.points());
    assertEquals(Integer.MAX_VALUE, RATE.fluidOf(Integer.MAX_VALUE));
  }
}
