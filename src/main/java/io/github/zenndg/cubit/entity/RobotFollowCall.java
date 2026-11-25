package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.robot.FollowCall;
import io.github.zenndg.cubit.menu.FollowControl;
import io.github.zenndg.cubit.player.RobotCalls;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * `Follow` of the Tablet: the chosen Cubit comes from anywhere and takes the place to follow, and
 * the one that followed switches off where it is, as with `Turn on` (answers 1055, 1073 to 1075 and
 * 2450).
 */
public final class RobotFollowCall implements FollowControl {
  private final RobotEntity robot;

  public RobotFollowCall(RobotEntity robot) {
    this.robot = robot;
  }

  @Override
  public FollowCall goal() {
    Player owner = RobotInteractions.followedPlayer(robot);
    boolean nearby =
        owner != null
            && owner.level() == robot.level()
            && robot.distanceTo(owner) < CubitMod.CONFIG.followDecision().teleportDistanceBlocks();
    return FollowCall.of(robot.powerState(), robot.battery(), nearby);
  }

  /** Checked again now, only for the owner. */
  @Override
  public boolean call(Player player) {
    return player instanceof ServerPlayer owner
        && Ownership.owns(robot.ownerId(), player.getUUID())
        && RobotCalls.callFromTablet(owner, robot.getUUID()).comes();
  }
}
