package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class XpFlowTest {
  private static final XpRate RATE = new XpRate(20);
  private static final int PER_TICK = 50;
  private static final int CUBIT_CAPACITY = 8_000;
  private static final int TICKS_TO_EMPTY = 160;

  /** 50 mB each tick on average, always in whole points (answer 1835). */
  @Test
  void aBaseTankEmptiesInEightSecondsAndNothingIsLost() {
    XpTank cubit = new XpTank(CUBIT_CAPACITY, CUBIT_CAPACITY);
    XpFlow flow = XpFlow.START;
    int ticks = 0;
    int moved = 0;
    while (!cubit.isEmpty(RATE)) {
      XpFlow.Step step = flow.drain(cubit, PER_TICK, RATE);
      flow = step.flow();
      cubit = step.from();
      moved += step.points();
      ticks++;
      assertEquals(CUBIT_CAPACITY, cubit.fluid() + RATE.fluidOf(moved));
    }
    assertEquals(TICKS_TO_EMPTY, ticks);
  }

  @Test
  void itGivesNoMoreThanThereIs() {
    XpFlow.Step step =
        XpFlow.START.drain(new XpTank(RATE.fluidPerPoint(), CUBIT_CAPACITY), PER_TICK, RATE);
    assertEquals(1, step.points());
    assertEquals(0, step.from().fluid());
  }

  /** Waiting never saves up for a burst. */
  @Test
  void anIdleFlowKeepsLessThanAPoint() {
    XpFlow flow = XpFlow.START;
    XpTank empty = new XpTank(0, CUBIT_CAPACITY);
    for (int tick = 0; tick < TICKS_TO_EMPTY; tick++) {
      flow = flow.drain(empty, PER_TICK, RATE).flow();
    }
    assertEquals(
        2, flow.drain(new XpTank(CUBIT_CAPACITY, CUBIT_CAPACITY), PER_TICK, RATE).points());
  }

  /** The pace of the conversion of a Dock is the same one (answers 1939 and 2104). */
  @Test
  void theBudgetCountsThePointsOfATick() {
    XpFlow.Budget first = XpFlow.START.tick(PER_TICK, RATE);
    XpFlow.Budget second = first.flow().tick(PER_TICK, RATE);
    assertEquals(2, first.points());
    assertEquals(1 + 2, second.points());
  }
}
