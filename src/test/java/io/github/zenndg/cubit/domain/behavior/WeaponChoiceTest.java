package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WeaponChoiceTest {
  private static final int SWITCH = 4;
  private static final double NEAR = 2;
  private static final double FAR = 10;

  private static WeaponChoice choose(
      double distance, boolean inAir, boolean creeper, boolean canShoot, boolean inRange) {
    return WeaponChoice.choose(
        new WeaponChoice.Check(distance, inAir, creeper, canShoot, inRange, SWITCH));
  }

  @Test
  void farTargetsGetThePistol() {
    assertEquals(WeaponChoice.PISTOL, choose(FAR, false, false, true, true));
  }

  @Test
  void nearTargetsGetTheBlade() {
    assertEquals(WeaponChoice.BLADE, choose(NEAR, false, false, true, true));
  }

  @Test
  void theSwitchDistanceIsThePistol() {
    assertEquals(WeaponChoice.PISTOL, choose(SWITCH, false, false, true, true));
  }

  @Test
  void aTargetInTheAirGetsThePistolEvenNear() {
    assertEquals(WeaponChoice.PISTOL, choose(NEAR, true, false, true, true));
  }

  @Test
  void withoutChargeEverythingGetsTheBlade() {
    assertEquals(WeaponChoice.BLADE, choose(FAR, false, false, false, true));
    assertEquals(WeaponChoice.BLADE, choose(NEAR, true, false, false, true));
  }

  @Test
  void outOfRangeTheBladeChases() {
    assertEquals(WeaponChoice.BLADE, choose(FAR, false, false, true, false));
  }

  @Test
  void aCreeperGetsOnlyThePistol() {
    assertEquals(WeaponChoice.PISTOL, choose(NEAR, false, true, true, true));
    assertEquals(WeaponChoice.NONE, choose(FAR, false, true, false, true));
    assertEquals(WeaponChoice.NONE, choose(FAR, false, true, true, false));
  }
}
