package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PatrolProgressTest {
  private static final int PAUSE = 2;
  private static final int SIZE = 3;
  private static final int BEYOND = 5;

  @Test
  void pausesAtThePointAndThenGoesOn() {
    PatrolProgress progress = PatrolProgress.START.arrived(PAUSE);
    assertTrue(progress.pausing());
    progress = progress.tick().tick();
    assertFalse(progress.pausing());
    assertEquals(1, progress.next(SIZE).index());
  }

  @Test
  void theLastPointGoesBackToTheFirst() {
    assertEquals(0, new PatrolProgress(SIZE - 1, 0).next(SIZE).index());
  }

  @Test
  void aShrunkRouteStartsOver() {
    assertEquals(PatrolProgress.START, new PatrolProgress(BEYOND, 0).within(SIZE));
    assertEquals(1, new PatrolProgress(1, 0).within(SIZE).index());
  }

  @Test
  void neverGoesBelowZero() {
    assertEquals(0, PatrolProgress.START.tick().pauseLeft());
    assertEquals(0, new PatrolProgress(-1, 0).index());
  }
}
