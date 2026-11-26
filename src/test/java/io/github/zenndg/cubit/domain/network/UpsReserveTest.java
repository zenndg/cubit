package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class UpsReserveTest {
  private static final Energy BASE = new Energy(1_200_000);
  private static final Energy COST = new Energy(25);
  private static final int DEFAULT = 25;
  private static final int BETWEEN_STEPS = 29;
  private static final int ABOVE = 99;
  private static final long TEN_MINUTES = 12_000;
  private static final Energy KEPT = new Energy(300_000);
  private static final Energy ABOVE_KEPT = new Energy(300_001);
  private static final Energy ONE = new Energy(1);
  private static final Energy ODD = new Energy(199);
  private static final Energy QUARTER_OF_ODD = new Energy(49);

  @Test
  void aQuarterOfTheBaseHoldsTheChunkForTenMinutes() {
    UpsReserve reserve = UpsReserve.of(DEFAULT);
    assertEquals(KEPT, reserve.of(BASE));
    assertEquals(TEN_MINUTES, reserve.holdTicks(BASE, COST));
  }

  @Test
  void aBatteryThatDoesNotSplitEvenlyRoundsDown() {
    assertEquals(QUARTER_OF_ODD, UpsReserve.of(DEFAULT).of(ODD));
  }

  @Test
  void theLargestBatteryNeverOverflows() {
    Energy huge = new Energy(Long.MAX_VALUE);
    assertEquals(Long.MAX_VALUE / 2, UpsReserve.of(UpsReserve.MAX).of(huge).amount());
  }

  @Test
  void aFreeChunkIsNeverPaid() {
    assertEquals(0, UpsReserve.of(DEFAULT).holdTicks(BASE, Energy.ZERO));
  }

  @Test
  void goesUpAndDownInStepsInsideTheRange() {
    assertEquals(DEFAULT + UpsReserve.STEP, UpsReserve.of(DEFAULT).more().percent());
    assertEquals(DEFAULT - UpsReserve.STEP, UpsReserve.of(DEFAULT).less().percent());
    assertEquals(UpsReserve.MAX, UpsReserve.of(UpsReserve.MAX).more().percent());
    assertTrue(UpsReserve.NONE.less().isNone());
  }

  @Test
  void aSavedNumberGoesToASafeStep() {
    assertEquals(DEFAULT, UpsReserve.of(BETWEEN_STEPS).percent());
    assertEquals(UpsReserve.MAX, UpsReserve.of(ABOVE).percent());
    assertEquals(0, UpsReserve.of(-1).percent());
  }

  @Test
  void onlyTheStepsAreValid() {
    assertThrows(IllegalArgumentException.class, () -> new UpsReserve(BETWEEN_STEPS));
    assertThrows(IllegalArgumentException.class, () -> new UpsReserve(-UpsReserve.STEP));
    assertThrows(
        IllegalArgumentException.class, () -> new UpsReserve(UpsReserve.MAX + UpsReserve.STEP));
  }

  @Test
  void onlyWhatIsAboveTheReserveIsSpare() {
    UpsReserve reserve = UpsReserve.of(DEFAULT);
    assertEquals(ONE, reserve.spare(ABOVE_KEPT, BASE));
    assertEquals(Energy.ZERO, reserve.spare(KEPT, BASE));
    assertEquals(BASE, UpsReserve.NONE.spare(BASE, BASE));
  }
}
