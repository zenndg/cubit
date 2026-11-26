package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class StandbyTest {
  private static final Energy SERVER = new Energy(20);
  private static final Energy RELAY = new Energy(2);
  private static final Standby STANDBY = new Standby(SERVER, RELAY);

  @Test
  void theServerPaysForItsRelayOnlyWithAPair() {
    assertEquals(SERVER.plus(RELAY), STANDBY.total(true));
    assertEquals(SERVER, STANDBY.total(false));
    assertEquals(Energy.ZERO, STANDBY.relay(false));
    assertEquals(Energy.ZERO, Standby.NONE.total(true));
  }
}
