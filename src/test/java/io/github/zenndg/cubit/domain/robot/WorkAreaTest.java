package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.tier.AreaSpec;
import org.junit.jupiter.api.Test;

class WorkAreaTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String NETHER = "minecraft:the_nether";
  private static final String BLANK = " ";
  private static final AreaCorner LOW = new AreaCorner(10, 64, -5);
  private static final AreaCorner HIGH = new AreaCorner(25, 71, 10);
  private static final WorkArea AREA = WorkArea.between(HIGH, LOW, OVERWORLD);
  private static final int SIDE = 16;
  private static final int HEIGHT = 8;
  private static final AreaSpec ADVANCED = new AreaSpec(SIDE, HEIGHT);
  private static final AreaSpec TOO_LOW = new AreaSpec(SIDE, HEIGHT - 1);
  private static final int LAST_CORNER = 3;
  private static final int AROUND = 4;
  private static final double INSIDE_X = 12.5;
  private static final double ON_TOP_Y = 72.0;
  private static final double ABOVE_Y = 73.0;
  private static final double INSIDE_Z = 0.5;
  private static final double PAST_X = 26.0;
  private static final int REACH = 24;
  private static final int SHORT_REACH = 4;
  private static final WorkArea BESIDE =
      WorkArea.between(new AreaCorner(26, 64, -5), new AreaCorner(30, 70, 0), OVERWORLD);
  private static final WorkArea APART =
      WorkArea.between(new AreaCorner(28, 64, -5), new AreaCorner(30, 70, 0), OVERWORLD);
  private static final WorkArea OVERLAPPING =
      WorkArea.between(new AreaCorner(20, 60, 0), new AreaCorner(40, 66, 20), OVERWORLD);
  private static final WorkArea IN_THE_NETHER = WorkArea.between(HIGH, LOW, NETHER);
  private static final double EAST_X = 30;
  private static final double SOUTH_Z = 15;
  private static final double EAST_GAP = 4;
  private static final double CORNER_GAP = Math.sqrt(32);
  private static final double PRECISION = 1e-9;

  @Test
  void theCornersGoInAnyOrderAndMakeAVolume() {
    assertEquals(LOW, AREA.min());
    assertEquals(HIGH, AREA.max());
    assertEquals(SIDE, AREA.width());
    assertEquals(HEIGHT, AREA.height());
    assertEquals(SIDE, AREA.depth());
  }

  @Test
  void itFitsATierByItsBaseAndItsHeight() {
    assertTrue(AREA.fits(ADVANCED));
    assertFalse(AREA.fits(TOO_LOW));
    assertFalse(AREA.fits(AreaSpec.NONE));
  }

  /** One block over the top holds the items and the Cubit standing on the marked blocks. */
  @Test
  void itHoldsTheBlockOverItsTop() {
    assertTrue(AREA.contains(INSIDE_X, ON_TOP_Y, INSIDE_Z));
    assertFalse(AREA.contains(INSIDE_X, ABOVE_Y, INSIDE_Z));
    assertFalse(AREA.contains(PAST_X, ON_TOP_Y, INSIDE_Z));
  }

  @Test
  void theRoundGoesAroundTheCornersOfTheBase() {
    assertEquals(new AreaCorner(LOW.x(), LOW.y(), LOW.z()), AREA.corner(0));
    assertEquals(new AreaCorner(LOW.x(), LOW.y(), HIGH.z()), AREA.corner(LAST_CORNER));
    assertEquals(AREA.corner(0), AREA.corner(AROUND));
    assertEquals(AREA.corner(LAST_CORNER), AREA.corner(-1));
  }

  @Test
  void theCenterIsWithinTheReachOfAPlace() {
    assertTrue(AREA.centerWithin(LOW.x(), LOW.z(), REACH));
    assertFalse(AREA.centerWithin(LOW.x(), LOW.z(), SHORT_REACH));
  }

  @Test
  void itKnowsItsDimension() {
    assertTrue(AREA.isIn(OVERWORLD));
    assertFalse(AREA.isIn(NETHER));
  }

  @Test
  void anAreaNeedsADimensionAndCornersInOrder() {
    assertThrows(IllegalArgumentException.class, () -> new WorkArea(HIGH, LOW, OVERWORLD));
    assertThrows(IllegalArgumentException.class, () -> new WorkArea(LOW, HIGH, BLANK));
  }

  @Test
  void areasThatOverlapOrTouchAreOne() {
    assertTrue(AREA.touches(BESIDE));
    assertTrue(BESIDE.touches(AREA));
    assertTrue(AREA.touches(OVERLAPPING));
    assertTrue(AREA.touches(AREA));
  }

  @Test
  void areasApartOrInAnotherDimensionAreNot() {
    assertFalse(AREA.touches(APART));
    assertFalse(AREA.touches(IN_THE_NETHER));
  }

  @Test
  void theDistanceToTheBaseIsZeroInsideIt() {
    assertEquals(0, AREA.distanceToBase(INSIDE_X, INSIDE_Z), PRECISION);
    assertEquals(0, AREA.distanceToBase(PAST_X, INSIDE_Z), PRECISION);
  }

  @Test
  void theDistanceToTheBaseGoesToItsNearestEdge() {
    assertEquals(EAST_GAP, AREA.distanceToBase(EAST_X, INSIDE_Z), PRECISION);
    assertEquals(CORNER_GAP, AREA.distanceToBase(EAST_X, SOUTH_Z), PRECISION);
  }

  @Test
  void theNearestCornerOfTheBase() {
    assertEquals(0, AREA.nearestCorner(LOW.x(), LOW.z()));
    assertEquals(LAST_CORNER - 1, AREA.nearestCorner(EAST_X, SOUTH_Z));
  }
}
