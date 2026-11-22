package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WingHopTest {
  private static final double FACTOR = 2;
  private static final double STRAIGHT = 10;
  private static final double SHORT_PATH = 15;
  private static final double LONG_PATH = 25;
  private static final double PRECISION = 1e-9;
  private static final double FLOOR = 64;
  private static final double WALL_TOP = 66;
  private static final double LAND = 64;
  private static final double CLEARANCE = 1;
  private static final int JUMP = 3;
  private static final int LOW_JUMP = 1;
  private static final double SPEED = 0.4;
  private static final double RISE = 0.5;
  private static final double FAR = 4;
  private static final double CLOSE = 0.2;

  @Test
  void itHopsWhenThePathDoesNotReachOrIsTooLong() {
    assertTrue(WingHop.worthHopping(false, SHORT_PATH, STRAIGHT, FACTOR));
    assertTrue(WingHop.worthHopping(true, LONG_PATH, STRAIGHT, FACTOR));
    assertFalse(WingHop.worthHopping(true, SHORT_PATH, STRAIGHT, FACTOR));
  }

  @Test
  void theFlightGoesOverTheHighestOfTheWayWithClearance() {
    assertEquals(WALL_TOP + CLEARANCE, WingHop.apex(FLOOR, LAND, WALL_TOP), PRECISION);
  }

  @Test
  void theJumpOfTheTierLimitsHowHighItGoes() {
    assertTrue(WingHop.withinJump(FLOOR, WingHop.apex(FLOOR, LAND, WALL_TOP), JUMP));
    assertFalse(WingHop.withinJump(FLOOR, WingHop.apex(FLOOR, LAND, WALL_TOP), LOW_JUMP));
  }

  @Test
  void belowTheTopItRisesAndAboveItCrossesTowardTheLanding() {
    WingHop.Motion rising = WingHop.motion(FLOOR, WALL_TOP + CLEARANCE, FAR, SPEED, RISE);
    assertEquals(RISE, rising.up(), PRECISION);
    WingHop.Motion crossing =
        WingHop.motion(WALL_TOP + CLEARANCE, WALL_TOP + CLEARANCE, FAR, SPEED, RISE);
    assertEquals(SPEED, crossing.forward(), PRECISION);
    assertFalse(crossing.rising());
  }

  @Test
  void nearTheLandingItSlowsDownToIt() {
    WingHop.Motion last =
        WingHop.motion(WALL_TOP + CLEARANCE, WALL_TOP + CLEARANCE, CLOSE, SPEED, RISE);
    assertEquals(CLOSE, last.forward(), PRECISION);
  }
}
