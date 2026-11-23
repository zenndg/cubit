package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class CascadeCostTest {
  private static final int CRAFTS = 25;
  private static final int TICKS = 30;
  private static final Energy ENERGY = new Energy(2_500);
  private static final Energy TOTAL = new Energy(62_500);
  private static final Energy LARGE = new Energy(40_000);
  private static final Energy LARGE_TOTAL = new Energy(1_000_000);
  private static final int SECONDS = 38;

  /** Five robots are 25 crafts, 62,500 FE and 37.5 s, rounded up (answer 2013). */
  @Test
  void fiveRobots() {
    CascadeCost cost = new CascadeCost(CRAFTS, TICKS, ENERGY);

    assertEquals(TOTAL, cost.energy());
    assertEquals(SECONDS, cost.seconds());
  }

  /** A craft may cost more than 16 bits hold. */
  @Test
  void aLargeCostIsShownWhole() {
    assertEquals(LARGE_TOTAL, new CascadeCost(CRAFTS, TICKS, LARGE).energy());
  }

  @Test
  void nothingCostsNothing() {
    CascadeCost cost = new CascadeCost(0, TICKS, ENERGY);

    assertEquals(new Energy(0), cost.energy());
    assertEquals(0, cost.seconds());
  }

  @Test
  void negativeNumbersAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new CascadeCost(-1, TICKS, ENERGY));
    assertThrows(IllegalArgumentException.class, () -> new CascadeCost(1, -1, ENERGY));
  }
}
