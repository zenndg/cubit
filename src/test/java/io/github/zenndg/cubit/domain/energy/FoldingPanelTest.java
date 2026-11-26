package io.github.zenndg.cubit.domain.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FoldingPanelTest {
  private static final int DELAY = 3;
  private static final int MOTION = 2;
  private static final FoldingPanel.Timings TIMINGS = new FoldingPanel.Timings(DELAY, MOTION);

  private static FoldingPanel run(FoldingPanel panel, boolean sun, int ticks) {
    FoldingPanel now = panel;
    for (int tick = 0; tick < ticks; tick++) {
      now = now.tick(sun, TIMINGS);
    }
    return now;
  }

  @Test
  void aStoredPanelStartsDeployingAfterTheDelayInTheSun() {
    assertEquals(PanelState.STORED, run(FoldingPanel.STORED, true, DELAY - 1).state());
    assertEquals(PanelState.DEPLOYING, run(FoldingPanel.STORED, true, DELAY).state());
  }

  /** A passing cloud does not open the cabinet: the streak starts again (answer 1856). */
  @Test
  void aBreakInTheSunRestartsTheDelay() {
    FoldingPanel almost = run(FoldingPanel.STORED, true, DELAY - 1);
    FoldingPanel broken = almost.tick(false, TIMINGS);
    assertEquals(PanelState.STORED, run(broken, true, DELAY - 1).state());
  }

  @Test
  void deployingEndsDeployedAfterTheMotion() {
    FoldingPanel deploying = run(FoldingPanel.STORED, true, DELAY);
    assertEquals(PanelState.DEPLOYING, run(deploying, true, MOTION - 1).state());
    assertEquals(PanelState.DEPLOYED, run(deploying, true, MOTION).state());
  }

  /** The motion runs to its end, even if the sun goes meanwhile. */
  @Test
  void theMotionIsNotCutByTheSky() {
    FoldingPanel deploying = run(FoldingPanel.STORED, true, DELAY);
    assertEquals(PanelState.DEPLOYED, run(deploying, false, MOTION).state());
  }

  /** At night or without sun, the panel goes back to the cabinet (answer 1862). */
  @Test
  void aDeployedPanelRetractsAfterTheDelayWithoutSun() {
    FoldingPanel deployed = FoldingPanel.saved(true);
    assertEquals(PanelState.DEPLOYED, run(deployed, false, DELAY - 1).state());
    FoldingPanel retracting = run(deployed, false, DELAY);
    assertEquals(PanelState.RETRACTING, retracting.state());
    assertEquals(PanelState.STORED, run(retracting, false, MOTION).state());
  }

  @Test
  void onlyAFullyDeployedPanelGenerates() {
    assertTrue(FoldingPanel.saved(true).generates());
    assertFalse(FoldingPanel.STORED.generates());
    assertFalse(run(FoldingPanel.STORED, true, DELAY).generates());
  }

  /** Only the destination is saved, so a load mid motion shows the end of it (answer 1864). */
  @Test
  void theSavedValueIsTheDestination() {
    FoldingPanel deploying = run(FoldingPanel.STORED, true, DELAY);
    assertTrue(deploying.deployedAtTheEnd());
    assertEquals(PanelState.DEPLOYED, FoldingPanel.saved(deploying.deployedAtTheEnd()).state());
    FoldingPanel retracting = run(FoldingPanel.saved(true), false, DELAY);
    assertFalse(retracting.deployedAtTheEnd());
  }

  @Test
  void invalidTimingsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new FoldingPanel.Timings(-1, MOTION));
    assertThrows(IllegalArgumentException.class, () -> new FoldingPanel.Timings(DELAY, -1));
  }
}
