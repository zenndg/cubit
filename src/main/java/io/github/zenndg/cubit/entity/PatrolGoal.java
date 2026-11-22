package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * The round of the Galactic Patroller: from point to point of its route, in a loop, stopping at
 * each to look around (answers 1561 and 1581). A point it cannot reach is skipped. Without points
 * the area goal walks the edges instead, and a fight comes first.
 */
final class PatrolGoal extends Goal {
  private static final double ROUTE_SPEED_MODIFIER = 1;
  private static final double ARRIVED_BLOCKS = 1.5;
  private static final double PROGRESS_BLOCKS = 0.5;
  private static final double HALF = 0.5;
  private static final int LOOK_TURN_TICKS = 20;
  private static final float LOOK_ANGLE = 70f;
  private static final float LOOK_SPEED = 10f;

  private final RobotEntity robot;
  private int repathCountdown;
  private double nearest;
  private int stuckTicks;

  PatrolGoal(RobotEntity robot) {
    this.robot = robot;
    setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return robot.workArea().work().patrol().active()
        && !robot.workArea().work().patrolCombat().fighting();
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
    resetProgress();
  }

  @Override
  public void stop() {
    robot.getNavigation().stop();
  }

  @Override
  public void tick() {
    RobotPatrol patrol = robot.workArea().work().patrol();
    if (patrol.pausing()) {
      lookAround();
      patrol.waitOrGoOn();
      if (!patrol.pausing()) {
        patrol.waitOrGoOn();
        resetProgress();
      }
      return;
    }
    Optional<BlockPos> point = patrol.currentPoint();
    if (point.isEmpty()) {
      return;
    }
    Vec3 target = Vec3.atBottomCenterOf(point.get().above());
    double distance = robot.position().subtract(target).horizontalDistance();
    if (distance < ARRIVED_BLOCKS || stuck(distance)) {
      robot.getNavigation().stop();
      patrol.arrived();
      return;
    }
    if (--repathCountdown <= 0) {
      repathCountdown = CubitMod.CONFIG.followRepathTicks();
      robot.getNavigation().moveTo(target.x, target.y + HALF, target.z, ROUTE_SPEED_MODIFIER);
    }
  }

  /** It turns its head from side to side while it waits (answer 1581). */
  private void lookAround() {
    float side = (robot.tickCount / LOOK_TURN_TICKS) % 2 == 0 ? LOOK_ANGLE : -LOOK_ANGLE;
    float yaw = robot.getYRot() + side;
    Vec3 look = Vec3.directionFromRotation(0, yaw).add(robot.getEyePosition());
    robot.getLookControl().setLookAt(look.x, look.y, look.z, LOOK_SPEED, LOOK_SPEED);
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
