package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FollowStuckTest {
  private static final int LIMIT = 100;
  private static final int PERIOD = 10;
  private static final double FAR = 8;
  private static final double CLOSER = 7;

  private static FollowStuck walled(int ticks) {
    FollowStuck stuck = FollowStuck.START;
    for (int tick = 0; tick <= ticks; tick += PERIOD) {
      stuck = stuck.update(false, FAR, PERIOD);
    }
    return stuck;
  }

  /** Without a way to the owner and without getting closer, it is stuck (answer 2092). */
  @Test
  void itIsStuckAfterTheLimitWithoutAWay() {
    assertFalse(walled(LIMIT - PERIOD).isStuck(LIMIT));
    assertTrue(walled(LIMIT).isStuck(LIMIT));
  }

  @Test
  void aWayOrAnAdvanceStartsTheCountAgain() {
    assertFalse(walled(LIMIT).update(true, FAR, PERIOD).isStuck(LIMIT));
    assertFalse(walled(LIMIT).update(false, CLOSER, PERIOD).isStuck(LIMIT));
  }

  /** Once each time, or an owner on top of a pillar would make it jump around without end. */
  @Test
  void itAppearsOnceUntilItHasAWayAgain() {
    FollowStuck spent = walled(LIMIT).appeared();
    for (int tick = 0; tick <= LIMIT; tick += PERIOD) {
      spent = spent.update(false, FAR, PERIOD);
    }
    assertFalse(spent.isStuck(LIMIT));

    FollowStuck again = spent.update(true, FAR, PERIOD);
    for (int tick = 0; tick <= LIMIT; tick += PERIOD) {
      again = again.update(false, FAR, PERIOD);
    }
    assertTrue(again.isStuck(LIMIT));
  }

  @Test
  void anImpossibleCountIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new FollowStuck(Double.NaN, 0, false));
    assertThrows(IllegalArgumentException.class, () -> new FollowStuck(FAR, -1, false));
  }
}
