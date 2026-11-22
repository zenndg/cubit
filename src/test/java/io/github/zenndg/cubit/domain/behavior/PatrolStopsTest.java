package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PatrolStopsTest {
  private static final double SPEED = 0.6;
  private static final int STOP_TICKS = 80;
  private static final int STOP_DISTANCE = 16;
  private static final double STEP = 4;
  private static final double SHORT = 15.5;
  private static final double BACKWARDS = -3;
  private static final int SEGMENTS = 5;
  private static final PatrolSettings.Round ROUND =
      new PatrolSettings.Round(SPEED, STOP_TICKS, STOP_DISTANCE);

  @Test
  void stopsAfterTheDistanceWalked() {
    PatrolStops stops = PatrolStops.START;
    for (int step = 0; step < STOP_DISTANCE / STEP - 1; step++) {
      stops = stops.walked(STEP, ROUND);
    }
    assertFalse(stops.pausing());
    assertTrue(stops.walked(STEP, ROUND).pausing());
  }

  @Test
  void aStopStartsTheCountOver() {
    PatrolStops stops = PatrolStops.START.walked(SHORT, ROUND).stop(STOP_TICKS);
    for (int tick = 0; tick < STOP_TICKS; tick++) {
      stops = stops.tick();
    }
    assertFalse(stops.pausing());
    assertFalse(stops.walked(SHORT, ROUND).pausing());
  }

  @Test
  void walkingBackwardsCountsNothing() {
    assertEquals(0, PatrolStops.START.walked(BACKWARDS, ROUND).walked());
  }

  @Test
  void looksLeftAndRightTwiceThenAhead() {
    PatrolStops stops = PatrolStops.START.stop(STOP_TICKS);
    List<PatrolStops.Gaze> seen = new ArrayList<>();
    for (int segment = 0; segment < SEGMENTS; segment++) {
      seen.add(stops.gaze());
      for (int tick = 0; tick < STOP_TICKS / SEGMENTS; tick++) {
        stops = stops.tick();
      }
    }
    assertEquals(
        List.of(
            PatrolStops.Gaze.LEFT,
            PatrolStops.Gaze.RIGHT,
            PatrolStops.Gaze.LEFT,
            PatrolStops.Gaze.RIGHT,
            PatrolStops.Gaze.AHEAD),
        seen);
  }

  @Test
  void walkingLooksAhead() {
    assertEquals(PatrolStops.Gaze.AHEAD, PatrolStops.START.gaze());
    assertEquals(PatrolStops.Gaze.AHEAD, PatrolStops.START.stop(0).gaze());
  }

  @Test
  void theRoundRejectsAnInvalidPace() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings.Round(0, STOP_TICKS, STOP_DISTANCE));
    assertThrows(
        IllegalArgumentException.class, () -> new PatrolSettings.Round(SPEED, STOP_TICKS, 0));
  }
}
