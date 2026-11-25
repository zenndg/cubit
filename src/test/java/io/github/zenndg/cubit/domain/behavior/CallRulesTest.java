package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CallRulesTest {
  private static final int TELEPORT = 20;
  private static final int SECONDS = 10;
  private static final int BLOCKS = 4;
  private static final int TICKS = 200;
  private static final CallSpec SPEC = new CallSpec(SECONDS, BLOCKS, SECONDS);
  private static final long CALLED_AT = 1_000;
  private static final UUID ROBOT = new UUID(1, 2);

  @Test
  void closeByItWalks() {
    assertEquals(CallArrival.WALKS, CallArrival.of(true, true, TELEPORT - 1, TELEPORT));
  }

  @Test
  void fromFarOrFromAnotherDimensionItAppears() {
    assertEquals(CallArrival.APPEARS, CallArrival.of(true, true, TELEPORT, TELEPORT));
    assertEquals(CallArrival.APPEARS, CallArrival.of(true, false, 0, TELEPORT));
  }

  @Test
  void withoutChargeItAlwaysAppears() {
    assertEquals(CallArrival.APPEARS, CallArrival.of(false, true, 1, TELEPORT));
  }

  @Test
  void theNextCallWaitsAndTheSecondsRoundUp() {
    assertEquals(SECONDS, SPEC.secondsLeft(CALLED_AT, CALLED_AT));
    assertEquals(SECONDS, SPEC.secondsLeft(CALLED_AT, CALLED_AT + 1));
    assertEquals(1, SPEC.secondsLeft(CALLED_AT, CALLED_AT + TICKS - 1));
    assertEquals(0, SPEC.secondsLeft(CALLED_AT, CALLED_AT + TICKS));
  }

  @Test
  void aClockThatWentBackNeverHoldsTheCalls() {
    assertEquals(0, SPEC.secondsLeft(CALLED_AT, CALLED_AT - 1));
  }

  @Test
  void withoutACooldownTheNextCallComesAtOnce() {
    assertEquals(0, new CallSpec(0, 1, 1).secondsLeft(CALLED_AT, CALLED_AT));
  }

  @Test
  void theNumbersAreChecked() {
    assertThrows(IllegalArgumentException.class, () -> new CallSpec(-1, 1, 1));
    assertThrows(IllegalArgumentException.class, () -> new CallSpec(0, 0, 1));
    assertThrows(IllegalArgumentException.class, () -> new CallSpec(0, 1, 0));
    assertEquals(TICKS, SPEC.timeoutTicks());
  }

  @Test
  void aSearchEnds() {
    CallSearch search = new CallSearch(ROBOT, CallSearch.Cause.CALL, 1);
    assertFalse(search.isOver());
    assertTrue(search.tick().isOver());
    assertTrue(search.tick().tick().isOver());
    assertThrows(
        IllegalArgumentException.class, () -> new CallSearch(ROBOT, CallSearch.Cause.RESPAWN, -1));
  }
}
