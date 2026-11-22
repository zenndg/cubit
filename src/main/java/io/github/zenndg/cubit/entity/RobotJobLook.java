package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import java.util.Optional;

/**
 * What the client knows of the job, only for the looks: the card in the slot, whether the tool of
 * the job is in the claw and the watch of the Galactic Patroller, with its visor (answers 1197,
 * 1199, 1229 and 1756). The rules stay on the server.
 */
public final class RobotJobLook {
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

  /** Set on the server on every tick of the watch. The game sends only a value that changed. */
  static void showWatch(RobotEntity robot, PatrolWatch watch) {
    robot.getEntityData().set(RobotSyncedData.PATROL_PHASE, (byte) watch.phase().ordinal());
    robot.getEntityData().set(RobotSyncedData.VISOR_DOWN, watch.visorDown());
  }

  /** Set on the server: the tool of the job goes to the claw while the Cubit works. */
  static void showTool(RobotEntity robot, boolean inClaw) {
    robot.getEntityData().set(RobotSyncedData.TOOL_IN_CLAW, inClaw);
  }
}
