package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class XpConversionTest {
  private static final XpRate RATE = new XpRate(20);
  private static final int FE = 9;
  private static final XpConversion NINE = new XpConversion(RATE, FE);
  private static final int POINTS = 3_000;
  private static final long FARM_HOUR = 27_000;

  /** The balance of the concept: a farm of 3,000 points an hour (answers 1940 and 1942). */
  @Test
  void aPointBecomesNineEnergy() {
    assertEquals(new Energy(FARM_HOUR), NINE.energyOf(POINTS));
  }

  @Test
  void onlyWholePointsFit() {
    assertEquals(0, NINE.pointsFitting(new Energy(FE - 1)));
    assertEquals(1, NINE.pointsFitting(new Energy(FE)));
    assertEquals(1, NINE.pointsFitting(new Energy(FE * 2 - 1)));
    assertEquals(Integer.MAX_VALUE, NINE.pointsFitting(new Energy(Long.MAX_VALUE)));
  }

  @Test
  void aPointIsWorthSomeEnergy() {
    assertThrows(IllegalArgumentException.class, () -> new XpConversion(RATE, 0));
  }

  @Test
  void theTotalStopsAtTheLargestNumber() {
    XpEnergyTotal total = XpEnergyTotal.ZERO.plus(new Energy(FE));
    assertEquals(FE, total.made().amount());
    assertEquals(
        Long.MAX_VALUE,
        new XpEnergyTotal(new Energy(Long.MAX_VALUE - 1)).plus(new Energy(FE)).made().amount());
  }
}
