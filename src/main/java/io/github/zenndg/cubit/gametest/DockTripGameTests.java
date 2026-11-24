package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlock;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.entity.RobotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The robot on its way to the base: called, stuck, without a dock, or waiting its turn. */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockTripGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int LONG_TIMEOUT = 600;
  private static final int WALK_TICKS = 120;
  private static final int STUCK_TICKS = 180;
  private static final int BEFORE_GIVING_UP = 60;
  private static final double AHEAD = 1.2;
  private static final double ASIDE = 0.5;
  private static final double REACH = 1.5;
  private static final String NOT_IN_REACH = "The robot should be within reach of the plate";
  private static final int THROUGH_TICKS = 300;
  private static final int CHARGE_TICKS = 20;
  private static final int ROBOT_X = 5;
  private static final int OTHER_DOCK_X = 18;
  private static final BlockPos FLOOR = DockScene.FLOOR;
  private static final BlockPos DOCK = DockScene.DOCK;
  private static final BlockPos ROBOT_FLOOR =
      new BlockPos(ROBOT_X, DockScene.FLOOR_Y, DockScene.MIDDLE_Z);
  private static final BlockPos OTHER_DOCK_FLOOR =
      new BlockPos(OTHER_DOCK_X, DockScene.FLOOR_Y, DockScene.MIDDLE_Z);
  private static final Energy PLENTY = new Energy(100_000);
  private static final Energy STOCK = new Energy(50_000);
  private static final Energy LOW = new Energy(1_000);
  private static final String WRONG_POWER = "Wrong power state";
  private static final String TELEPORTED = "The robot should be walking to the dock";
  private static final int STILL_WALKING_TICKS = 25;
  private static final double SOME_BLOCKS = 0.5;
  private static final double TOO_FAR = 6;

  private DockTripGameTests() {}

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void sneakClickingCallsTheNearestActiveRobot(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    DockScene.sneakClick(helper, owner, DOCK);

    Vec3 start = robot.position();
    helper.runAfterDelay(
        STILL_WALKING_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, TELEPORTED);
          double walked = robot.position().distanceTo(start);
          helper.assertTrue(walked > SOME_BLOCKS && walked < TOO_FAR, TELEPORTED);
        });

    helper.runAfterDelay(
        WALK_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.HIBERNATING, WRONG_POWER);
          helper.assertTrue(robot.docking().isDockedAt(helper.absolutePos(DOCK)), WRONG_POWER);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void sneakClickingTheUpperPartAlsoCalls(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    DockScene.sneakClick(helper, owner, DOCK.above());

    helper.runAfterDelay(
        WALK_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.HIBERNATING, WRONG_POWER);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aStrangerCallsNobody(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    ServerPlayer stranger = GameTestRobots.onlinePlayer(helper);
    DockScene.click(helper, stranger, DOCK);
    DockScene.sneakClick(helper, stranger, DOCK);

    helper.runAfterDelay(
        WALK_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, WRONG_POWER);
          helper.succeed();
        });
  }

  /** With no way on foot it gives up, and goes back to what it did (answer 2091). */
  @GameTest(template = ARENA, timeoutTicks = LONG_TIMEOUT)
  public static void aRobotWithoutARouteGivesUpTheDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);
    DockScene.buildWallAt(helper, ROBOT_X + 2);

    robot.callToDock(helper.absolutePos(DOCK));

    helper.runAfterDelay(
        STUCK_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, WRONG_POWER);
          helper.assertTrue(robot.answersDockCall(), WRONG_POWER);
          helper.succeed();
        });
  }

  /** Beside the dock but with a pane between, it never goes through (answer 2091). */
  @GameTest(template = ARENA, timeoutTicks = LONG_TIMEOUT)
  public static void aRobotNeverEntersThroughABlock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    closeRing(helper);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    robot.callToDock(helper.absolutePos(DOCK));

    helper.runAfterDelay(
        THROUGH_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, WRONG_POWER);
          helper.succeed();
        });
  }

  /** Against the bars and within reach of the plate, the line to it is what stops it (2091). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void withinReachTheBarsStillStopTheRobot(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = DockScene.placeDock(helper, owner, FLOOR, STOCK);
    closeRing(helper);
    RobotEntity robot = withinReach(helper, owner, dock);

    robot.callToDock(helper.absolutePos(DOCK));

    helper.runAfterDelay(
        BEFORE_GIVING_UP,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, WRONG_POWER);
          helper.succeed();
        });
  }

  /** The same place without the bars is where it enters from, so the test above proves the bars. */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void withinReachAndWithoutBarsTheRobotEnters(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = DockScene.placeDock(helper, owner, FLOOR, STOCK);
    RobotEntity robot = withinReach(helper, owner, dock);

    robot.callToDock(helper.absolutePos(DOCK));

    helper.runAfterDelay(
        BEFORE_GIVING_UP,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.HIBERNATING, WRONG_POWER);
          helper.succeed();
        });
  }

  private static RobotEntity withinReach(
      GameTestHelper helper, ServerPlayer owner, InductionDockBlockEntity dock) {
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);
    Direction front = dock.getBlockState().getValue(InductionDockBlock.FACING);
    Direction side = front.getClockWise();
    Vec3 place =
        dock.robotSpot()
            .add(front.getStepX() * AHEAD, 0, front.getStepZ() * AHEAD)
            .add(side.getStepX() * ASIDE, 0, side.getStepZ() * ASIDE);
    robot.moveTo(place.x, dock.getBlockPos().getY(), place.z);
    helper.assertTrue(robot.position().distanceTo(dock.robotSpot()) <= REACH, NOT_IN_REACH);
    return robot;
  }

  /** Bars all around the dock, joined, so there is no gap between them nor at the corners. */
  private static void closeRing(GameTestHelper helper) {
    BlockState bars = Blocks.IRON_BARS.defaultBlockState();
    for (Direction side : Direction.Plane.HORIZONTAL) {
      bars = bars.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(side), true);
    }
    for (BlockPos pos : BlockPos.betweenClosed(DOCK.offset(-1, 0, -1), DOCK.offset(1, 2, 1))) {
      if (pos.getX() != DOCK.getX() || pos.getZ() != DOCK.getZ()) {
        helper.setBlock(pos, bars);
      }
    }
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aRobotGivesUpWhenTheDockIsGone(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);
    DockScene.sneakClick(helper, owner, DOCK);
    helper.setBlock(DOCK, Blocks.AIR);

    helper.runAfterDelay(
        WALK_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, WRONG_POWER);
          helper.assertTrue(robot.answersDockCall(), WRONG_POWER);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = LONG_TIMEOUT)
  public static void aRobotGoesToAnotherFreeDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    DockScene.placeDock(helper, owner, OTHER_DOCK_FLOOR, STOCK);
    DockScene.placeOnDock(helper, owner, LOW);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    robot.callToDock(helper.absolutePos(DOCK));

    helper.runAfterDelay(
        STUCK_TICKS,
        () -> {
          helper.assertTrue(
              robot.docking().isDockedAt(helper.absolutePos(OTHER_DOCK_FLOOR.above())),
              WRONG_POWER);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = LONG_TIMEOUT)
  public static void aRobotWaitsForABusyDockAndEntersWhenItIsFree(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, FLOOR, STOCK);
    RobotEntity first = DockScene.placeOnDock(helper, owner, LOW);
    RobotEntity waiting = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);
    waiting.callToDock(helper.absolutePos(DOCK));

    helper.runAfterDelay(
        WALK_TICKS,
        () -> {
          helper.assertValueEqual(waiting.powerState(), PowerState.ACTIVE, WRONG_POWER);
          first.releaseFromDock();
          helper.runAfterDelay(
              CHARGE_TICKS,
              () -> {
                helper.assertValueEqual(waiting.powerState(), PowerState.HIBERNATING, WRONG_POWER);
                helper.succeed();
              });
        });
  }
}
