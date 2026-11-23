package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DockDoorLightTest {
  private static final long START = 0;
  private static final long HALF_BLINK = DockDoorLight.BLINK_TICKS / 2;

  @Test
  void theLightIsRedWithThePanelStored() {
    assertEquals(DockDoorLight.RED, DockDoorLight.of(DockPanelState.STORED));
  }

  @Test
  void theLightIsGreenWithThePanelDeployed() {
    assertEquals(DockDoorLight.GREEN, DockDoorLight.of(DockPanelState.DEPLOYED));
  }

  /** A third color only for the motion, as a machine at work (answer 1863). */
  @Test
  void theLightIsYellowWhileThePanelMoves() {
    assertEquals(DockDoorLight.YELLOW, DockDoorLight.of(DockPanelState.DEPLOYING));
    assertEquals(DockDoorLight.YELLOW, DockDoorLight.of(DockPanelState.RETRACTING));
  }

  @Test
  void theYellowLightBlinks() {
    assertTrue(DockDoorLight.YELLOW.isLit(START));
    assertFalse(DockDoorLight.YELLOW.isLit(START + HALF_BLINK));
    assertTrue(DockDoorLight.YELLOW.isLit(START + DockDoorLight.BLINK_TICKS));
  }

  @Test
  void theFixedLightsNeverBlink() {
    assertTrue(DockDoorLight.RED.isLit(START + HALF_BLINK));
    assertTrue(DockDoorLight.GREEN.isLit(START + HALF_BLINK));
  }
}
