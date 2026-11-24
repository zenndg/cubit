package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlock;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Whether a robot reaches a dock on foot: only from its front and with nothing between, and the
 * docks it gave up on, left out for a while (answer 2091). Only in memory, so a loaded robot tries
 * again.
 */
public final class RobotDockReach {
  public static final String UNREACHABLE_KEY = "notice.cubit.dock_unreachable";

  private static final double HALF = 0.5;

  private final RobotEntity robot;
  private final Map<BlockPos, Long> leftOut = new HashMap<>();
  private boolean told;

  RobotDockReach(RobotEntity robot) {
    this.robot = robot;
  }

  /** The dock itself may be in the way of the line, since its plate is inside its block. */
  boolean clearTo(InductionDockBlockEntity dock, Vec3 spot) {
    Direction front = dock.getBlockState().getValue(InductionDockBlock.FACING);
    Vec3 toRobot = robot.position().subtract(spot);
    if (toRobot.x * front.getStepX() + toRobot.z * front.getStepZ() <= 0) {
      return false;
    }
    BlockHitResult hit =
        robot
            .level()
            .clip(
                new ClipContext(
                    robot.position().add(0, HALF, 0),
                    spot.add(0, HALF, 0),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    robot));
    return hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(dock.getBlockPos());
  }

  boolean mayTry(BlockPos dock) {
    Long until = leftOut.get(dock);
    if (until == null) {
      return true;
    }
    if (robot.level().getGameTime() < until) {
      return false;
    }
    leftOut.remove(dock);
    return true;
  }

  void gaveUp(BlockPos dock) {
    leftOut.put(
        dock.immutable(), robot.level().getGameTime() + CubitMod.CONFIG.dock().unreachableTicks());
  }

  void arrived() {
    leftOut.clear();
    told = false;
  }

  /** Once until it gets to a dock, on the action bar of its owner, with its talk on. */
  void tell() {
    Player owner = RobotInteractions.onlineOwner(robot);
    if (told || owner == null || !robot.chatter().switchedOn()) {
      return;
    }
    told = true;
    owner.displayClientMessage(Component.translatable(UNREACHABLE_KEY, robot.getName()), true);
    robot.playSound(CubitSounds.EXPRESSION_BEEP.get());
  }
}
