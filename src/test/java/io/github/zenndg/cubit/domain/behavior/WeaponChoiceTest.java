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
        new WeaponChoice.Check(distance, inAir, creeper, canShoot, inRange, SWITCH, false, false));
  }

  private static WeaponChoice cautious(double distance, boolean canShoot) {
    return WeaponChoice.choose(
        new WeaponChoice.Check(distance, false, false, canShoot, true, SWITCH, true, false));
  }

  @Test
  void woundedItShootsEvenANearTarget() {
    assertEquals(WeaponChoice.PISTOL, cautious(NEAR, true));
  }

  @Test
  void woundedWithoutAShotItStillHasTheBlade() {
    assertEquals(WeaponChoice.BLADE, cautious(NEAR, false));
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

  /** The enderman dodges the orb and the breeze sends it back, so only the blade (1695, 1696). */
  @Test
  void aTargetTheShotCannotHurtGetsTheBlade() {
    WeaponChoice choice =
        WeaponChoice.choose(
            new WeaponChoice.Check(FAR, true, false, true, true, SWITCH, true, true));
    assertEquals(WeaponChoice.BLADE, choice);
  }
}
