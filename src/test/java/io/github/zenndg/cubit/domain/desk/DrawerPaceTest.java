package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DrawerPaceTest {
  private static final long START = 100;

  @Test
  void aDrawerThatNeverOpenedCanOpen() {
    assertTrue(DrawerPace.NEVER.allows(START));
  }

  @Test
  void aDrawerWaitsTheIntervalBeforeOpeningAgain() {
    DrawerPace opened = DrawerPace.NEVER.open(START);
    assertFalse(opened.allows(START + DrawerPace.INTERVAL_TICKS - 1));
    assertTrue(opened.allows(START + DrawerPace.INTERVAL_TICKS));
  }

  @Test
  void openingTooSoonChangesNothing() {
    DrawerPace opened = DrawerPace.NEVER.open(START);
    assertSame(opened, opened.open(START + 1));
  }

  @Test
  void openingRemembersTheTick() {
    assertEquals(START, DrawerPace.NEVER.open(START).lastOpened());
  }

  @Test
  void aClockThatWentBackAllowsOpening() {
    DrawerPace opened = DrawerPace.NEVER.open(START);
    assertTrue(opened.allows(START - 1));
  }
}
