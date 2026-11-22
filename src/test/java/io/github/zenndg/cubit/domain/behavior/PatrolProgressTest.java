package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PatrolProgressTest {
  private static final int SIZE = 3;
  private static final int BEYOND = 5;

  @Test
  void goesOnToTheNextPoint() {
    assertEquals(1, PatrolProgress.START.next(SIZE).index());
  }

  @Test
  void theLastPointGoesBackToTheFirst() {
    assertEquals(0, new PatrolProgress(SIZE - 1).next(SIZE).index());
  }

  @Test
  void aShrunkRouteStartsOver() {
    assertEquals(PatrolProgress.START, new PatrolProgress(BEYOND).within(SIZE));
    assertEquals(1, new PatrolProgress(1).within(SIZE).index());
  }

  @Test
  void neverGoesBelowZero() {
    assertEquals(0, new PatrolProgress(-1).index());
    assertEquals(0, PatrolProgress.START.next(0).index());
  }
}
