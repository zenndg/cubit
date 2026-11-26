package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class SignalDistanceTest {
  private static final int FREE = 100;
  private static final int STEP = 500;
  private static final SignalDistance DEFAULTS = new SignalDistance(FREE, STEP, new Energy(1));
  private static final long NEAR = 599;
  private static final long ONE_STEP = 600;
  private static final long TWO_STEPS = 1_100;
  private static final int SIDE = 300;
  private static final int OTHER_SIDE = 400;
  private static final long HYPOTENUSE = 500;

  @Test
  void onlyWholeStepsBeyondTheFreeBlocksCost() {
    assertEquals(Energy.ZERO, DEFAULTS.cost(0));
    assertEquals(Energy.ZERO, DEFAULTS.cost(NEAR));
    assertEquals(new Energy(1), DEFAULTS.cost(ONE_STEP));
    assertEquals(new Energy(2), DEFAULTS.cost(TWO_STEPS));
  }

  @Test
  void theDistanceIsOnThePlane() {
    assertEquals(HYPOTENUSE, SignalDistance.blocks(0, 0, SIDE, OTHER_SIDE));
    assertEquals(HYPOTENUSE, SignalDistance.blocks(SIDE, OTHER_SIDE, 0, 0));
  }

  @Test
  void aStepHasABlock() {
    assertThrows(IllegalArgumentException.class, () -> new SignalDistance(FREE, 0, Energy.ZERO));
    assertThrows(IllegalArgumentException.class, () -> new SignalDistance(-1, STEP, Energy.ZERO));
  }
}
