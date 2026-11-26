package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class AntennaSpecTest {
  private static final Energy CAPACITY = new Energy(600_000);
  private static final Energy INPUT = new Energy(5_000);
  private static final Energy STANDBY = new Energy(1);
  private static final Energy CHUNK = new Energy(25);
  private static final Energy DISTANCE = new Energy(2);
  private static final int LOSS = 24_000;
  private static final int HALF = 50;
  private static final Energy ALL_OF_IT = new Energy(28);
  private static final Energy WITHOUT_CHUNK = new Energy(3);
  private static final AntennaSpec SPEC =
      new AntennaSpec(CAPACITY, INPUT, STANDBY, UpsReserve.of(HALF), LOSS);

  @Test
  void itSpendsForBeingOnTheDistanceAndTheChunk() {
    assertEquals(ALL_OF_IT, SPEC.spending(true, true, true, CHUNK, DISTANCE));
    assertEquals(WITHOUT_CHUNK, SPEC.spending(true, true, false, CHUNK, DISTANCE));
  }

  @Test
  void offOrStoppedItSpendsNothing() {
    assertEquals(Energy.ZERO, SPEC.spending(false, true, true, CHUNK, DISTANCE));
    assertEquals(Energy.ZERO, SPEC.spending(true, false, true, CHUNK, DISTANCE));
  }

  @Test
  void itNeedsABatteryAndATimeWithoutSignal() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new AntennaSpec(Energy.ZERO, INPUT, STANDBY, UpsReserve.NONE, LOSS));
    assertThrows(
        IllegalArgumentException.class,
        () -> new AntennaSpec(CAPACITY, INPUT, STANDBY, UpsReserve.NONE, 0));
  }
}
