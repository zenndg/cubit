package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class ServerPowerTest {
  private static final Energy SOME = new Energy(1);

  @Test
  void startsWhenTheBatteryLeavesZeroAndFallsWhenItGetsThere() {
    assertEquals(ServerPower.STARTS, ServerPower.between(Energy.ZERO, SOME));
    assertEquals(ServerPower.FALLS, ServerPower.between(SOME, Energy.ZERO));
  }

  @Test
  void anythingElseIsTheSame() {
    assertEquals(ServerPower.SAME, ServerPower.between(SOME, SOME));
    assertEquals(ServerPower.SAME, ServerPower.between(Energy.ZERO, Energy.ZERO));
  }
}
