package io.github.zenndg.cubit.domain.behavior;

import io.github.zenndg.cubit.domain.robot.DefenseMode;

/**
 * The rules of the Galactic Patroller around its weapons: it is always aggressive, it chases only
 * so far from its route, it jumps only at a target in the air it cannot shoot, and it backs away
 * from a creeper about to blow (answers 1562, 1566, 1567 and 1596).
 */
public final class PatrolRules {
  private PatrolRules() {}

  /** Attacking first is what the Patroller is, so the card locks the Aggressive mode. */
  public static DefenseMode mode(DefenseMode chosen, boolean patroller) {
    return patroller ? DefenseMode.AGGRESSIVE : chosen;
  }

  /** The modes the card takes away, which the defense tab shows locked (answer 1610). */
  public static boolean lockedByCard(DefenseMode mode, boolean patroller) {
    return patroller && mode != DefenseMode.AGGRESSIVE;
  }

  public static boolean mayChase(double distanceFromRoute, int chaseDistance) {
    return distanceFromRoute <= chaseDistance;
  }

  /** What the Patroller knows when a target is above it. */
  public record JumpCheck(
      boolean targetInAir,
      double heightAbove,
      int jumpHeight,
      boolean shotPossible,
      boolean canPayJump,
      boolean onGround) {}

  public static boolean jumps(JumpCheck check) {
    return check.targetInAir()
        && !check.shotPossible()
        && check.canPayJump()
        && check.onGround()
        && check.heightAbove() > 0
        && check.heightAbove() <= check.jumpHeight();
  }

  /** How hard the blade pushes: a base, and a step for each level of the tier (answer 1642). */
  public record Knockback(double base, double step) {
    private static final String INVALID_PUSH = "Invalid knockback of the blade: ";

    public Knockback {
      if (base < 0 || step < 0) {
        throw new IllegalArgumentException(INVALID_PUSH + base);
      }
    }

    public double strength(int level) {
      return base + step * level;
    }
  }

  /** How far the blast of a creeper reaches and how far it keeps without a shot (1642). */
  public record CreeperDistances(int blast, int keepAway) {
    private static final String INVALID = "Invalid distances of the creeper: ";
    private static final String SEPARATOR = ", ";

    public CreeperDistances {
      if (blast < 1 || keepAway < 1) {
        throw new IllegalArgumentException(INVALID + blast + SEPARATOR + keepAway);
      }
    }
  }

  /**
   * Where it stands as a shield without a shot: out of the blast, on the way from the creeper to
   * the one it protects (answers 1595, 1641 and 1731).
   */
  public static PatrolSiege.Spot shield(
      double creeperX, double creeperZ, double guardedX, double guardedZ, double keepAway) {
    double angle = Math.atan2(guardedZ - creeperZ, guardedX - creeperX);
    return PatrolSiege.spot(creeperX, creeperZ, angle, keepAway);
  }

  /**
   * A swelling creeper near it sends it out of the blast. Near a protected one, with it already out
   * of the blast, it keeps shooting to bring the creeper down first (answer 1731).
   */
  public static boolean backsAway(boolean swelling, double distance, double blastRadius) {
    return swelling && distance < blastRadius;
  }
}
