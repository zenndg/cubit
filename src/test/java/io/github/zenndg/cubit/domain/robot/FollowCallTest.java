package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import org.junit.jupiter.api.Test;

class FollowCallTest {
  private static final Energy CAPACITY = new Energy(1_000);
  private static final Battery CHARGED = new Battery(CAPACITY, new Energy(500));
  private static final Battery EMPTY = new Battery(CAPACITY, Energy.ZERO);

  @Test
  void aSwitchedOffCubitWithChargeCanBeCalled() {
    FollowCall call = FollowCall.of(PowerState.INACTIVE_SWITCHED_OFF, CHARGED, false);
    assertEquals(FollowCall.READY, call);
    assertTrue(call.ready());
  }

  @Test
  void aCubitInTheDockOrActiveWithoutThePlaceCanBeCalled() {
    assertEquals(FollowCall.READY, FollowCall.of(PowerState.HIBERNATING, CHARGED, false));
    assertEquals(FollowCall.READY, FollowCall.of(PowerState.ACTIVE, CHARGED, false));
  }

  @Test
  void theCubitThatFollowsNearbyAlreadySaysSo() {
    FollowCall call = FollowCall.of(PowerState.ACTIVE, CHARGED, true);
    assertEquals(FollowCall.FOLLOWING, call);
    assertFalse(call.ready());
  }

  @Test
  void withoutChargeItIsCalledAndSaysSo() {
    assertEquals(
        FollowCall.NO_CHARGE, FollowCall.of(PowerState.INACTIVE_SWITCHED_OFF, EMPTY, false));
    FollowCall exhausted = FollowCall.of(PowerState.INACTIVE_EXHAUSTED, CHARGED, true);
    assertEquals(FollowCall.NO_CHARGE, exhausted);
    assertTrue(exhausted.ready());
  }
}
