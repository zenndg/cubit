package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotFollowCall;
import io.github.zenndg.cubit.registry.CubitAttachments;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The area mode: the Area app draws the area of a Cubit, which goes there, fetches the items inside
 * it and leaves it with `Follow` (answers 1051 to 1064 and 1080).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AreaModeGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int SETTLE_TICKS = 40;

  /** Long enough to walk to the item, too short for the round to bring the Cubit by it. */
  private static final int FETCH_TICKS = 60;

  private static final int FLOOR_Y = 1;
  private static final BlockPos ROBOT_FLOOR = new BlockPos(4, FLOOR_Y, 2);
  private static final BlockPos FIRST_CORNER = new BlockPos(14, FLOOR_Y, 0);
  private static final BlockPos WIDE_FIRST_CORNER = new BlockPos(6, FLOOR_Y, 0);
  private static final BlockPos SECOND_CORNER = new BlockPos(20, FLOOR_Y, 4);
  private static final BlockPos ITEM_SPOT = new BlockPos(17, FLOOR_Y + 1, 2);
  private static final Energy CHARGE = new Energy(300_000);
  private static final float HEALTH = 50f;
  private static final String NO_AREA = "The Cubit should have its area";
  private static final String HAS_AREA = "The Cubit should have no area";
  private static final String STILL_FOLLOWS = "A Cubit in an area does not follow";
  private static final String SWITCHED_OFF = "A Cubit in an area must stay on";
  private static final String NOT_INSIDE = "The Cubit did not reach its area";
  private static final String NOT_FETCHED = "The Cubit did not fetch the item in its area";
  private static final String NOT_FOLLOWING = "Follow should take the Cubit out of its area";

  private AreaModeGameTests() {}

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theAreaAppSendsTheCubitToItsArea(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);

    AreaClicks.drawBox(helper, owner, robot, FIRST_CORNER, SECOND_CORNER);

    helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
    helper.assertTrue(follower(owner).isEmpty(), STILL_FOLLOWS);
    helper.succeedWhen(() -> {
      helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, SWITCHED_OFF);
      WorkArea area = robot.workArea().area().orElseThrow();
      helper.assertTrue(area.contains(robot.getX(), robot.getY(), robot.getZ()), NOT_INSIDE);
    });
  }

  @GameTest(template = ARENA)
  public static void aBaseCubitHasNoAreaMode(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, RobotTier.BASE);

    AreaClicks.drawBox(helper, owner, robot, FIRST_CORNER, SECOND_CORNER);

    helper.assertTrue(robot.workArea().area().isEmpty(), HAS_AREA);
    helper.succeed();
  }

  /**
   * With fetch off, in an area only pickup counts (answer 1080). The magnet is off and the item
   * falls once the Cubit is inside, away from the center and the edges of the round, so only the
   * fetch can take it, lying on top of the marked floor.
   */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theCubitFetchesTheItemsInItsArea(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    robot.pickup().setSettings(robot.pickup().settings().toggledFetch().toggledMagnet());
    AreaClicks.drawBox(helper, owner, robot, WIDE_FIRST_CORNER, SECOND_CORNER);
    ItemEntity[] item = new ItemEntity[1];

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(insideArea(robot), NOT_INSIDE))
        .thenExecute(() -> item[0] = dropItem(helper))
        .thenExecuteAfter(FETCH_TICKS, () -> helper.assertFalse(item[0].isAlive(), NOT_FETCHED))
        .thenSucceed();
  }

  /**
   * An item lying on top of the marked floor is inside the area, where the fetch looks. Checked on
   * the next tick, before the Cubit walks over it on its way to the center.
   */
  @GameTest(template = ARENA)
  public static void theFetchSeesTheItemsOnTopOfTheMarkedFloor(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    AreaClicks.drawBox(helper, owner, robot, FIRST_CORNER, SECOND_CORNER);
    ItemEntity item = dropItem(helper);

    helper.runAfterDelay(
        1,
        () -> {
          WorkArea area = robot.workArea().area().orElseThrow();
          helper.assertValueEqual(robot.pickup().areaTarget(area), Optional.of(item), NOT_FETCHED);
          helper.succeed();
        });
  }

  private static boolean insideArea(RobotEntity robot) {
    return robot
        .workArea()
        .area()
        .filter(area -> area.contains(robot.getX(), robot.getY(), robot.getZ()))
        .isPresent();
  }

  private static ItemEntity dropItem(GameTestHelper helper) {
    Vec3 spot = helper.absoluteVec(Vec3.atBottomCenterOf(ITEM_SPOT));
    ItemEntity item =
        new ItemEntity(helper.getLevel(), spot.x, spot.y, spot.z, new ItemStack(Items.COBBLESTONE));
    item.setDeltaMovement(Vec3.ZERO);
    helper.getLevel().addFreshEntity(item);
    return item;
  }

  @GameTest(template = ARENA)
  public static void followTakesTheCubitOutOfItsArea(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    AreaClicks.drawBox(helper, owner, robot, FIRST_CORNER, SECOND_CORNER);

    helper.assertTrue(new RobotFollowCall(robot).call(owner), NOT_FOLLOWING);

    helper.assertTrue(robot.workArea().area().isEmpty(), HAS_AREA);
    helper.assertValueEqual(follower(owner), Optional.of(robot.getUUID()), NOT_FOLLOWING);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theAreaIsSavedWithTheCubit(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    AreaClicks.drawBox(helper, owner, robot, FIRST_CORNER, SECOND_CORNER);

    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    RobotEntity loaded = GameTestRobots.loadRobot(helper, saved);

    helper.assertValueEqual(loaded.workArea().area(), robot.workArea().area(), NO_AREA);
    helper.succeed();
  }

  /** Leaving the Dock, a Cubit with an area goes back to it, without the place (answer 1068). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void leavingTheDockTheCubitGoesBackToItsArea(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    AreaClicks.drawBox(helper, owner, robot, FIRST_CORNER, SECOND_CORNER);

    robot.releaseFromDock();

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, SWITCHED_OFF);
          helper.assertTrue(robot.workArea().area().isPresent(), NO_AREA);
          helper.assertTrue(follower(owner).isEmpty(), STILL_FOLLOWS);
          helper.succeed();
        });
  }

  private static RobotEntity robot(GameTestHelper helper, ServerPlayer owner, RobotTier tier) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(CubitDataComponents.ROBOT_STATE.get(), new RobotState(tier, CHARGE, HEALTH));
    return GameTestRobots.placeRobot(helper, owner, item, ROBOT_FLOOR);
  }

  private static Optional<UUID> follower(ServerPlayer owner) {
    return owner.getData(CubitAttachments.FOLLOWING_ROBOT).robot();
  }
}
