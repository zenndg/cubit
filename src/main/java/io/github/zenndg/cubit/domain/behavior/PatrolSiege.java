package io.github.zenndg.cubit.domain.behavior;

/**
 * The siege of the team of Patrollers on one target: each one takes a side, the first keeps the one
 * it came from and the others spread evenly around, so they never crowd (answers 1645 and 1718).
 */
public final class PatrolSiege {
  private static final double TURN = 2 * Math.PI;
  private static final String INVALID_RANK = "A rank outside the team: ";
  private static final String OF = " of ";

  /** A place on the ground around the target. */
  public record Spot(double x, double z) {}

  /**
   * How near each weapon stands: the blade at its stop, and the pistol at its own distance, kept
   * between the change of weapon and its range.
   */
  public record Reach(double bladeStop, int switchDistance, int pistolRange) {
    public double radius(boolean blade, double current) {
      return blade ? bladeStop : Math.clamp(current, switchDistance, pistolRange);
    }
  }

  private PatrolSiege() {}

  /** The side of one of the team, by its rank, from the side the first one keeps. */
  public static double angle(int rank, int size, double firstAngle) {
    if (size < 1 || rank < 0 || rank >= size) {
      throw new IllegalArgumentException(INVALID_RANK + rank + OF + size);
    }
    return firstAngle + TURN * rank / size;
  }

  public static Spot spot(double targetX, double targetZ, double angle, double radius) {
    return new Spot(targetX + Math.cos(angle) * radius, targetZ + Math.sin(angle) * radius);
  }
}
