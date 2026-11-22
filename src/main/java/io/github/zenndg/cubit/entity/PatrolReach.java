package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolRules;
import io.github.zenndg.cubit.domain.robot.RoutePoint;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * How far the Galactic Patroller chases: within its chase, measured from what it guards, the one it
 * escorts, else the nearest point of its route, else the base of its area, else itself (answers
 * 1562, 1565, 1636 and 1657).
 */
final class PatrolReach {
  private PatrolReach() {}

  static boolean mayChase(RobotEntity robot, Vec3 at) {
    int chase = CubitMod.CONFIG.patrol().settings().chaseDistance();
    return PatrolRules.mayChase(guardDistance(robot, at), chase);
  }

  private static double guardDistance(RobotEntity robot, Vec3 at) {
    Optional<LivingEntity> marked = robot.workArea().work().escort().escortedHere();
    if (marked.isPresent()) {
      return marked.get().position().distanceTo(at);
    }
    Player escorted = RobotInteractions.followedPlayer(robot);
    if (escorted != null) {
      return escorted.position().distanceTo(at);
    }
    OptionalDouble toRoute =
        robot.workArea().work().patrol().walkable().points().stream()
            .mapToDouble(point -> topOf(point).distanceTo(at))
            .min();
    if (toRoute.isPresent()) {
      return toRoute.getAsDouble();
    }
    return robot
        .workArea()
        .here()
        .map(area -> area.distanceToBase(at.x, at.z))
        .orElse(robot.position().distanceTo(at));
  }

  private static Vec3 topOf(RoutePoint point) {
    return Vec3.atBottomCenterOf(new BlockPos(point.x(), point.y(), point.z()).above());
  }
}
