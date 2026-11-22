package io.github.zenndg.cubit.domain.behavior;

/**
 * Which weapon the Patroller uses on its target: the pistol from a distance and on a target in the
 * air, the blade near, and nothing on a creeper it cannot shoot, as it keeps away. Wounded, it
 * shoots even a near target, and keeps the blade only without a shot (answers 1557, 1567, 1571,
 * 1595 and 1686).
 */
public enum WeaponChoice {
  PISTOL,
  BLADE,
  NONE;

  /** What the Patroller knows of its target when it chooses. */
  public record Check(
      double distance,
      boolean inAir,
      boolean creeper,
      boolean canShoot,
      boolean inPistolRange,
      int switchDistance,
      boolean cautious) {
    boolean shotPossible() {
      return canShoot && inPistolRange;
    }
  }

  public static WeaponChoice choose(Check check) {
    if (check.creeper()) {
      return check.shotPossible() ? PISTOL : NONE;
    }
    boolean far = check.distance() >= check.switchDistance();
    if (check.shotPossible() && (check.cautious() || check.inAir() || far)) {
      return PISTOL;
    }
    return BLADE;
  }
}
