package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SecondClickTest {
  private static final String ANTENNA = "antenna";
  private static final String OTHER = "other";
  private static final long NOW = 1_000;
  private static final SecondClick<String> ASKED = new SecondClick<>(ANTENNA, NOW);
  private static final long LAST = NOW + SecondClick.CONFIRM_TICKS;

  @Test
  void theSecondClickOnTheSameTargetWithinFiveSecondsConfirms() {
    assertTrue(ASKED.confirmedBy(ANTENNA, NOW + 1));
    assertTrue(ASKED.confirmedBy(ANTENNA, LAST));
  }

  @Test
  void tooLateTooEarlyOrOnAnotherTargetItDoesNot() {
    assertFalse(ASKED.confirmedBy(ANTENNA, LAST + 1));
    assertFalse(ASKED.confirmedBy(ANTENNA, NOW - 1));
    assertFalse(ASKED.confirmedBy(OTHER, NOW + 1));
  }
}
