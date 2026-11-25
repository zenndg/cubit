package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ServerFrontTest {
  private static final long CAPACITY = 1_200_000;
  private static final long HALF = CAPACITY / 2;
  private static final long ALMOST_FULL = CAPACITY - 1;
  private static final int HALF_OF_THE_BARS = 5;
  private static final int CHANNELS = 4;

  @Test
  void eachBarIsTenPerCent() {
    assertEquals(HALF_OF_THE_BARS, ServerFront.bars(HALF, CAPACITY));
    assertEquals(ServerFront.BARS, ServerFront.bars(CAPACITY, CAPACITY));
    assertEquals(ServerFront.BARS - 1, ServerFront.bars(ALMOST_FULL, CAPACITY));
  }

  @Test
  void aboveZeroAtLeastOneBarIsLit() {
    assertEquals(1, ServerFront.bars(1, CAPACITY));
    assertEquals(0, ServerFront.bars(0, CAPACITY));
    assertEquals(0, ServerFront.bars(1, 0));
  }

  @Test
  void theChannelsOfTheTierAreFreeAndTheOthersClosed() {
    assertEquals(ServerFront.Lamp.FREE, ServerFront.lamp(CHANNELS - 1, CHANNELS, true));
    assertEquals(ServerFront.Lamp.CLOSED, ServerFront.lamp(CHANNELS, CHANNELS, true));
  }

  @Test
  void withoutEnergyTheOpenLampsAreOff() {
    assertEquals(ServerFront.Lamp.OFF, ServerFront.lamp(0, CHANNELS, false));
    assertEquals(ServerFront.Lamp.CLOSED, ServerFront.lamp(CHANNELS, CHANNELS, false));
  }
}
