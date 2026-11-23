package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DockPanelTest {
  private static final int DELAY = 3;
  private static final int MOTION = 2;
  private static final DockPanel.Timings TIMINGS = new DockPanel.Timings(DELAY, MOTION);

  private static DockPanel run(DockPanel panel, boolean sun, int ticks) {
    DockPanel now = panel;
    for (int tick = 0; tick < ticks; tick++) {
      now = now.tick(sun, TIMINGS);
    }
    return now;
  }

  @Test
  void aStoredPanelStartsDeployingAfterTheDelayInTheSun() {
    assertEquals(DockPanelState.STORED, run(DockPanel.STORED, true, DELAY - 1).state());
    assertEquals(DockPanelState.DEPLOYING, run(DockPanel.STORED, true, DELAY).state());
  }

  /** A passing cloud does not open the cabinet: the streak starts again (answer 1856). */
  @Test
  void aBreakInTheSunRestartsTheDelay() {
    DockPanel almost = run(DockPanel.STORED, true, DELAY - 1);
    DockPanel broken = almost.tick(false, TIMINGS);
    assertEquals(DockPanelState.STORED, run(broken, true, DELAY - 1).state());
  }

  @Test
  void deployingEndsDeployedAfterTheMotion() {
    DockPanel deploying = run(DockPanel.STORED, true, DELAY);
    assertEquals(DockPanelState.DEPLOYING, run(deploying, true, MOTION - 1).state());
    assertEquals(DockPanelState.DEPLOYED, run(deploying, true, MOTION).state());
  }

  /** The motion runs to its end, even if the sun goes meanwhile. */
  @Test
  void theMotionIsNotCutByTheSky() {
    DockPanel deploying = run(DockPanel.STORED, true, DELAY);
    assertEquals(DockPanelState.DEPLOYED, run(deploying, false, MOTION).state());
  }

  /** At night or without sun, the panel goes back to the cabinet (answer 1862). */
  @Test
  void aDeployedPanelRetractsAfterTheDelayWithoutSun() {
    DockPanel deployed = DockPanel.saved(true);
    assertEquals(DockPanelState.DEPLOYED, run(deployed, false, DELAY - 1).state());
    DockPanel retracting = run(deployed, false, DELAY);
    assertEquals(DockPanelState.RETRACTING, retracting.state());
    assertEquals(DockPanelState.STORED, run(retracting, false, MOTION).state());
  }

  @Test
  void onlyAFullyDeployedPanelGenerates() {
    assertTrue(DockPanel.saved(true).generates());
    assertFalse(DockPanel.STORED.generates());
    assertFalse(run(DockPanel.STORED, true, DELAY).generates());
  }

  /** Only the destination is saved, so a load mid motion shows the end of it (answer 1864). */
  @Test
  void theSavedValueIsTheDestination() {
    DockPanel deploying = run(DockPanel.STORED, true, DELAY);
    assertTrue(deploying.deployedAtTheEnd());
    assertEquals(DockPanelState.DEPLOYED, DockPanel.saved(deploying.deployedAtTheEnd()).state());
    DockPanel retracting = run(DockPanel.saved(true), false, DELAY);
    assertFalse(retracting.deployedAtTheEnd());
  }

  @Test
  void invalidTimingsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new DockPanel.Timings(-1, MOTION));
    assertThrows(IllegalArgumentException.class, () -> new DockPanel.Timings(DELAY, -1));
  }
}
