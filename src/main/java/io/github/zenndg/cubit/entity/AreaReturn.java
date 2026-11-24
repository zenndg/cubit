package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Back to the area of the Cubit, whatever its card: it walks there and, with no way, appears inside
 * (answers 1058 and 2092).
 */
final class AreaReturn {
  private static final int TELEPORT_ATTEMPTS = 16;
  private static final double PROGRESS_BLOCKS = 0.5;
  private static final double ROUTE_SPEED_MODIFIER = 1;

  private final RobotEntity robot;
  private double nearest;
  private int stuckTicks;
  private int repathCountdown;

  AreaReturn(RobotEntity robot) {
    this.robot = robot;
    reset();
  }

  boolean outside(WorkArea area) {
    return !area.contains(robot.getX(), robot.getY(), robot.getZ());
  }

  void reset() {
    nearest = Double.MAX_VALUE;
    stuckTicks = 0;
    repathCountdown = 0;
  }

  /** With nothing to do outside its area, it goes back, on every tick (answer 2092). */
  void walkBack(WorkArea area) {
    if (!outside(area)) {
      reset();
      return;
    }
    Vec3 center = new Vec3(area.centerX(), levelIn(area), area.centerZ());
    double distance = robot.position().subtract(center).horizontalDistance();
    if (distance < nearest - PROGRESS_BLOCKS) {
      nearest = distance;
      stuckTicks = 0;
    } else if (++stuckTicks >= CubitMod.CONFIG.dock().stuckTicks()) {
      appearInside(area);
      return;
    }
    if (--repathCountdown <= 0) {
      repathCountdown = CubitMod.CONFIG.followRepathTicks();
      robot.getNavigation().moveTo(center.x, center.y, center.z, ROUTE_SPEED_MODIFIER);
    }
  }

  /** A task it cannot reach from outside the area is tried again from inside (answer 2092). */
  void onStuck(WorkArea area) {
    if (outside(area)) {
      appearInside(area);
    }
  }

  /** Without a safe spot, the robot stays and tries again after the next stuck time. */
  void appearInside(WorkArea area) {
    reset();
    for (int attempt = 0; attempt < TELEPORT_ATTEMPTS; attempt++) {
      BlockPos target =
          new BlockPos(
              between(area.min().x(), area.max().x()),
              between(area.min().y() + 1, area.max().y() + 1),
              between(area.min().z(), area.max().z()));
      if (FollowOwnerGoal.canStandAt(robot, target)) {
        Vec3 spot = Vec3.atBottomCenterOf(target);
        robot.moveTo(spot.x, spot.y, spot.z, robot.getYRot(), robot.getXRot());
        robot.getNavigation().stop();
        return;
      }
    }
  }

  /** From standing on the lowest marked block to standing on the highest. */
  double levelIn(WorkArea area) {
    return Math.clamp(robot.getY(), area.min().y() + 1, area.max().y() + 1);
  }

  private int between(int low, int high) {
    return robot.getRandom().nextIntBetweenInclusive(low, high);
  }
}
