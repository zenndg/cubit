package io.github.zenndg.cubit.domain.behavior;

/**
 * When the Plasma Blade of the Patroller is wanted lit: from a target on the way in, even while it
 * still shoots with the pistol in the other claw. It stays lit a while after, by a {@link Linger}
 * (answers 1574, 1804 and 1805).
 */
public final class BladeGlow {
  private static final String NEGATIVE = "Blade timings cannot be negative: ";
  private static final String SEPARATOR = ", ";

  private BladeGlow() {}

  /** How near a target lights it and how long it stays lit after, in ticks. */
  public record Timings(int igniteDistance, int lingerTicks) {
    public Timings {
      if (igniteDistance < 0 || lingerTicks < 0) {
        throw new IllegalArgumentException(NEGATIVE + igniteDistance + SEPARATOR + lingerTicks);
      }
    }
  }

  /**
   * In a fight with a weapon: the blade chosen, or the target already within the ignite distance.
   */
  public static boolean wanted(
      boolean fighting, WeaponChoice weapon, double distance, Timings timings) {
    if (!fighting || weapon == WeaponChoice.NONE) {
      return false;
    }
    return weapon == WeaponChoice.BLADE || distance <= timings.igniteDistance();
  }
}
