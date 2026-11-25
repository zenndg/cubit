package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BigSlotTest {
  private static final int LIMIT = 128;
  private static final int STACK = 64;
  private static final int SMALL_STACK = 16;
  private static final int STORED = 120;
  private static final BigSlot SLOT = new BigSlot(LIMIT);

  @Test
  void keepsUpToTheLimitOfAnItemThatStacks() {
    assertEquals(LIMIT, SLOT.keeps(STACK));
    assertEquals(LIMIT, SLOT.keeps(SMALL_STACK));
  }

  @Test
  void keepsOneOfAnItemThatDoesNotStack() {
    assertEquals(1, SLOT.keeps(1));
    assertEquals(0, SLOT.takes(1, 1, 1));
  }

  @Test
  void aLimitBelowTheNormalStackKeepsTheStack() {
    assertEquals(STACK, new BigSlot(SMALL_STACK).keeps(STACK));
  }

  @Test
  void takesOnlyWhatFits() {
    assertEquals(LIMIT - STORED, SLOT.takes(STORED, STACK, STACK));
    assertEquals(STACK, SLOT.takes(0, STACK, STACK));
    assertEquals(0, SLOT.takes(LIMIT + STACK, STACK, STACK));
  }

  @Test
  void givesAtMostANormalStackAtATime() {
    assertEquals(STACK, SLOT.gives(LIMIT, LIMIT, STACK));
    assertEquals(SMALL_STACK, SLOT.gives(LIMIT, LIMIT, SMALL_STACK));
    assertEquals(1, SLOT.gives(LIMIT, 1, STACK));
    assertEquals(1, SLOT.gives(1, STACK, STACK));
  }

  @Test
  void aSlotKeepsFromOneToThreeDigits() {
    assertThrows(IllegalArgumentException.class, () -> new BigSlot(0));
    assertThrows(IllegalArgumentException.class, () -> new BigSlot(BigSlot.MAX_LIMIT + 1));
    assertEquals(BigSlot.MAX_LIMIT, new BigSlot(BigSlot.MAX_LIMIT).keeps(STACK));
  }
}
