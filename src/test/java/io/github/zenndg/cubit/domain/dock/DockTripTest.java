package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.dock.DockTrip.Action;
import org.junit.jupiter.api.Test;

class DockTripTest {
  private static final int STUCK_LIMIT = 3;
  private static final double FAR = 10;
  private static final double SMALL_ADVANCE = 9.95;
  private static final double REAL_ADVANCE = 9.8;
  private static final double AT_THE_ENTRANCE = 1.5;
  private static final double OUTSIDE_THE_ENTRANCE = 1.51;
  private static final int NEGATIVE = -1;
  private static final DockTrip.Entrance FREE = new DockTrip.Entrance(false, true);
  private static final DockTrip.Entrance BUSY = new DockTrip.Entrance(true, true);
  private static final DockTrip.Entrance WALLED = new DockTrip.Entrance(false, false);

  private static DockTrip stuckFor(int ticks) {
    DockTrip trip = DockTrip.START;
    for (int tick = 0; tick < ticks; tick++) {
      trip = trip.advance(FAR, STUCK_LIMIT, FREE).trip();
    }
    return trip;
  }

  @Test
  void atTheEntranceTheRobotArrives() {
    assertEquals(
        Action.ARRIVE, DockTrip.START.advance(AT_THE_ENTRANCE, STUCK_LIMIT, FREE).action());
  }

  @Test
  void justOutsideTheEntranceTheRobotWalks() {
    assertEquals(
        Action.WALK, DockTrip.START.advance(OUTSIDE_THE_ENTRANCE, STUCK_LIMIT, FREE).action());
  }

  @Test
  void arrivingDoesNotCountAsTimeStuck() {
    DockTrip stuck = stuckFor(STUCK_LIMIT);

    assertEquals(stuck, stuck.advance(AT_THE_ENTRANCE, STUCK_LIMIT, FREE).trip());
  }

  @Test
  void upToTheLimitTheRobotKeepsWalking() {
    assertEquals(Action.WALK, stuckFor(STUCK_LIMIT - 1).advance(FAR, STUCK_LIMIT, FREE).action());
  }

  @Test
  void pastTheLimitARobotHeadingToAFreeDockGivesUp() {
    assertEquals(Action.GIVE_UP, stuckFor(STUCK_LIMIT).advance(FAR, STUCK_LIMIT, FREE).action());
  }

  @Test
  void pastTheLimitARobotHeadingToABusyDockWaitsWhereItIs() {
    assertEquals(Action.ARRIVE, stuckFor(STUCK_LIMIT).advance(FAR, STUCK_LIMIT, BUSY).action());
  }

  /** Beside the dock but with a block between, it never goes in (answer 2091). */
  @Test
  void throughABlockTheRobotNeverArrives() {
    assertEquals(
        Action.WALK, DockTrip.START.advance(AT_THE_ENTRANCE, STUCK_LIMIT, WALLED).action());
    DockTrip trip = DockTrip.START;
    Action last = Action.WALK;
    for (int tick = 0; tick <= STUCK_LIMIT; tick++) {
      DockTrip.Step step = trip.advance(AT_THE_ENTRANCE, STUCK_LIMIT, WALLED);
      trip = step.trip();
      last = step.action();
    }
    assertEquals(Action.GIVE_UP, last);
  }

  @Test
  void aRealAdvanceStartsTheCountAgain() {
    DockTrip.Step step = stuckFor(STUCK_LIMIT).advance(REAL_ADVANCE, STUCK_LIMIT, FREE);

    assertEquals(Action.WALK, step.action());
    assertEquals(new DockTrip(REAL_ADVANCE, 1), step.trip());
  }

  @Test
  void aTinyAdvanceDoesNotCount() {
    assertEquals(
        Action.GIVE_UP, stuckFor(STUCK_LIMIT).advance(SMALL_ADVANCE, STUCK_LIMIT, FREE).action());
  }

  @Test
  void aFreeDockIsEnteredOnlyByTheNextInLine() {
    assertTrue(DockTrip.entersOnArrival(false, true));
    assertFalse(DockTrip.entersOnArrival(false, false));
  }

  @Test
  void aBusyDockIsNeverEntered() {
    assertFalse(DockTrip.entersOnArrival(true, true));
  }

  @Test
  void invalidValuesAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new DockTrip(FAR, NEGATIVE));
    assertThrows(IllegalArgumentException.class, () -> new DockTrip(Double.NaN, 0));
  }
}
