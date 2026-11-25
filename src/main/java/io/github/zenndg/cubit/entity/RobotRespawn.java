package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.RespawnReturn;
import io.github.zenndg.cubit.registry.CubitAttachments;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Brings the robot that follows to an owner who respawned, at the spot the game chose for the
 * owner. The game takes a dead owner out of the world a second later, so the chunk of the robot may
 * be gone, and then it is searched as in a call (answers 2458 and 2500).
 */
public final class RobotRespawn {
  private RobotRespawn() {}

  /** The robot to search, when the one that follows is not loaded anywhere. */
  public static Optional<UUID> bringTo(ServerPlayer owner) {
    return RobotFollowing.follower(owner).filter(robot -> !bringLoaded(owner, robot));
  }

  /** A robot found by the search, if it still follows. */
  public static void bringTo(ServerPlayer owner, RobotEntity robot) {
    if (RobotFollowing.follower(owner).filter(robot.getUUID()::equals).isPresent()) {
      bring(robot, owner);
    }
  }

  /**
   * The Cubit that follows, back with the owner, says where the last death was. A Cubit left in
   * another dimension cannot speak to the owner, as any other text (answer 1111).
   */
  public static void tellDeathPoint(ServerPlayer owner) {
    owner
        .getData(CubitAttachments.DEATH_LOG)
        .latest()
        .ifPresent(death ->
            RobotFollowing.follower(owner)
                .map(robot -> owner.serverLevel().getEntity(robot))
                .filter(RobotEntity.class::isInstance)
                .map(RobotEntity.class::cast)
                .ifPresent(robot -> robot.docking().deathGuide().tellOnRespawn(death)));
  }

  private static boolean bringLoaded(ServerPlayer owner, UUID robotId) {
    for (ServerLevel level : owner.server.getAllLevels()) {
      if (level.getEntity(robotId) instanceof RobotEntity robot) {
        bring(robot, owner);
        return true;
      }
    }
    return false;
  }

  private static void bring(RobotEntity robot, ServerPlayer owner) {
    boolean sameDimension = robot.level() == owner.level();
    boolean bringsBack =
        RespawnReturn.bringsBack(
            robot.powerState(),
            robot.docking().hasTrip(),
            sameDimension,
            sameDimension ? robot.distanceTo(owner) : 0,
            CubitMod.CONFIG.followDecision().teleportDistanceBlocks());
    if (bringsBack) {
      RobotArrival.moveTo(robot, owner, owner.position());
    }
  }
}
