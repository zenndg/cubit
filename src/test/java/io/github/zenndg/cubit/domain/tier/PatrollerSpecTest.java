package io.github.zenndg.cubit.domain.tier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.Percentage;
import org.junit.jupiter.api.Test;

class PatrollerSpecTest {
  private static final long SHOT_COST = 1_000;
  private static final long JUMP_COST = 500;
  private static final long PLENTY = 5_000;
  private static final Energy CAPACITY = new Energy(10_000);
  private static final Energy SHOT = new Energy(SHOT_COST);
  private static final Energy JUMP = new Energy(JUMP_COST);
  private static final int PISTOL_DAMAGE = 5;
  private static final int PISTOL_RANGE = 16;
  private static final int PISTOL_TICKS = 20;
  private static final int SLOWNESS = 1;
  private static final int BLADE_DAMAGE = 7;
  private static final int BLADE_TICKS = 12;
  private static final int KNOCKBACK = 1;
  private static final int ARMOR = 6;
  private static final int SEARCH = 16;
  private static final int JUMP_HEIGHT = 3;
  private static final int POINTS = 16;
  private static final int AREA = 64;
  private static final double REACH = 2.5;
  private static final int GAP = 4;
  private static final Percentage RETURN = new Percentage(5);
  private static final int TIER_AREA = 32;
  private static final int TIER_HEIGHT = 24;
  private static final PatrollerSpec.Weapons WEAPONS =
      new PatrollerSpec.Weapons(
          PISTOL_DAMAGE,
          PISTOL_RANGE,
          PISTOL_TICKS,
          SHOT,
          SLOWNESS,
          BLADE_DAMAGE,
          BLADE_TICKS,
          KNOCKBACK,
          REACH);
  private static final PatrollerSpec ELITE =
      new PatrollerSpec(
          WEAPONS,
          ARMOR,
          SEARCH,
          new PatrollerSpec.Jump(JUMP_HEIGHT, JUMP, GAP),
          POINTS,
          AREA,
          RETURN);

  private static Battery charged(long amount) {
    return new Battery(CAPACITY, new Energy(amount));
  }

  @Test
  void aShotNeedsItsWholeCost() {
    assertTrue(ELITE.canShoot(charged(SHOT_COST)));
    assertFalse(ELITE.canShoot(charged(SHOT_COST - 1)));
  }

  @Test
  void aShotPaysItsCost() {
    assertEquals(new Energy(PLENTY - SHOT_COST), ELITE.payForShot(charged(PLENTY)).charge());
  }

  @Test
  void aJumpNeedsItsCostAndAHeight() {
    assertTrue(ELITE.canJump(charged(JUMP_COST)));
    assertFalse(ELITE.canJump(charged(JUMP_COST - 1)));
    assertFalse(PatrollerSpec.NONE.canJump(charged(PLENTY)));
  }

  @Test
  void aJumpPaysItsCost() {
    assertEquals(new Energy(PLENTY - JUMP_COST), ELITE.payForJump(charged(PLENTY)).charge());
  }

  @Test
  void theAreaHasItsOwnSideAndTheHeightOfTheTier() {
    assertEquals(new AreaSpec(AREA, TIER_HEIGHT), ELITE.area(new AreaSpec(TIER_AREA, TIER_HEIGHT)));
  }

  @Test
  void rejectsInvalidNumbers() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrollerSpec(WEAPONS, -1, SEARCH, ELITE.jump(), POINTS, AREA, RETURN));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new PatrollerSpec.Weapons(
                PISTOL_DAMAGE,
                0,
                PISTOL_TICKS,
                SHOT,
                SLOWNESS,
                BLADE_DAMAGE,
                BLADE_TICKS,
                KNOCKBACK,
                REACH));
    assertThrows(IllegalArgumentException.class, () -> new PatrollerSpec.Jump(-1, JUMP, GAP));
  }
}
