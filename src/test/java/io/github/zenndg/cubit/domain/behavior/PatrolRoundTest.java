package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PatrolRoundTest {
  private static final double SPEED = 0.6;
  private static final int MIN_TICKS = 60;
  private static final int MAX_TICKS = 100;
  private static final int MIN_DISTANCE = 12;
  private static final int MAX_DISTANCE = 20;
  private static final int BETWEEN_TICKS = 75;
  private static final int BETWEEN_DISTANCE = 15;
  private static final int FAR_BELOW = -10;
  private static final int FAR_ABOVE = 999;
  private static final PatrolSettings.Round ROUND =
      new PatrolSettings.Round(SPEED, MIN_TICKS, MAX_TICKS, MIN_DISTANCE, MAX_DISTANCE);

  @Test
  void keepsADrawWithinTheLimits() {
    assertEquals(BETWEEN_TICKS, ROUND.stopTicks(BETWEEN_TICKS));
    assertEquals(BETWEEN_DISTANCE, ROUND.stopDistance(BETWEEN_DISTANCE));
  }

  @Test
  void clampsADrawOutsideTheLimits() {
    assertEquals(MIN_TICKS, ROUND.stopTicks(FAR_BELOW));
    assertEquals(MAX_TICKS, ROUND.stopTicks(FAR_ABOVE));
    assertEquals(MIN_DISTANCE, ROUND.stopDistance(FAR_BELOW));
    assertEquals(MAX_DISTANCE, ROUND.stopDistance(FAR_ABOVE));
  }

  @Test
  void equalLimitsGiveAFixedPace() {
    PatrolSettings.Round fixed =
        new PatrolSettings.Round(SPEED, MIN_TICKS, MIN_TICKS, MIN_DISTANCE, MIN_DISTANCE);
    assertEquals(MIN_TICKS, fixed.stopTicks(FAR_ABOVE));
    assertEquals(MIN_DISTANCE, fixed.stopDistance(FAR_ABOVE));
  }

  @Test
  void rejectsAnInvalidSpeed() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings.Round(0, MIN_TICKS, MAX_TICKS, MIN_DISTANCE, MAX_DISTANCE));
  }

  @Test
  void rejectsReversedOrNegativeStops() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings.Round(SPEED, MAX_TICKS, MIN_TICKS, MIN_DISTANCE, MAX_DISTANCE));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings.Round(SPEED, FAR_BELOW, MAX_TICKS, MIN_DISTANCE, MAX_DISTANCE));
  }

  @Test
  void rejectsReversedOrNoDistance() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings.Round(SPEED, MIN_TICKS, MAX_TICKS, MAX_DISTANCE, MIN_DISTANCE));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings.Round(SPEED, MIN_TICKS, MAX_TICKS, 0, MAX_DISTANCE));
  }
}
