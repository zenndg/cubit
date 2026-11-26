package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LinkLimitsTest {
  private static final int ULTIMATE_ANTENNAS = 9;
  private static final int ULTIMATE_MARKS = 6;

  @Test
  void aBaseCubitHasNoAntenna() {
    assertDoesNotThrow(() -> new LinkLimits(0, 1, 1));
    assertDoesNotThrow(() -> new LinkLimits(ULTIMATE_ANTENNAS, ULTIMATE_MARKS, ULTIMATE_MARKS));
  }

  @Test
  void noLimitIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> new LinkLimits(-1, 1, 1));
    assertThrows(IllegalArgumentException.class, () -> new LinkLimits(1, -1, 1));
    assertThrows(IllegalArgumentException.class, () -> new LinkLimits(1, 1, -1));
  }
}
