package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PatrolSiegeTest {
  private static final double PRECISION = 1e-9;
  private static final double EAST = 0;
  private static final double TARGET_X = 10;
  private static final double TARGET_Z = -4;
  private static final double RADIUS = 2;
  private static final int ALONE = 1;
  private static final int THREE = 3;
  private static final int TWO = 2;
  private static final double BLADE_STOP = 1.8;
  private static final int SWITCH = 4;
  private static final int RANGE = 16;
  private static final double MIDDLE = 9;
  private static final double TOO_NEAR = 1;
  private static final double TOO_FAR = 30;

  @Test
  void theFirstKeepsItsSideAndTheOthersSpreadEvenly() {
    assertEquals(EAST, PatrolSiege.angle(0, THREE, EAST), PRECISION);
    assertEquals(2 * Math.PI / THREE, PatrolSiege.angle(1, THREE, EAST), PRECISION);
    assertEquals(Math.PI, PatrolSiege.angle(1, TWO, EAST), PRECISION);
  }

  @Test
  void aloneItKeepsItsOwnSide() {
    assertEquals(EAST, PatrolSiege.angle(0, ALONE, EAST), PRECISION);
  }

  @Test
  void theSpotIsOnTheCircleAroundTheTarget() {
    PatrolSiege.Spot east = PatrolSiege.spot(TARGET_X, TARGET_Z, EAST, RADIUS);
    assertEquals(TARGET_X + RADIUS, east.x(), PRECISION);
    assertEquals(TARGET_Z, east.z(), PRECISION);
    PatrolSiege.Spot west = PatrolSiege.spot(TARGET_X, TARGET_Z, Math.PI, RADIUS);
    assertEquals(TARGET_X - RADIUS, west.x(), PRECISION);
  }

  @Test
  void theBladeClosesInAndThePistolKeepsItsDistanceWithinLimits() {
    PatrolSiege.Reach reach = new PatrolSiege.Reach(BLADE_STOP, SWITCH, RANGE);
    assertEquals(BLADE_STOP, reach.radius(true, MIDDLE), PRECISION);
    assertEquals(MIDDLE, reach.radius(false, MIDDLE), PRECISION);
    assertEquals(SWITCH, reach.radius(false, TOO_NEAR), PRECISION);
    assertEquals(RANGE, reach.radius(false, TOO_FAR), PRECISION);
  }

  @Test
  void rejectsARankOutsideTheTeam() {
    assertThrows(IllegalArgumentException.class, () -> PatrolSiege.angle(THREE, THREE, EAST));
    assertThrows(IllegalArgumentException.class, () -> PatrolSiege.angle(0, 0, EAST));
  }
}
