package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.CallArrival;
import io.github.zenndg.cubit.domain.behavior.CallResult;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.FollowCall;
import io.github.zenndg.cubit.player.TabletAreaView;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

/**
 * A called Cubit, loaded, obeys and comes to its owner. It is always the same Cubit that moves,
 * never one made again, and without a safe spot it stays and keeps what it did (answers 2457, 2459,
 * 2483 and 2484).
 */
public final class RobotArrival {
  private RobotArrival() {}

  public static CallResult called(RobotEntity robot, ServerPlayer owner) {
    boolean charged = FollowCall.charged(robot.powerState(), robot.battery());
    boolean sameDimension = robot.level() == owner.level();
    CallArrival arrival =
        CallArrival.of(
            charged,
            sameDimension,
            sameDimension ? robot.distanceTo(owner) : 0,
            CubitMod.CONFIG.followDecision().teleportDistanceBlocks());
    if (arrival == CallArrival.WALKS) {
      obey(robot, owner, true);
      return CallResult.COMING;
    }
    Optional<Vec3> spot = ArrivalSpot.near(robot, owner, CubitMod.CONFIG.calls().arrivalBlocks());
    if (spot.isEmpty()) {
      return CallResult.NO_SPOT;
    }
    obey(robot, owner, charged);
    moveTo(robot, owner, spot.get());
    return CallResult.ARRIVED;
  }

  /** An order of the owner ends the escort, the area and the trip to a Dock (1660 and 2484). */
  private static void obey(RobotEntity robot, ServerPlayer owner, boolean charged) {
    robot.workArea().work().wake();
    robot.workArea().work().escort().stop();
    robot.docking().cancelTrip();
    if (robot.workArea().area().isPresent()) {
      robot.workArea().clear();
      TabletAreaView.sync(owner);
    }
    if (charged && robot.powerState() == PowerState.INACTIVE_SWITCHED_OFF) {
      RobotInteractions.turnOn(robot, owner);
      return;
    }
    if (charged) {
      RobotInteractions.takeThePlace(robot, owner);
    }
    if (robot.powerState() == PowerState.HIBERNATING) {
      robot.releaseFromDock();
    }
  }

  static void moveTo(RobotEntity robot, ServerPlayer owner, Vec3 spot) {
    robot.stopRiding();
    robot.getNavigation().stop();
    robot.resetFallDistance();
    if (robot.level() == owner.level()) {
      robot.teleportTo(spot.x, spot.y, spot.z);
      return;
    }
    robot.changeDimension(
        new DimensionTransition(
            owner.serverLevel(),
            spot,
            Vec3.ZERO,
            robot.getYRot(),
            robot.getXRot(),
            DimensionTransition.DO_NOTHING));
  }
}
