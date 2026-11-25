package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.domain.tier.AreaSpec;
import io.github.zenndg.cubit.gametest.AreaBoxes;
import org.junit.jupiter.api.Test;

class AreaCheckTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String NETHER = "minecraft:the_nether";
  private static final WorkArea AREA =
      AreaBoxes.between(new AreaCorner(0, 64, 0), new AreaCorner(9, 66, 9), OVERWORLD);
  private static final WorkArea WIDE =
      AreaBoxes.between(new AreaCorner(0, 64, 0), new AreaCorner(40, 66, 9), OVERWORLD);
  private static final AreaSpec ADVANCED = new AreaSpec(16, 8);
  private static final int REACH = 24;
  private static final double NEAR = 5;
  private static final double FAR = 200;

  @Test
  void anAreaInReachOfTheCubitIsTaken() {
    assertEquals(AreaCheck.OK, AreaCheck.of(AREA, ADVANCED, place(OVERWORLD, NEAR)));
  }

  @Test
  void theRefusalSaysWhy() {
    assertEquals(AreaCheck.LOCKED, AreaCheck.of(AREA, AreaSpec.NONE, place(OVERWORLD, NEAR)));
    assertEquals(AreaCheck.TOO_BIG, AreaCheck.of(WIDE, ADVANCED, place(OVERWORLD, NEAR)));
    assertEquals(AreaCheck.OTHER_DIMENSION, AreaCheck.of(AREA, ADVANCED, place(NETHER, NEAR)));
    assertEquals(AreaCheck.OUT_OF_REACH, AreaCheck.of(AREA, ADVANCED, place(OVERWORLD, FAR)));
  }

  private static AreaCheck.Place place(String dimension, double at) {
    return new AreaCheck.Place(dimension, at, at, REACH);
  }
}
