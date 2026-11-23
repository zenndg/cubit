package io.github.zenndg.cubit.domain.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SolarOutputTest {
  private static final Energy DAY = new Energy(1_400);
  private static final Energy NIGHT = new Energy(350);
  private static final SolarOutput WITH_NIGHT = new SolarOutput(DAY, NIGHT);
  private static final SolarOutput DAY_ONLY = new SolarOutput(DAY, Energy.ZERO);

  @Test
  void itGivesTheRateOfTheMoment() {
    assertEquals(DAY, WITH_NIGHT.at(true));
    assertEquals(NIGHT, WITH_NIGHT.at(false));
    assertEquals(Energy.ZERO, DAY_ONLY.at(false));
  }

  @Test
  void onlyARateAboveZeroGivesAtNight() {
    assertTrue(WITH_NIGHT.givesAtNight());
    assertFalse(DAY_ONLY.givesAtNight());
  }

  /** At night it shows the rate of the night, and without one, the rate of the day (1949). */
  @Test
  void theMostIsOfTheMomentOnlyWhenItGivesAtNight() {
    assertEquals(DAY, WITH_NIGHT.most(true));
    assertEquals(NIGHT, WITH_NIGHT.most(false));
    assertEquals(DAY, DAY_ONLY.most(true));
    assertEquals(DAY, DAY_ONLY.most(false));
  }
}
