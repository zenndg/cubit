package io.github.zenndg.cubit.domain.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InputMeterTest {
  private static final Energy STEADY = new Energy(2_400);
  private static final Energy BURST = new Energy(48_000);
  private static final long AVERAGE_OF_ONE_BURST = 2_400;

  private static void recordSecond(InputMeter meter, Energy each) {
    for (int tick = 0; tick < InputMeter.TICKS_PER_SECOND; tick++) {
      meter.record(each);
    }
  }

  @Test
  void aNewMeterShowsNothing() {
    assertEquals(Energy.ZERO, new InputMeter().perTick());
  }

  @Test
  void aSteadyInputShowsItsRate() {
    InputMeter meter = new InputMeter();
    recordSecond(meter, STEADY);
    assertEquals(STEADY, meter.perTick());
  }

  @Test
  void aBurstIsSpreadOverTheSecond() {
    InputMeter meter = new InputMeter();
    meter.record(BURST);
    assertEquals(new Energy(AVERAGE_OF_ONE_BURST), meter.perTick());
  }

  @Test
  void anInputOlderThanASecondIsForgotten() {
    InputMeter meter = new InputMeter();
    meter.record(BURST);
    recordSecond(meter, Energy.ZERO);
    assertEquals(Energy.ZERO, meter.perTick());
  }
}
