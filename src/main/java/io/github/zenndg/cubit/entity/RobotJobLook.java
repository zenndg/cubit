package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import io.github.zenndg.cubit.domain.behavior.WeaponChoice;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import java.util.Optional;

/**
 * What the client knows of the job, only for the looks: the card in the slot, whether the tool of
 * the job is in the claw and the watch of the Galactic Patroller, with its visor (answers 1197,
 * 1199, 1229 and 1756). The rules stay on the server.
 */
public final class RobotJobLook {
  private static final int BLADE_LIT = 1;
  private static final int WINGS_OPEN = 2;
  private static final int ON_GUARD = 4;
  private static final double AIRBORNE_SPEED = 0.15;

  private RobotJobLook() {}

  public static Optional<RobotJob> job(RobotEntity robot) {
    int shown = robot.getEntityData().get(RobotSyncedData.JOB) - 1;
    RobotJob[] jobs = RobotJob.values();
    return shown >= 0 && shown < jobs.length ? Optional.of(jobs[shown]) : Optional.empty();
  }

  public static boolean fisher(RobotEntity robot) {
    return job(robot).filter(RobotJob.FISHER::equals).isPresent();
  }

  public static boolean farmer(RobotEntity robot) {
    return job(robot).filter(RobotJob.FARMER::equals).isPresent();
  }

  public static boolean organizer(RobotEntity robot) {
    return job(robot).filter(RobotJob.ORGANIZER::equals).isPresent();
  }

  public static boolean cook(RobotEntity robot) {
    return job(robot).filter(RobotJob.COOK::equals).isPresent();
  }

  public static boolean toolInClaw(RobotEntity robot) {
    return robot.getEntityData().get(RobotSyncedData.TOOL_IN_CLAW);
  }

  /** An unknown order, as from a newer server, shows the round, the calmest look. */
  public static PatrolWatch.Phase patrolPhase(RobotEntity robot) {
    int shown = robot.getEntityData().get(RobotSyncedData.PATROL_PHASE);
    PatrolWatch.Phase[] phases = PatrolWatch.Phase.values();
    return shown >= 0 && shown < phases.length ? phases[shown] : PatrolWatch.Phase.ROUND;
  }

  public static boolean visorDown(RobotEntity robot) {
    return robot.getEntityData().get(RobotSyncedData.VISOR_DOWN);
  }

  /** The blade is lit in a fight up close (answer 1574). */
  public static boolean bladeLit(RobotEntity robot) {
    return gear(robot, BLADE_LIT);
  }

  /** The wings are open in the air: a hop, a jump, a gap, a fall or a glide (answer 1553). */
  public static boolean wingsOpen(RobotEntity robot) {
    return gear(robot, WINGS_OPEN);
  }

  /** It stands on guard at a stop of its round, with the pistol up (answer 1627). */
  public static boolean onGuard(RobotEntity robot) {
    return gear(robot, ON_GUARD);
  }

  private static boolean gear(RobotEntity robot, int bit) {
    return (robot.getEntityData().get(RobotSyncedData.PATROL_GEAR) & bit) != 0;
  }

  /**
   * Set on the server after every tick of the Patroller. The game sends only a value that changed.
   */
  static void showPatrol(RobotEntity robot, RobotPatrolCombat combat) {
    PatrolWatch watch = combat.watch();
    robot.getEntityData().set(RobotSyncedData.PATROL_PHASE, (byte) watch.phase().ordinal());
    robot.getEntityData().set(RobotSyncedData.VISOR_DOWN, watch.visorDown());
    int gear = RobotSyncedData.NO_GEAR;
    if (combat.fighting() && combat.weapon() == WeaponChoice.BLADE) {
      gear |= BLADE_LIT;
    }
    if (combat.armed() && (combat.wings().flying() || inTheAir(robot))) {
      gear |= WINGS_OPEN;
    }
    if (combat.armed() && !combat.fighting() && robot.workArea().work().patrol().pausing()) {
      gear |= ON_GUARD;
    }
    robot.getEntityData().set(RobotSyncedData.PATROL_GEAR, (byte) gear);
  }

  /** Off the ground and moving up or down, so a step on a block does not open the wings. */
  private static boolean inTheAir(RobotEntity robot) {
    return !robot.onGround() && Math.abs(robot.getDeltaMovement().y) > AIRBORNE_SPEED;
  }

  /** Set on the server: the tool of the job goes to the claw while the Cubit works. */
  static void showTool(RobotEntity robot, boolean inClaw) {
    robot.getEntityData().set(RobotSyncedData.TOOL_IN_CLAW, inClaw);
  }
}
