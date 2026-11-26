package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class ServerStatusTest {
  private static final Energy CAPACITY = new Energy(1_000);
  private static final Energy RESERVE = new Energy(250);
  private static final Energy ABOVE = new Energy(251);
  private static final UpsReserve QUARTER = new UpsReserve(25);

  private static ServerStatus status(Energy charge, UpsReserve ups, boolean chunk) {
    return ServerStatus.of(charge, CAPACITY, ups, chunk);
  }

  @Test
  void withoutEnergyNothingElseMatters() {
    assertEquals(ServerStatus.NO_ENERGY, status(Energy.ZERO, QUARTER, true));
  }

  @Test
  void insideTheReserveItRunsOnTheUps() {
    assertEquals(ServerStatus.ON_UPS, status(RESERVE, QUARTER, true));
    assertEquals(ServerStatus.ON, status(ABOVE, QUARTER, true));
  }

  /** Without a reserve, or without a chunk to hold, the UPS is off (detail 23). */
  @Test
  void withoutAReserveOrAChunkItIsOn() {
    assertEquals(ServerStatus.ON, status(RESERVE, UpsReserve.NONE, true));
    assertEquals(ServerStatus.ON, status(RESERVE, QUARTER, false));
  }
}
