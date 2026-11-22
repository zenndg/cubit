package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import io.github.zenndg.cubit.domain.behavior.RobotNotice;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotJobLook;
import io.github.zenndg.cubit.entity.RobotNotices;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The auto return: the Cubit that follows goes to a Dock with a low battery, to the same Dock as
 * `Go to Dock`, and the switch keeps it from going (answers 1065 to 1068).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AutoReturnGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int WALK_TICKS = 160;
  private static final int SHORT_TICKS = 20;
  private static final int ROBOT_X = 5;
  private static final int NEARER_DOCK_X = 8;
  private static final BlockPos ROBOT_FLOOR =
      new BlockPos(ROBOT_X, DockScene.FLOOR_Y, DockScene.MIDDLE_Z);
  private static final BlockPos NEARER_DOCK_FLOOR =
      new BlockPos(NEARER_DOCK_X, DockScene.FLOOR_Y, 0);

  /** Below the 10% of the return and above the 5% that would switch the Base off. */
  private static final Energy LOW = new Energy(24_000);

  private static final Energy PLENTY = new Energy(100_000);
  private static final Energy STOCK = new Energy(50_000);
  private static final String NOT_DOCKED = "The Cubit did not go back to the Dock";
  private static final String DOCKED = "The Cubit went to the Dock without a reason";
  private static final String NOT_SAID = "The Cubit did not say it needs to recharge";
  private static final String NOT_KEPT = "The auto return switch was not kept";

  /** Below the 5% of the return of an Elite Patroller, of 3 million FE (answer 1688). */
  private static final Energy LOW_PATROLLER = new Energy(120_000);

  private static final float PATROLLER_HEALTH = 100f;
  private static final int CARD_SLOT = 0;
  private static final int STAND_Y = 2;
  private static final int BEHIND_X = 2;
  private static final double TOUGH = 1000;

  /** Longer than the check of the return and shorter than an investigation (answer 1624). */
  private static final int SETTLE_TICKS = 40;

  private static final String NO_FIGHT = "The Patroller should have fought the hostile";
  private static final String INVESTIGATED = "With a low battery it should not investigate";

  private AutoReturnGameTests() {}

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aFollowingCubitWithALowBatteryGoesBackToTheDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, LOW);

    helper.runAfterDelay(
        SHORT_TICKS,
        () ->
            helper.assertTrue(RobotNotices.isHolding(robot, RobotNotice.NEED_RECHARGE), NOT_SAID));
    helper.runAfterDelay(WALK_TICKS, () -> assertDocked(helper, robot, DockScene.DOCK));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theSwitchOffKeepsTheCubitFollowing(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, LOW);
    robot.docking().autoReturn().toggle();

    helper.runAfterDelay(WALK_TICKS, () -> assertStillOut(helper, robot));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aboveTheReturnChargeTheCubitKeepsFollowing(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    helper.runAfterDelay(WALK_TICKS, () -> assertStillOut(helper, robot));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void withoutADockTheCubitTriesAgainLater(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, LOW);

    helper.runAfterDelay(
        SHORT_TICKS, () -> DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK));
    helper.runAfterDelay(WALK_TICKS, () -> assertDocked(helper, robot, DockScene.DOCK));
  }

  /** The other Dock is nearer, so only the mark of the Tablet sends the Cubit to the far one. */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theCubitGoesBackToItsPreferredDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, NEARER_DOCK_FLOOR, STOCK);
    InductionDockBlockEntity preferred = DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, LOW);
    robot.docking().preferred().click(owner, preferred);

    helper.runAfterDelay(WALK_TICKS, () -> assertDocked(helper, robot, DockScene.DOCK));
  }

  @GameTest(template = ARENA)
  public static void theSwitchIsKeptInTheItemAndInTheSave(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);
    robot.docking().autoReturn().toggle();

    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    RobotEntity loaded = GameTestRobots.loadRobot(helper, saved);
    helper.assertFalse(loaded.docking().autoReturn().isOn(), NOT_KEPT);
    ItemStack item = GameTestRobots.collect(owner, robot);

    helper.assertFalse(item.get(CubitDataComponents.ROBOT_STATE.get()).autoReturnOn(), NOT_KEPT);
    helper.succeed();
  }

  /** With the battery of its return it skips the investigation and goes (answers 1689, 1763). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aLowPatrollerGoesStraightToTheDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = patroller(helper, owner);
    Husk husk =
        helper.spawnWithNoFreeWill(
            EntityType.HUSK, new BlockPos(BEHIND_X, STAND_Y, DockScene.MIDDLE_Z));
    husk.getAttribute(Attributes.MAX_HEALTH).setBaseValue(TOUGH);
    husk.setHealth((float) TOUGH);
    helper
        .startSequence()
        .thenWaitUntil(() ->
            helper.assertTrue(RobotJobLook.patrolPhase(robot) == PatrolWatch.Phase.FIGHT, NO_FIGHT))
        .thenExecute(husk::discard)
        .thenIdle(SETTLE_TICKS)
        .thenExecute(() ->
            helper.assertFalse(
                RobotJobLook.patrolPhase(robot) == PatrolWatch.Phase.INVESTIGATION, INVESTIGATED))
        .thenWaitUntil(() -> checkDocked(helper, robot, DockScene.DOCK))
        .thenSucceed();
  }

  private static RobotEntity patroller(GameTestHelper helper, ServerPlayer owner) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(
        CubitDataComponents.ROBOT_STATE.get(),
        new RobotState(RobotTier.ELITE, LOW_PATROLLER, PATROLLER_HEALTH));
    RobotEntity robot = GameTestRobots.placeRobot(helper, owner, item, ROBOT_FLOOR);
    robot
        .workArea()
        .work()
        .slot()
        .setItem(CARD_SLOT, new ItemStack(CubitItems.PATROLLER_CARD.get()));
    return robot;
  }

  private static void assertDocked(GameTestHelper helper, RobotEntity robot, BlockPos dock) {
    checkDocked(helper, robot, dock);
    helper.succeed();
  }

  private static void checkDocked(GameTestHelper helper, RobotEntity robot, BlockPos dock) {
    helper.assertValueEqual(robot.powerState(), PowerState.HIBERNATING, NOT_DOCKED);
    helper.assertTrue(robot.docking().isDockedAt(helper.absolutePos(dock)), NOT_DOCKED);
  }

  private static void assertStillOut(GameTestHelper helper, RobotEntity robot) {
    helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, DOCKED);
    helper.succeed();
  }
}
