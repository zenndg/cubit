package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PatrolSettingsTest {
  private static final int SLOW_CHANCE = 20;
  private static final int SLOW_TICKS = 60;
  private static final int HEAD_CHANCE = 50;
  private static final int CHASE = 16;
  private static final int SWITCH = 4;
  private static final int PAUSE = 40;
  private static final int OVER_A_HUNDRED = 101;
  private static final PatrolSettings DEFAULTS =
      new PatrolSettings(SLOW_CHANCE, SLOW_TICKS, HEAD_CHANCE, CHASE, SWITCH, PAUSE);

  @Test
  void slowsUnderItsChance() {
    assertTrue(DEFAULTS.slows(SLOW_CHANCE - 1));
    assertFalse(DEFAULTS.slows(SLOW_CHANCE));
  }

  @Test
  void dropsAHeadUnderItsChance() {
    assertTrue(DEFAULTS.dropsHead(HEAD_CHANCE - 1));
    assertFalse(DEFAULTS.dropsHead(HEAD_CHANCE));
  }

  @Test
  void rejectsAChanceOverAHundredOrNoTime() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings(OVER_A_HUNDRED, SLOW_TICKS, HEAD_CHANCE, CHASE, SWITCH, PAUSE));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolSettings(SLOW_CHANCE, 0, HEAD_CHANCE, CHASE, SWITCH, PAUSE));
  }
}
