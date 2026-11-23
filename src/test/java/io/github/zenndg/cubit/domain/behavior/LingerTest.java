package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LingerTest {
  private static final int TICKS = 40;
  private static final long NOW = 100;

  @Test
  void anOffLingerIsOff() {
    assertFalse(Linger.OFF.on(NOW));
  }

  @Test
  void itStaysOnForItsTicksAfterTheLastWantedTick() {
    Linger linger = Linger.OFF.tick(true, NOW, TICKS);
    assertTrue(linger.on(NOW));
    Linger later = linger.tick(false, NOW + TICKS, TICKS);
    assertTrue(later.on(NOW + TICKS));
    assertFalse(later.on(NOW + TICKS + 1));
  }

  /** Wanted again in time, it stays on, without going off and on again. */
  @Test
  void wantingItAgainInTimeExtendsIt() {
    Linger linger = Linger.OFF.tick(true, NOW, TICKS).tick(true, NOW + TICKS, TICKS);
    assertTrue(linger.on(NOW + TICKS + TICKS));
  }

  @Test
  void zeroTicksLastOnlyWhileWanted() {
    Linger linger = Linger.OFF.tick(true, NOW, 0);
    assertTrue(linger.on(NOW));
    assertFalse(linger.on(NOW + 1));
  }

  @Test
  void negativeTicksAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> Linger.OFF.tick(true, NOW, -1));
  }
}
