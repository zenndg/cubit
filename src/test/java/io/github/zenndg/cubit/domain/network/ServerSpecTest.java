package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import org.junit.jupiter.api.Test;

class ServerSpecTest {
  private static final Energy BATTERY = new Energy(1_200_000);
  private static final Energy INPUT = new Energy(20_000);
  private static final SolarOutput RELAY = new SolarOutput(new Energy(500), Energy.ZERO);
  private static final int CHANNELS = 4;
  private static final int SLOTS = 27;
  private static final int BROKEN_ROW = 28;
  private static final int TOO_MANY_SLOTS = 63;
  private static final int TOO_MANY_CHANNELS = 17;

  private static ServerSpec spec(Energy battery, int channels, Energy input, int slots) {
    return new ServerSpec(battery, channels, input, slots, RELAY);
  }

  @Test
  void keepsItsNumbers() {
    ServerSpec spec = spec(BATTERY, CHANNELS, INPUT, SLOTS);
    assertEquals(BATTERY, spec.capacity());
    assertEquals(SLOTS, spec.storageSlots());
  }

  @Test
  void theLargestServerIsValid() {
    ServerSpec spec = spec(BATTERY, ServerSpec.MAX_CHANNELS, INPUT, ServerSpec.MAX_SLOTS);
    assertEquals(ServerSpec.MAX_CHANNELS, spec.channels());
  }

  @Test
  void aServerWithoutBatteryOrInputIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> spec(Energy.ZERO, CHANNELS, INPUT, SLOTS));
    assertThrows(IllegalArgumentException.class, () -> spec(BATTERY, CHANNELS, Energy.ZERO, SLOTS));
  }

  @Test
  void theSlotsAreWholeRowsUpToSix() {
    assertThrows(IllegalArgumentException.class, () -> spec(BATTERY, CHANNELS, INPUT, BROKEN_ROW));
    assertThrows(
        IllegalArgumentException.class, () -> spec(BATTERY, CHANNELS, INPUT, TOO_MANY_SLOTS));
    assertThrows(IllegalArgumentException.class, () -> spec(BATTERY, CHANNELS, INPUT, 0));
  }

  @Test
  void theChannelsGoFromOneToSixteen() {
    assertThrows(IllegalArgumentException.class, () -> spec(BATTERY, 0, INPUT, SLOTS));
    assertThrows(
        IllegalArgumentException.class, () -> spec(BATTERY, TOO_MANY_CHANNELS, INPUT, SLOTS));
  }
}
