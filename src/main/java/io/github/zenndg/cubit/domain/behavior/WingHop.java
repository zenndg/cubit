package io.github.zenndg.cubit.domain.behavior;

/**
 * The hop of the wings of the Galactic Patroller: when it is worth it, how high the short flight
 * goes and the motion of each tick, to rise over what is in the way and glide to the landing
 * (answers 1647, 1648, 1651 and 1738). The world checks, as a free way and a safe landing, stay
 * with the caller.
 */
public final class WingHop {
  /** How far over the highest of the way the flight goes, so it never brushes a block. */
  private static final double CLEARANCE = 1;

  private WingHop() {}

  /** The motion of one tick: forward toward the landing and up, while it still rises. */
  public record Motion(double forward, double up, boolean rising) {}

  /** The path of the game does not reach, or walks more than the factor of the straight line. */
  public static boolean worthHopping(
      boolean reaches, double pathLength, double straight, double detourFactor) {
    return !reaches || pathLength > straight * detourFactor;
  }

  /** How high it flies: over the highest of the start, the landing and the way, with room. */
  public static double apex(double startY, double landingY, double highestOnTheWay) {
    return Math.max(Math.max(startY, landingY), highestOnTheWay) + CLEARANCE;
  }

  /** Within the jump of the tier, from where it stands (answer 1647). */
  public static boolean withinJump(double startY, double apexY, int jumpHeight) {
    return apexY - startY <= jumpHeight + CLEARANCE;
  }

  /**
   * Below the height of the flight it rises with a little forward, and above it crosses toward the
   * landing, slowing down at the end, while the wings glide it down.
   */
  public static Motion motion(double y, double apexY, double toLanding, double speed, double rise) {
    double forward = Math.min(speed, toLanding);
    if (y < apexY) {
      return new Motion(forward / 2, rise, true);
    }
    return new Motion(forward, 0, false);
  }
}
