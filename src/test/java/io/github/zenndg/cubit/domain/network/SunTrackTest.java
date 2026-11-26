package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SunTrackTest {
  private static final long DAY = 24_000;
  private static final long NOON = 6_000;
  private static final long DUSK = 12_000;
  private static final long MIDNIGHT = 18_000;
  private static final long MORNING = 3_000;
  private static final double HALF = 0.5;
  private static final double EXACT = 0;

  @Test
  void goesFromTheEastAtDawnToTheWestAtDusk() {
    assertEquals(-1, SunTrack.at(0), EXACT);
    assertEquals(-HALF, SunTrack.at(MORNING), EXACT);
    assertEquals(0, SunTrack.at(NOON), EXACT);
    assertEquals(1, SunTrack.at(DUSK), EXACT);
  }

  @Test
  void liesFlatByTheMoon() {
    assertEquals(0, SunTrack.at(MIDNIGHT), EXACT);
    assertEquals(0, SunTrack.at(DUSK + 1), EXACT);
  }

  @Test
  void everyDayIsTheSame() {
    assertEquals(SunTrack.at(MORNING), SunTrack.at(DAY + MORNING), EXACT);
  }
}
