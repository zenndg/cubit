package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LinkLimitsTest {
  private static final int ADVANCED_CHESTS = 2;
  private static final int ADVANCED_ORGANIZER = 12;
  private static final LinkLimits ADVANCED =
      new LinkLimits(1, ADVANCED_CHESTS, ADVANCED_CHESTS, ADVANCED_ORGANIZER);

  @Test
  void anOrganizerLinksMoreChests() {
    assertEquals(ADVANCED_ORGANIZER, ADVANCED.chestsFor(true));
    assertEquals(ADVANCED_CHESTS, ADVANCED.chestsFor(false));
  }

  @Test
  void noLimitIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> new LinkLimits(-1, 1, 1, 0));
    assertThrows(IllegalArgumentException.class, () -> new LinkLimits(0, -1, 1, 0));
    assertThrows(IllegalArgumentException.class, () -> new LinkLimits(0, 1, -1, 0));
    assertThrows(IllegalArgumentException.class, () -> new LinkLimits(0, 1, 1, -1));
  }
}
