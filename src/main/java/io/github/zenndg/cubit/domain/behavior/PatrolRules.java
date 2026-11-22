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

  /** A swelling creeper near it or near a protected one sends it out of the blast. */
  public static boolean backsAway(boolean swelling, double distance, double blastRadius) {
    return swelling && distance < blastRadius;
  }
}
