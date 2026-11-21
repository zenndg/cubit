package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DeskLookTest {
  @Test
  void withoutAnOrderTheDeskLooksIdle() {
    assertEquals(DeskLook.IDLE, DeskLook.of(DeskStatus.IDLE));
  }

  @Test
  void craftingLooksLikeCrafting() {
    assertEquals(DeskLook.CRAFTING, DeskLook.of(DeskStatus.CRAFTING));
  }

  @Test
  void everyCauseOfPauseLooksPaused() {
    for (DeskStatus status : DeskStatus.values()) {
      if (status.isPaused()) {
        assertEquals(DeskLook.PAUSED, DeskLook.of(status));
      }
    }
  }

  @Test
  void onlyCraftingLightsTheRoom() {
    assertTrue(DeskLook.CRAFTING.lightsRoom());
    assertFalse(DeskLook.PAUSED.lightsRoom());
    assertFalse(DeskLook.IDLE.lightsRoom());
  }

  @Test
  void craftingAndPauseHaveAnOrder() {
    assertTrue(DeskLook.CRAFTING.hasOrder());
    assertTrue(DeskLook.PAUSED.hasOrder());
    assertFalse(DeskLook.IDLE.hasOrder());
  }
}
