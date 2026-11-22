package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * The round of the Galactic Patroller: from point to point of its route, in a loop, at the pace of
 * a lookout, stopping at each point and every so many blocks on the way to look around (answers
 * 1561, 1614, 1615 and 1617), each one of the team from a different point (answers 1646 and 1708).
 * A point it cannot reach is skipped. Without points the area goal walks the edges instead, and a
 * fight comes first.
 */
final class PatrolGoal extends Goal {
  private static final double ARRIVED_BLOCKS = 1.5;
  private static final double PROGRESS_BLOCKS = 0.5;
  private static final double HALF = 0.5;

  private final RobotEntity robot;
  private final PatrolPause pause;
  private int repathCountdown;
  private double nearest;
  private int stuckTicks;

  PatrolGoal(RobotEntity robot) {
    this.robot = robot;
    this.pause = new PatrolPause(robot);
    setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return robot.workArea().work().patrol().active()
        && !robot.workArea().work().patrolCombat().busy();
  }

  @Override
  public boolean canContinueToUse() {
    return canUse();
  }

  @Override
  public boolean requiresUpdateEveryTick() {
    return true;
  }

  @Override
  public void start() {
    repathCountdown = 0;
    pause.reset();
    resetProgress();
    robot.workArea().work().patrol().spread();
  }

  @Override
  public void stop() {
    robot.getNavigation().stop();
  }

  @Override
  public void tick() {
    RobotPatrol patrol = robot.workArea().work().patrol();
    if (pause.hold()) {
      repathCountdown = 0;
      resetProgress();
      return;
    }
    Optional<BlockPos> point = patrol.currentPoint();
    if (point.isEmpty()) {
      return;
    }
    Vec3 target = Vec3.atBottomCenterOf(point.get().above());
    double distance = robot.position().subtract(target).horizontalDistance();
    if (distance < ARRIVED_BLOCKS || stuck(distance)) {
      patrol.arrived();
      return;
    }
    pause.count();
    if (--repathCountdown <= 0) {
      repathCountdown = CubitMod.CONFIG.followRepathTicks();
      robot.getNavigation().moveTo(target.x, target.y + HALF, target.z, patrol.speed());
    }
  }

  private boolean stuck(double distance) {
    if (distance < nearest - PROGRESS_BLOCKS) {
      nearest = distance;
      stuckTicks = 0;
      return false;
    }
    return ++stuckTicks >= CubitMod.CONFIG.dock().stuckTicks();
  }

  private void resetProgress() {
    nearest = Double.MAX_VALUE;
    stuckTicks = 0;
  }
}
