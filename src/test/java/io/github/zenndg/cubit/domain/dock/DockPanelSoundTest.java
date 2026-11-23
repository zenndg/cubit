package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class DockPanelSoundTest {
  private static final int LOCK = 5;
  private static final int BEFORE_LOCK = LOCK - 1;
  private static final int AFTER_LOCK = LOCK + 1;

  private static Optional<DockPanelSound> between(DockPanel before, DockPanel after) {
    return DockPanelSound.between(before, after, LOCK);
  }

  private static DockPanel deploying(int ticks) {
    return new DockPanel(DockPanelState.DEPLOYING, ticks);
  }

  @Test
  void theDeploySoundPlaysWhenTheArmsStartToDeploy() {
    assertEquals(Optional.of(DockPanelSound.DEPLOY), between(DockPanel.STORED, deploying(0)));
  }

  @Test
  void theRetractSoundPlaysWhenTheArmsStartToRetract() {
    DockPanel deployed = new DockPanel(DockPanelState.DEPLOYED, 0);
    DockPanel retracting = new DockPanel(DockPanelState.RETRACTING, 0);

    assertEquals(Optional.of(DockPanelSound.RETRACT), between(deployed, retracting));
  }

  /** The halves meet at the center at the tick of the lock (answer 1921). */
  @Test
  void theLockSoundPlaysOnceWhenTheHalvesMeet() {
    assertEquals(
        Optional.of(DockPanelSound.LOCK), between(deploying(BEFORE_LOCK), deploying(LOCK)));
    assertTrue(between(deploying(LOCK), deploying(AFTER_LOCK)).isEmpty());
    assertTrue(between(deploying(0), deploying(1)).isEmpty());
  }

  @Test
  void nothingPlaysWhileThePanelStays() {
    assertTrue(between(DockPanel.STORED, DockPanel.STORED).isEmpty());
    DockPanel deployed = new DockPanel(DockPanelState.DEPLOYED, 0);
    assertTrue(between(deployed, deployed).isEmpty());
  }
}
