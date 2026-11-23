package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BladeGlowTest {
  private static final int IGNITE = 8;
  private static final int LINGER = 40;
  private static final BladeGlow.Timings TIMINGS = new BladeGlow.Timings(IGNITE, LINGER);
  private static final double NEAR = 3;
  private static final double APPROACHING = 7.5;
  private static final double FAR = 12;

  @Test
  void theBladeIsWantedUpCloseAndOnTheWayIn() {
    assertTrue(BladeGlow.wanted(true, WeaponChoice.BLADE, FAR, TIMINGS));
    assertTrue(BladeGlow.wanted(true, WeaponChoice.PISTOL, APPROACHING, TIMINGS));
    assertTrue(BladeGlow.wanted(true, WeaponChoice.PISTOL, IGNITE, TIMINGS));
  }

  @Test
  void theBladeIsNotWantedFarAwayOrOutOfAFight() {
    assertFalse(BladeGlow.wanted(true, WeaponChoice.PISTOL, FAR, TIMINGS));
    assertFalse(BladeGlow.wanted(false, WeaponChoice.BLADE, NEAR, TIMINGS));
  }

  /** It keeps away from a creeper it cannot shoot, so the blade has no use (answer 1642). */
  @Test
  void theBladeIsNotWantedWithoutAWeapon() {
    assertFalse(BladeGlow.wanted(true, WeaponChoice.NONE, NEAR, TIMINGS));
  }

  @Test
  void negativeTimingsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new BladeGlow.Timings(-1, LINGER));
    assertThrows(IllegalArgumentException.class, () -> new BladeGlow.Timings(IGNITE, -1));
  }
}
