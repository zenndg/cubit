package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class XpFlowTest {
  private static final XpRate RATE = new XpRate(20);
  private static final int PER_TICK = 50;
  private static final int CUBIT_CAPACITY = 8_000;
  private static final int DOCK_CAPACITY = 32_000;
  private static final int TICKS_TO_EMPTY = 160;
  private static final int SMALL = 60;
  private static final XpTank DOCK = new XpTank(0, DOCK_CAPACITY);

  /** 50 mB each tick on average, always in whole points (answer 1835). */
  @Test
  void aBaseTankEmptiesInEightSecondsAndNothingIsLost() {
    XpTank cubit = new XpTank(CUBIT_CAPACITY, CUBIT_CAPACITY);
    XpTank dock = DOCK;
    XpFlow flow = XpFlow.START;
    int ticks = 0;
    while (!cubit.isEmpty(RATE)) {
      XpFlow.Step step = flow.advance(cubit, dock, PER_TICK, RATE);
      flow = step.flow();
      cubit = step.from();
      dock = step.to();
      ticks++;
      assertEquals(CUBIT_CAPACITY, cubit.fluid() + dock.fluid());
      assertEquals(0, dock.fluid() % RATE.fluidPerPoint());
    }
    assertEquals(TICKS_TO_EMPTY, ticks);
  }

  @Test
  void aFullDockTakesOnlyWhatFits() {
    XpTank nearly = new XpTank(DOCK_CAPACITY - RATE.fluidPerPoint(), DOCK_CAPACITY);
    XpFlow.Step step =
        XpFlow.START.advance(new XpTank(SMALL, CUBIT_CAPACITY), nearly, PER_TICK, RATE);
    assertEquals(SMALL - RATE.fluidPerPoint(), step.from().fluid());
    assertEquals(DOCK_CAPACITY, step.to().fluid());
  }

  /** Waiting never saves up for a burst. */
  @Test
  void anIdleFlowKeepsLessThanAPoint() {
    XpFlow flow = XpFlow.START;
    XpTank empty = new XpTank(0, CUBIT_CAPACITY);
    for (int tick = 0; tick < TICKS_TO_EMPTY; tick++) {
      flow = flow.advance(empty, DOCK, PER_TICK, RATE).flow();
    }
    XpFlow.Step step =
        flow.advance(new XpTank(CUBIT_CAPACITY, CUBIT_CAPACITY), DOCK, PER_TICK, RATE);
    assertEquals(2, RATE.pointsIn(step.to().fluid()));
  }
}
