package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.AreaErase;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.player.TabletUse;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The drawing of an area with the Area app: the points, the refusals, closing, opening again,
 * undoing, applying and erasing, and the saves (answers 2240 to 2246, 2283, 2299 and 2352 to 2357).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DrawnAreaGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int FLOOR_Y = 1;
  private static final int HIGH_Y = 8;
  private static final int TOO_HIGH_Y = 9;
  private static final int HEIGHT = 8;
  private static final float LOOK_DOWN = 90f;
  private static final BlockPos ROBOT_FLOOR = new BlockPos(4, FLOOR_Y, 2);
  private static final BlockPos OTHER_FLOOR = new BlockPos(6, FLOOR_Y, 2);
  private static final BlockPos A = new BlockPos(14, FLOOR_Y, 0);
  private static final BlockPos B = new BlockPos(20, FLOOR_Y, 0);
  private static final BlockPos C = new BlockPos(20, FLOOR_Y, 4);
  private static final BlockPos D = new BlockPos(14, FLOOR_Y, 4);
  private static final BlockPos D_HIGH = new BlockPos(14, HIGH_Y, 4);
  private static final BlockPos D_TOO_HIGH = new BlockPos(14, TOO_HIGH_Y, 4);
  private static final BlockPos CROOKED = new BlockPos(17, FLOOR_Y, 3);
  private static final BlockPos ON_A_LINE = new BlockPos(17, FLOOR_Y, 0);
  private static final BlockPos FAR = new BlockPos(40, FLOOR_Y, 0);
  private static final BlockPos INNER = new BlockPos(16, FLOOR_Y, 2);
  private static final BlockPos ELL_EAST = new BlockPos(20, FLOOR_Y, 2);
  private static final BlockPos ELL_SOUTH = new BlockPos(16, FLOOR_Y, 4);
  private static final int ELL_CORNERS = 6;
  private static final Energy LOW_CHARGE = new Energy(100_000);
  private static final int SETTLE_TICKS = 40;
  private static final Energy CHARGE = new Energy(300_000);
  private static final float HEALTH = 50f;
  private static final String OLD_KEY = "WorkArea";
  private static final String NEW_KEY = "DrawnArea";
  private static final String POINTS_KEY = "Points";
  private static final String OLD_MIN_KEY = "Min";
  private static final String NO_AREA = "The Cubit should have its area";
  private static final String HAS_AREA = "The Cubit should have no area";
  private static final String WRONG_HEIGHT = "The area has the wrong height";
  private static final String WRONG_SHAPE = "The area should have the shape drawn";
  private static final String STILL_ON = "A Cubit without an area should switch off";
  private static final String CHANGED = "The Cubit should keep its charge";

  private DrawnAreaGameTests() {}

  @GameTest(template = ARENA)
  public static void anEllIsDrawnAndApplied(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);

    AreaClicks.draw(helper, owner, robot, A, B, ELL_EAST, INNER, ELL_SOUTH, D);

    WorkArea area = robot.workArea().area().orElseThrow();
    BlockPos cut = helper.absolutePos(C);
    BlockPos inner = helper.absolutePos(INNER);
    helper.assertValueEqual(area.cornerCount(), ELL_CORNERS, WRONG_SHAPE);
    helper.assertTrue(area.contains(inner.getX(), inner.getY(), inner.getZ()), WRONG_SHAPE);
    helper.assertFalse(area.contains(cut.getX(), cut.getY(), cut.getZ()), WRONG_SHAPE);
    helper.succeed();
  }

  /** Open, Shift and the click take out the last point, and never apply (answer 2245). */
  @GameTest(template = ARENA)
  public static void shiftUndoesTheLastPointOfAnOpenDrawing(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.choose(owner, robot);
    AreaClicks.mark(helper, owner, A, B, C, ELL_SOUTH);

    AreaClicks.shift(owner, new TabletUse.Air());
    helper.assertTrue(robot.workArea().area().isEmpty(), HAS_AREA);
    AreaClicks.mark(helper, owner, D, A);
    AreaClicks.shift(owner, new TabletUse.Air());

    helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
    helper.succeed();
  }

  /** Every refused point leaves the drawing as it was, so it still closes (answer 2356). */
  @GameTest(template = ARENA)
  public static void aRefusedPointLeavesTheDrawing(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.choose(owner, robot);

    AreaClicks.mark(helper, owner, A, FAR, B, CROOKED, ON_A_LINE, C, A, D_TOO_HIGH, D, A);
    AreaClicks.shift(owner, new TabletUse.Air());

    WorkArea area = robot.workArea().area().orElseThrow();
    helper.assertValueEqual(area, AreaClicks.area(helper, robot, 1, A, B, C, D), WRONG_SHAPE);
    helper.succeed();
  }

  /** The tier of the Advanced takes 8 blocks, from the lowest point to the highest (2244). */
  @GameTest(template = ARENA)
  public static void theHeightComesFromThePoints(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);

    AreaClicks.draw(helper, owner, robot, A, B, C, D_HIGH);

    WorkArea area = robot.workArea().area().orElseThrow();
    helper.assertValueEqual(area.height(), HEIGHT, WRONG_HEIGHT);
    helper.assertValueEqual(area.low(), helper.absolutePos(A).getY(), WRONG_HEIGHT);
    helper.succeed();
  }

  /** A click on a marked point opens it with all its points (answer 2355). */
  @GameTest(template = ARENA)
  public static void aMarkedPointOpensAClosedDrawing(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.choose(owner, robot);
    AreaClicks.mark(helper, owner, A, B, C, D, A, C);

    AreaClicks.shift(owner, new TabletUse.Air());
    helper.assertTrue(robot.workArea().area().isEmpty(), HAS_AREA);
    AreaClicks.mark(helper, owner, D, A);
    AreaClicks.shift(owner, new TabletUse.Air());

    helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
    helper.succeed();
  }

  /** The drawing is of the Cubit clicked last, and starts over with another one (2357). */
  @GameTest(template = ARENA)
  public static void anotherCubitStartsTheDrawingOver(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    RobotEntity other = robot(helper, owner, OTHER_FLOOR);
    AreaClicks.choose(owner, robot);
    AreaClicks.mark(helper, owner, A, B, C);

    AreaClicks.choose(owner, other);
    AreaClicks.mark(helper, owner, D, A);
    AreaClicks.shift(owner, new TabletUse.Air());

    helper.assertTrue(robot.workArea().area().isEmpty(), HAS_AREA);
    helper.assertTrue(other.workArea().area().isEmpty(), HAS_AREA);
    AreaClicks.shift(owner, new TabletUse.Air());
    AreaClicks.draw(helper, owner, other, A, B, C, D);
    helper.assertTrue(other.workArea().area().isPresent(), NO_AREA);
    helper.succeed();
  }

  /** Halfway through a drawing, Shift on the Cubit undoes a point and never erases (2352). */
  @GameTest(template = ARENA)
  public static void shiftOnTheCubitHalfwayNeverErases(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.draw(helper, owner, robot, A, B, C, D);
    AreaClicks.choose(owner, robot);
    AreaClicks.mark(helper, owner, A, B);

    AreaClicks.shift(owner, new TabletUse.Living(robot));
    AreaClicks.shift(owner, new TabletUse.Living(robot));

    helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
    helper.succeed();
  }

  /** The second click erases it, and the Cubit stops and switches off (answers 2299, 2353). */
  @GameTest(template = ARENA)
  public static void theSecondClickErasesTheArea(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.draw(helper, owner, robot, A, B, C, D);

    AreaClicks.shift(owner, new TabletUse.Living(robot));
    helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
    AreaClicks.shift(owner, new TabletUse.Living(robot));

    helper.assertTrue(robot.workArea().area().isEmpty(), HAS_AREA);
    helper.assertValueEqual(robot.powerState(), PowerState.INACTIVE_SWITCHED_OFF, STILL_ON);
    helper.succeed();
  }

  /** Hibernating in the Dock, it stays there (answer 2353). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anErasedAreaLeavesACubitInItsDock(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, Energy.ZERO);
    RobotEntity robot = DockScene.placeOnDock(helper, owner, LOW_CHARGE);
    robot.workArea().assign(AreaClicks.area(helper, robot, 1, A, B, C, D), owner);

    robot.workArea().erase();

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.HIBERNATING, STILL_ON);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theSecondClickTooLateOnlyAsksAgain(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.draw(helper, owner, robot, A, B, C, D);
    AreaClicks.shift(owner, new TabletUse.Living(robot));

    helper.runAfterDelay(
        AreaErase.CONFIRM_TICKS + 1,
        () -> {
          AreaClicks.shift(owner, new TabletUse.Living(robot));
          helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA)
  public static void nobodyErasesTheAreaOfSomeoneElse(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    ServerPlayer stranger = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.draw(helper, owner, robot, A, B, C, D);

    AreaClicks.shift(stranger, new TabletUse.Living(robot));
    AreaClicks.shift(stranger, new TabletUse.Living(robot));

    helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
    helper.succeed();
  }

  /** A click in the air marks the block in the aim (answer 2311). */
  @GameTest(template = ARENA)
  public static void theBlockInTheAimIsMarked(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, A);
    helper.setBlock(A, Blocks.STONE);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.choose(owner, robot);

    owner.setXRot(LOOK_DOWN);
    TabletUse.click(owner, AreaClicks.tablet(), new TabletUse.Air());
    AreaClicks.mark(helper, owner, B, C, D, A);
    AreaClicks.shift(owner, new TabletUse.Air());

    helper.assertValueEqual(
        robot.workArea().area().orElseThrow(),
        AreaClicks.area(helper, robot, 1, A, B, C, D),
        WRONG_SHAPE);
    helper.succeed();
  }

  /** The box of before the drawing is not read, and nothing else changes (answer 2283). */
  @GameTest(template = ARENA)
  public static void anAreaOfAnOldSaveIsDropped(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    CompoundTag old = new CompoundTag();
    old.putLong(OLD_MIN_KEY, helper.absolutePos(A).asLong());
    saved.put(OLD_KEY, old);

    RobotEntity loaded = GameTestRobots.loadRobot(helper, saved);

    helper.assertTrue(loaded.workArea().area().isEmpty(), HAS_AREA);
    helper.assertValueEqual(loaded.state().energy(), robot.state().energy(), CHANGED);
    helper.succeed();
  }

  /** A broken drawing never stops the game (rule 17). */
  @GameTest(template = ARENA)
  public static void aBrokenAreaIsDropped(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.draw(helper, owner, robot, A, B, C, D);
    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    saved.getCompound(NEW_KEY).putLongArray(POINTS_KEY, new long[] {0, 1});

    RobotEntity loaded = GameTestRobots.loadRobot(helper, saved);

    helper.assertTrue(loaded.workArea().area().isEmpty(), HAS_AREA);
    helper.succeed();
  }

  /** Active, away from its owner and with no area left, it switches off (answer 2353). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aCubitThatLostItsAreaSwitchesOff(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, ROBOT_FLOOR);
    AreaClicks.draw(helper, owner, robot, A, B, C, D);

    robot.workArea().clear();

    helper.succeedWhen(() ->
        helper.assertValueEqual(robot.powerState(), PowerState.INACTIVE_SWITCHED_OFF, STILL_ON));
  }

  private static RobotEntity robot(GameTestHelper helper, ServerPlayer owner, BlockPos floor) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(
        CubitDataComponents.ROBOT_STATE.get(), new RobotState(RobotTier.ADVANCED, CHARGE, HEALTH));
    return GameTestRobots.placeRobot(helper, owner, item, floor);
  }
}
