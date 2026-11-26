package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AntennaSignalTest {
  private static final int LOSS = 24_000;
  private static final int MINUTES = 20;
  private static final int ONE_MINUTE = 1_200;
  private static final int ALMOST = LOSS - 1;

  @Test
  void withTheSignalItIsLinked() {
    assertEquals(AntennaSignal.State.LINKED, AntennaSignal.LINKED.state(LOSS));
    assertEquals(AntennaSignal.LINKED, new AntennaSignal(ALMOST).next(true, LOSS));
  }

  @Test
  void withoutItItCountsAndThenStops() {
    AntennaSignal lost = AntennaSignal.LINKED.next(false, LOSS);
    assertEquals(AntennaSignal.State.NO_SIGNAL, lost.state(LOSS));
    assertEquals(MINUTES, lost.minutesLeft(LOSS));
    AntennaSignal stopped = new AntennaSignal(ALMOST).next(false, LOSS);
    assertEquals(AntennaSignal.State.STOPPED, stopped.state(LOSS));
    assertEquals(stopped, stopped.next(false, LOSS));
    assertFalse(AntennaSignal.State.STOPPED.works());
    assertTrue(AntennaSignal.State.NO_SIGNAL.works());
  }

  @Test
  void theMinutesRoundUp() {
    assertEquals(1, new AntennaSignal(LOSS - ONE_MINUTE + 1).minutesLeft(LOSS));
    assertEquals(0, new AntennaSignal(LOSS).minutesLeft(LOSS));
  }

  @Test
  void theTicksAreNeverNegative() {
    assertThrows(IllegalArgumentException.class, () -> new AntennaSignal(-1));
  }
}
