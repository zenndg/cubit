package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.robot.RouteMarking;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotPatrol;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The route of the Galactic Patroller marked with the Mark app: only the top of a solid block
 * inside the area, a click on a point unmarks it, Shift takes out the last, the limit of the tier,
 * only the owner, and the route kept in the save but lost with the item (answers 1579, 1599, 1602
 * and 1611).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PatrolRouteGameTests {
  private static final String ARENA = "arena";
  private static final int FLOOR_Y = 1;
  private static final int MIDDLE_Z = 2;
  private static final int ROBOT_X = 1;
  private static final int AREA_END_X = 12;
  private static final int LAST_Z = 4;
  private static final int POINT_X = 5;
  private static final int OTHER_X = 7;
  private static final int OUTSIDE_X = 20;
  private static final int CARD_SLOT = 0;
  private static final Energy CHARGE = new Energy(1_000_000);
  private static final float HEALTH = 100f;
  private static final BlockPos ROBOT_FLOOR = new BlockPos(ROBOT_X, FLOOR_Y, MIDDLE_Z);
  private static final BlockPos AREA_START = new BlockPos(0, FLOOR_Y, 0);
  private static final BlockPos AREA_END = new BlockPos(AREA_END_X, FLOOR_Y, LAST_Z);
  private static final BlockPos POINT = new BlockPos(POINT_X, FLOOR_Y, MIDDLE_Z);
  private static final BlockPos OTHER = new BlockPos(OTHER_X, FLOOR_Y, MIDDLE_Z);
  private static final BlockPos OUTSIDE = new BlockPos(OUTSIDE_X, FLOOR_Y, MIDDLE_Z);
  private static final String WRONG_RESULT = "Wrong result of the click";
  private static final String WRONG_ROUTE = "Wrong size of the route";
  private static final String NOT_OWNER = "Only the owner marks the route";
  private static final String NOT_KEPT = "The save should keep the route";
  private static final String KEPT_IN_ITEM = "The item should not keep the route";

  private PatrolRouteGameTests() {}

  /** An Elite Cubit with the card of the Patroller and an area over the floor. */
  private static RobotEntity patroller(GameTestHelper helper, ServerPlayer owner, boolean area) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(
        CubitDataComponents.ROBOT_STATE.get(), new RobotState(RobotTier.ELITE, CHARGE, HEALTH));
    RobotEntity robot = GameTestRobots.placeRobot(helper, owner, item, ROBOT_FLOOR);
    robot
        .workArea()
        .work()
        .slot()
        .setItem(CARD_SLOT, new ItemStack(CubitItems.PATROLLER_CARD.get()));
    if (area) {
      robot.workArea().assign(area(helper, robot), owner);
    }
    return robot;
  }

  private static WorkArea area(GameTestHelper helper, RobotEntity robot) {
    BlockPos first = helper.absolutePos(AREA_START);
    BlockPos second = helper.absolutePos(AREA_END);
    return WorkArea.between(
        new AreaCorner(first.getX(), first.getY(), first.getZ()),
        new AreaCorner(second.getX(), second.getY(), second.getZ()),
        robot.level().dimension().location().toString());
  }

  private static RouteMarking.Result click(
      GameTestHelper helper, RobotPatrol patrol, ServerPlayer player, BlockPos relative) {
    return patrol.click(player, helper.absolutePos(relative)).orElseThrow();
  }

  @GameTest(template = ARENA)
  public static void aClickMarksAndAClickOnThePointUnmarks(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotPatrol patrol = patroller(helper, owner, true).workArea().work().patrol();
    helper.assertValueEqual(
        click(helper, patrol, owner, POINT), RouteMarking.Result.ADDED, WRONG_RESULT);
    helper.assertValueEqual(
        click(helper, patrol, owner, OTHER), RouteMarking.Result.ADDED, WRONG_RESULT);
    helper.assertValueEqual(
        click(helper, patrol, owner, POINT), RouteMarking.Result.REMOVED, WRONG_RESULT);
    helper.assertValueEqual(patrol.route().size(), 1, WRONG_ROUTE);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void onlyTheTopOfASolidBlockInsideTheArea(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotPatrol patrol = patroller(helper, owner, true).workArea().work().patrol();
    helper.assertValueEqual(
        click(helper, patrol, owner, OUTSIDE), RouteMarking.Result.OUTSIDE_AREA, WRONG_RESULT);
    helper.assertValueEqual(
        click(helper, patrol, owner, POINT.above()), RouteMarking.Result.NOT_SOLID, WRONG_RESULT);
    helper.assertTrue(patrol.route().isEmpty(), WRONG_ROUTE);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void withoutAnAreaNothingIsMarked(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotPatrol patrol = patroller(helper, owner, false).workArea().work().patrol();
    helper.assertValueEqual(
        click(helper, patrol, owner, POINT), RouteMarking.Result.NO_AREA, WRONG_RESULT);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theTierKeepsOnlySoManyPoints(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotPatrol patrol = patroller(helper, owner, true).workArea().work().patrol();
    int marked = 0;
    for (int x = 0; x <= AREA_END_X && marked < patrol.limit(); x++) {
      for (int z = 0; z <= LAST_Z && marked < patrol.limit(); z++) {
        click(helper, patrol, owner, new BlockPos(x, FLOOR_Y, z));
        marked++;
      }
    }
    helper.assertValueEqual(
        click(helper, patrol, owner, new BlockPos(AREA_END_X, FLOOR_Y, LAST_Z)),
        RouteMarking.Result.FULL,
        WRONG_RESULT);
    helper.assertValueEqual(patrol.route().size(), patrol.limit(), WRONG_ROUTE);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void shiftTakesOutTheLastAndOnlyTheOwnerMarks(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    ServerPlayer stranger = GameTestRobots.onlinePlayer(helper);
    RobotPatrol patrol = patroller(helper, owner, true).workArea().work().patrol();
    click(helper, patrol, owner, POINT);
    click(helper, patrol, owner, OTHER);
    helper.assertTrue(patrol.click(stranger, helper.absolutePos(OUTSIDE)).isEmpty(), NOT_OWNER);
    helper.assertFalse(patrol.unmark(stranger, Optional.empty()), NOT_OWNER);
    helper.assertTrue(patrol.unmark(owner, Optional.empty()), WRONG_ROUTE);
    helper.assertValueEqual(patrol.route().size(), 1, WRONG_ROUTE);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theSaveKeepsTheRouteButTheItemDoesNot(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner, true);
    click(helper, robot.workArea().work().patrol(), owner, POINT);
    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    RobotEntity loaded = GameTestRobots.loadRobot(helper, saved);
    helper.assertValueEqual(
        loaded.workArea().work().patrol().route(),
        robot.workArea().work().patrol().route(),
        NOT_KEPT);
    ItemStack item = GameTestRobots.collect(owner, robot);
    RobotEntity placed = GameTestRobots.placeRobot(helper, owner, item, ROBOT_FLOOR);
    helper.assertTrue(placed.workArea().work().patrol().route().isEmpty(), KEPT_IN_ITEM);
    helper.succeed();
  }
}
