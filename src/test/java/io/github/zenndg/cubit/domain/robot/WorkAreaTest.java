package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.tier.AreaSpec;
import io.github.zenndg.cubit.gametest.AreaBoxes;
import org.junit.jupiter.api.Test;

class WorkAreaTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String NETHER = "minecraft:the_nether";
  private static final String BLANK = " ";
  private static final AreaCorner LOW = new AreaCorner(10, 64, -5);
  private static final AreaCorner HIGH = new AreaCorner(25, 71, 10);
  private static final WorkArea AREA = AreaBoxes.between(HIGH, LOW, OVERWORLD);
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
      AreaBoxes.between(new AreaCorner(26, 64, -5), new AreaCorner(30, 70, 0), OVERWORLD);
  private static final WorkArea APART =
      AreaBoxes.between(new AreaCorner(28, 64, -5), new AreaCorner(30, 70, 0), OVERWORLD);
  private static final WorkArea OVERLAPPING =
      AreaBoxes.between(new AreaCorner(20, 60, 0), new AreaCorner(40, 66, 20), OVERWORLD);
  private static final WorkArea IN_THE_NETHER = AreaBoxes.between(HIGH, LOW, NETHER);
  private static final WorkArea ABOVE =
      new WorkArea(AREA.shape(), HIGH.y() + 2, HIGH.y() + 2, OVERWORLD);
  private static final WorkArea RIGHT_ABOVE =
      new WorkArea(AREA.shape(), HIGH.y() + 1, HIGH.y() + 2, OVERWORLD);
  private static final double EAST_X = 30;
  private static final double SOUTH_Z = 15;
  private static final double EAST_GAP = 4;
  private static final double CORNER_GAP = Math.sqrt(32);
  private static final double PRECISION = 1e-9;
  private static final double BY_THE_WEST = 10.5;
  private static final double BY_THE_EAST = 25.5;
  private static final double BY_THE_SOUTH = 10.5;
  private static final double MID_X = 18;
  private static final int OUT = 1;
  private static final int IN = -1;

  @Test
  void theBoxAroundItGoesFromCornerToCorner() {
    assertEquals(LOW, AREA.bounds().min());
    assertEquals(HIGH, AREA.bounds().max());
    assertEquals(SIDE, AREA.bounds().width());
    assertEquals(HEIGHT, AREA.height());
    assertEquals(HEIGHT, AREA.bounds().height());
    assertEquals(SIDE, AREA.bounds().depth());
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
    assertEquals(LOW.column(), AREA.corner(0));
    assertEquals(new AreaColumn(LOW.x(), HIGH.z()), AREA.corner(LAST_CORNER));
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
  void anAreaNeedsADimensionAndHeightsInOrder() {
    AreaShape shape = AREA.shape();

    assertThrows(
        IllegalArgumentException.class, () -> new WorkArea(shape, HIGH.y(), LOW.y(), OVERWORLD));
    assertThrows(
        IllegalArgumentException.class, () -> new WorkArea(shape, LOW.y(), HIGH.y(), BLANK));
    assertThrows(IllegalArgumentException.class, () -> new AreaBounds(HIGH, LOW));
  }

  @Test
  void theScansReadEveryColumnOfIt() {
    assertEquals(SIDE * SIDE, AREA.columnCount());
    assertEquals(LOW.column(), AREA.column(0));
    assertEquals(HIGH.column(), AREA.column(AREA.columnCount() - 1));
  }

  @Test
  void aCubitOutsideHeadsToTheMiddle() {
    assertEquals(AREA.bounds().center(), AREA.heart());
  }

  @Test
  void areasOneOverTheOtherApartAreNotOne() {
    assertFalse(AREA.touches(ABOVE));
    assertTrue(AREA.touches(RIGHT_ABOVE));
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

  @Test
  void onAnEdgeTheOutsideIsAcrossIt() {
    assertEquals(new AreaOutward(IN, 0), AREA.outwardFrom(BY_THE_WEST, INSIDE_Z));
    assertEquals(new AreaOutward(0, OUT), AREA.outwardFrom(MID_X, BY_THE_SOUTH));
  }

  @Test
  void atACornerTheOutsideIsTheDiagonal() {
    assertEquals(new AreaOutward(OUT, OUT), AREA.outwardFrom(BY_THE_EAST, BY_THE_SOUTH));
  }
}
