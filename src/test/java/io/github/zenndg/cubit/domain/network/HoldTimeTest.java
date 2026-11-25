package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HoldTimeTest {
  private static final long MINUTE = 1_200;
  private static final long LAST_IN_MINUTES = 119;
  private static final long LONG = 1_066;
  private static final int LONG_HOURS = 17;
  private static final int LONG_MINUTES = 46;

  @Test
  void upToTwoHoursOnlyTheMinutesShow() {
    assertEquals(new HoldTime(0, 0), HoldTime.of(MINUTE - 1));
    assertEquals(new HoldTime(0, 1), HoldTime.of(MINUTE));
    assertEquals(new HoldTime(0, LAST_IN_MINUTES), HoldTime.of(LAST_IN_MINUTES * MINUTE));
  }

  @Test
  void fromTwoHoursTheHoursShowToo() {
    assertEquals(new HoldTime(2, 0), HoldTime.of((LAST_IN_MINUTES + 1) * MINUTE));
    assertEquals(new HoldTime(LONG_HOURS, LONG_MINUTES), HoldTime.of(LONG * MINUTE));
  }
}
