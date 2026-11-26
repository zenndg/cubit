package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class UpsWatchTest {
  private static final Energy CAPACITY = new Energy(1_000);
  private static final Energy KEPT = new Energy(250);
  private static final Energy ABOVE = new Energy(251);
  private static final UpsReserve QUARTER = UpsReserve.of(25);

  @Test
  void warnsOnceOnTheWayDown() {
    UpsWatch.Step down = UpsWatch.OUTSIDE.next(QUARTER, KEPT, CAPACITY);
    assertTrue(down.warns());
    assertFalse(down.watch().next(QUARTER, Energy.ZERO, CAPACITY).warns());
  }

  @Test
  void warnsAgainAfterLeavingTheReserve() {
    UpsWatch inside = UpsWatch.OUTSIDE.next(QUARTER, KEPT, CAPACITY).watch();
    UpsWatch outside = inside.next(QUARTER, ABOVE, CAPACITY).watch();
    assertTrue(outside.next(QUARTER, KEPT, CAPACITY).warns());
  }

  @Test
  void aboveTheReserveOrWithoutOneItIsSilent() {
    assertFalse(UpsWatch.OUTSIDE.next(QUARTER, ABOVE, CAPACITY).warns());
    assertFalse(UpsWatch.OUTSIDE.next(UpsReserve.NONE, Energy.ZERO, CAPACITY).warns());
  }
}
