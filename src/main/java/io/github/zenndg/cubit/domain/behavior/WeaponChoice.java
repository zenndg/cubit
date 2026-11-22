package io.github.zenndg.cubit.domain.behavior;

/**
 * Which weapon the Patroller uses on its target: the pistol from a distance and on a target in the
 * air, the blade near, and nothing on a creeper it cannot shoot, as it keeps away. Wounded, it
 * shoots even a near target, and keeps the blade only without a shot. On a target the orb cannot
 * hurt, as the enderman that dodges it and the breeze that sends it back, only the blade (answers
 * 1557, 1567, 1571, 1595, 1686, 1695 and 1696).
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
      boolean cautious,
      boolean shotUseless) {
    boolean shotPossible() {
      return canShoot && inPistolRange && !shotUseless;
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
