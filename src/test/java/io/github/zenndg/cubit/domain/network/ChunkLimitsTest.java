package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChunkLimitsTest {
  private static final int PLAYER = 48;
  private static final int WORLD = 192;
  private static final ChunkLimits LIMITS = new ChunkLimits(PLAYER, WORLD);

  @Test
  void oneMoreFitsBelowTheLimit() {
    assertTrue(LIMITS.fitsPlayer(PLAYER - 1));
    assertFalse(LIMITS.fitsPlayer(PLAYER));
    assertTrue(LIMITS.fitsWorld(WORLD - 1));
    assertFalse(LIMITS.fitsWorld(WORLD));
  }

  @Test
  void zeroIsNoLimit() {
    ChunkLimits none = new ChunkLimits(0, 0);
    assertTrue(none.fitsPlayer(Integer.MAX_VALUE));
    assertTrue(none.fitsWorld(Integer.MAX_VALUE));
  }

  @Test
  void aNegativeLimitIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new ChunkLimits(-1, 0));
    assertThrows(IllegalArgumentException.class, () -> new ChunkLimits(0, -1));
  }
}
