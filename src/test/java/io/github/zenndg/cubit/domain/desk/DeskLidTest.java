package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DeskLidTest {
  private static final int NOBODY = 0;
  private static final int ONE_VIEWER = 1;

  @Test
  void anIdleDeskWithNobodyKeepsTheLidClosed() {
    assertFalse(DeskLid.isOpen(NOBODY, DeskLook.IDLE));
  }

  @Test
  void aViewerOpensTheLid() {
    assertTrue(DeskLid.isOpen(ONE_VIEWER, DeskLook.IDLE));
  }

  @Test
  void anOrderKeepsTheLidOpenWhileCraftingOrPaused() {
    assertTrue(DeskLid.isOpen(NOBODY, DeskLook.CRAFTING));
    assertTrue(DeskLid.isOpen(NOBODY, DeskLook.PAUSED));
  }
}
