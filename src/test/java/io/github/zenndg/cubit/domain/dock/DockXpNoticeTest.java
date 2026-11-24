package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.xp.XpTank;
import org.junit.jupiter.api.Test;

class DockXpNoticeTest {
  private static final int CAPACITY = 1_000;
  private static final XpTank FULL = new XpTank(CAPACITY, CAPACITY);
  private static final XpTank ALMOST = new XpTank(CAPACITY * 9 / 10, CAPACITY);
  private static final XpTank LOWER = new XpTank(CAPACITY * 9 / 10 - 1, CAPACITY);
  private static final int HUGE = Integer.MAX_VALUE;

  @Test
  void itTellsOnceWhenTheTankOverflows() {
    assertFalse(DockXpNotice.START.tells(false));
    assertTrue(DockXpNotice.START.tells(true));
    assertFalse(DockXpNotice.START.afterTelling().tells(true));
  }

  /** A tank that fills and lowers a point does not repeat it (answer 2105). */
  @Test
  void itComesBackOnlyBelowNineTenths() {
    DockXpNotice told = DockXpNotice.START.afterTelling();
    assertFalse(told.update(FULL).tells(true));
    assertFalse(told.update(ALMOST).tells(true));
    assertTrue(told.update(LOWER).tells(true));
  }

  @Test
  void aHugeTankDoesNotOverflowTheCount() {
    DockXpNotice told = DockXpNotice.START.afterTelling();
    assertFalse(told.update(new XpTank(HUGE, HUGE)).tells(true));
  }
}
