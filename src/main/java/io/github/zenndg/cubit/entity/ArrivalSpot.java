package io.github.zenndg.cubit.entity;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Where a called Cubit appears: the nearest safe ground to its owner, or nowhere (answer 2459). */
final class ArrivalSpot {
  private ArrivalSpot() {}

  static Optional<Vec3> near(RobotEntity robot, Player owner, int radius) {
    if (!(owner.level() instanceof ServerLevel level)) {
      return Optional.empty();
    }
    RobotEntity stand = standIn(robot, level);
    BlockPos center = owner.blockPosition();
    BlockPos corner = new BlockPos(radius, radius, radius);
    return BlockPos.betweenClosedStream(center.subtract(corner), center.offset(corner))
        .filter(pos -> !pos.equals(center) && pos.closerThan(center, radius + 1))
        .map(BlockPos::immutable)
        .sorted(Comparator.comparingDouble(pos -> pos.distSqr(center)))
        .filter(pos -> isSafe(stand, level, pos))
        .findFirst()
        .map(BlockPos::getBottomCenter);
  }

  /** The route reading of the game belongs to a level, so another dimension has one of its own. */
  private static RobotEntity standIn(RobotEntity robot, ServerLevel level) {
    if (robot.level() == level) {
      return robot;
    }
    return robot.getType().create(level) instanceof RobotEntity other ? other : robot;
  }

  private static boolean isSafe(RobotEntity stand, ServerLevel level, BlockPos pos) {
    return level.getWorldBorder().isWithinBounds(pos)
        && DamagingBlocks.safeToStand(stand, pos)
        && level.noCollision(
            stand, stand.getDimensions(stand.getPose()).makeBoundingBox(pos.getBottomCenter()));
  }
}
