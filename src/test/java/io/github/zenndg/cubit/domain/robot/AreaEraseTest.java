package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AreaEraseTest {
  private static final UUID ROBOT = UUID.randomUUID();
  private static final long NOW = 1_000;
  private static final AreaErase ASKED = new AreaErase(ROBOT, NOW);
  private static final long LAST = NOW + AreaErase.CONFIRM_TICKS;

  @Test
  void theSecondClickInTimeConfirms() {
    assertTrue(ASKED.confirmedBy(ROBOT, NOW + 1));
    assertTrue(ASKED.confirmedBy(ROBOT, LAST));
  }

  @Test
  void tooLateOrOnAnotherCubitItDoesNot() {
    assertFalse(ASKED.confirmedBy(ROBOT, LAST + 1));
    assertFalse(ASKED.confirmedBy(ROBOT, NOW - 1));
    assertFalse(ASKED.confirmedBy(UUID.randomUUID(), NOW + 1));
  }
}
