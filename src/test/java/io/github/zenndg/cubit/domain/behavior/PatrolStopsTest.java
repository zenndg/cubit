package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PatrolStopsTest {
  private static final int STOP_TICKS = 80;
  private static final int LEG = 16;
  private static final int NEXT_LEG = 12;
  private static final double STEP = 4;
  private static final double SHORT = 11.5;
  private static final double BACKWARDS = -3;
  private static final int NEGATIVE = -5;
  private static final int SEGMENTS = 5;

  @Test
  void isDueOnceTheLegIsWalked() {
    PatrolStops stops = PatrolStops.START.withLeg(LEG);
    for (int step = 0; step < LEG / STEP - 1; step++) {
      stops = stops.walked(STEP);
    }
    assertFalse(stops.due());
    assertTrue(stops.walked(STEP).due());
  }

  @Test
  void withoutALegItIsNeverDue() {
    PatrolStops stops = PatrolStops.START.walked(LEG);
    assertFalse(stops.hasLeg());
    assertFalse(stops.due());
  }

  @Test
  void aStopStartsTheCountOverWithTheNextLeg() {
    PatrolStops stops = PatrolStops.START.withLeg(LEG).walked(SHORT).stop(STOP_TICKS, NEXT_LEG);
    assertEquals(0, stops.walked());
    assertEquals(NEXT_LEG, stops.leg());
    for (int tick = 0; tick < STOP_TICKS; tick++) {
      stops = stops.tick();
    }
    assertFalse(stops.pausing());
    assertFalse(stops.walked(SHORT).due());
    assertTrue(stops.walked(NEXT_LEG).due());
  }

  @Test
  void isNotDueWhileItStops() {
    PatrolStops stops = PatrolStops.START.stop(STOP_TICKS, NEXT_LEG).walked(NEXT_LEG);
    assertTrue(stops.pausing());
    assertFalse(stops.due());
  }

  @Test
  void walkingBackwardsCountsNothing() {
    assertEquals(0, PatrolStops.START.walked(BACKWARDS).walked());
  }

  @Test
  void aNegativeLegCountsAsNone() {
    assertFalse(PatrolStops.START.withLeg(NEGATIVE).hasLeg());
  }

  @Test
  void looksLeftAndRightTwiceThenAhead() {
    PatrolStops stops = PatrolStops.START.stop(STOP_TICKS, LEG);
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
    assertEquals(PatrolStops.Gaze.AHEAD, PatrolStops.START.stop(0, LEG).gaze());
  }
}
