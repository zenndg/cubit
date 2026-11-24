package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CascadeAmountTest {
  private static final int MAX = 256;
  private static final int EIGHT = 8;

  /** A chosen item starts at zero, and plus asks for the first one (answer 2053). */
  @Test
  void itStartsAtZeroAndPlusGivesOne() {
    assertTrue(CascadeAmount.NONE.isNone());

    CascadeAmount one = CascadeAmount.NONE.adjust(1, MAX);

    assertEquals(1, one.value());
    assertFalse(one.isNone());
  }

  @Test
  void itStaysBetweenZeroAndTheLargestOrder() {
    assertEquals(0, new CascadeAmount(1).adjust(-EIGHT, MAX).value());
    assertEquals(EIGHT, CascadeAmount.NONE.adjust(EIGHT, MAX).value());
    assertEquals(MAX, new CascadeAmount(MAX - 1).adjust(EIGHT, MAX).value());
    assertEquals(MAX, new CascadeAmount(MAX).adjust(Integer.MAX_VALUE, MAX).value());
  }

  @Test
  void aNegativeAmountIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new CascadeAmount(-1));
  }
}
