package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.robot.RouteMarking;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotPatrol;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Each job in an area that is not a box: what is in the cut is outside, and the Cubit goes around
 * it (answers 2240 and 2365).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EllAreaGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 800;
  private static final int SETTLE = 300;
  private static final int FLOOR_Y = 1;
  private static final int ON_FLOOR_Y = 2;
  private static final int HEIGHT = 3;
  private static final BlockPos ROBOT_FLOOR = new BlockPos(3, FLOOR_Y, 4);

  /** A U over the arena: two legs joined by the north side, with the cut between them. */
  private static final BlockPos[] YOU = {
    new BlockPos(2, FLOOR_Y, 0),
    new BlockPos(14, FLOOR_Y, 0),
    new BlockPos(14, FLOOR_Y, 4),
    new BlockPos(12, FLOOR_Y, 4),
    new BlockPos(12, FLOOR_Y, 1),
    new BlockPos(4, FLOOR_Y, 1),
    new BlockPos(4, FLOOR_Y, 4),
    new BlockPos(2, FLOOR_Y, 4)
  };

  private static final BlockPos IN_THE_CUT = new BlockPos(8, ON_FLOOR_Y, 3);
  private static final BlockPos IN_THE_FAR_LEG = new BlockPos(13, ON_FLOOR_Y, 4);
  private static final BlockPos INNER_CORNER = new BlockPos(4, FLOOR_Y, 1);
  private static final BlockPos CHEST = new BlockPos(2, ON_FLOOR_Y, 0);
  private static final double HALF = 0.5;
  private static final int POND_MIN_X = 6;
  private static final int POND_MAX_X = 10;
  private static final int POND_MIN_Z = 3;
  private static final int POND_MAX_Z = 4;
  private static final Energy CHARGE = new Energy(300_000);
  private static final float HEALTH = 50f;
  private static final int CARD_SLOT = 0;
  private static final int SOME = 8;
  private static final int INPUT_SLOT = 0;
  private static final String LEFT_AREA = "The Cubit should go around inside its area";
  private static final String NOT_FETCHED = "The Cubit did not fetch the item in the far leg";
  private static final String TOOK_FROM_THE_CUT = "The Cubit worked outside its area";
  private static final String NOT_ASLEEP = "The water in the cut is outside the area";
  private static final String WRONG_RESULT = "The cut is outside the area of the route";
  private static final String NOT_STORED = "The Organizer did not work in its area";
  private static final String NOT_HARVESTED = "The Farmer did not work in its area";
  private static final String NOT_COOKED = "The Cook did not work in its area";
  private static final String NOT_AROUND = "The round did not reach the inner corner";

  private EllAreaGameTests() {}

  /** From one leg of the U to the other, it never steps on the cut (answer 2365). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aCubitGoesAroundTheCut(GameTestHelper helper) {
    RobotEntity robot = worker(helper, RobotTier.ADVANCED, Optional.empty());
    robot.pickup().setSettings(robot.pickup().settings().toggledMagnet());
    ItemEntity item = drop(helper, IN_THE_FAR_LEG);

    helper.onEachTick(() -> {
      WorkArea area = robot.workArea().area().orElseThrow();
      helper.assertTrue(area.shape().has(robot.getBlockX(), robot.getBlockZ()), LEFT_AREA);
    });
    helper.succeedWhen(() -> helper.assertFalse(item.isAlive(), NOT_FETCHED));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anItemInTheCutIsNotFetched(GameTestHelper helper) {
    RobotEntity robot = worker(helper, RobotTier.ADVANCED, Optional.empty());
    robot.pickup().setSettings(robot.pickup().settings().toggledMagnet());
    ItemEntity item = drop(helper, IN_THE_CUT);

    helper.runAfterDelay(
        SETTLE,
        () -> {
          helper.assertTrue(item.isAlive(), TOOK_FROM_THE_CUT);
          helper.succeed();
        });
  }

  /** The round of every Cubit goes by the inner corners too (answer 1062). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theRoundGoesByTheInnerCorner(GameTestHelper helper) {
    RobotEntity robot = worker(helper, RobotTier.ADVANCED, Optional.empty());
    Vec3 corner = helper.absoluteVec(Vec3.atBottomCenterOf(INNER_CORNER.above()));

    helper.succeedWhen(() ->
        helper.assertTrue(robot.position().subtract(corner).horizontalDistance() < 2, NOT_AROUND));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theOrganizerLeavesTheCutAlone(GameTestHelper helper) {
    RobotEntity robot =
        worker(helper, RobotTier.ADVANCED, Optional.of(CubitItems.ORGANIZER_CARD.get()));
    robot.pickup().setSettings(robot.pickup().settings().toggledMagnet());
    ChestBlockEntity chest = chest(helper, robot);
    ItemEntity outside = drop(helper, IN_THE_CUT);
    drop(helper, IN_THE_FAR_LEG);

    helper.succeedWhen(() -> {
      helper.assertTrue(chest.countItem(Items.COBBLESTONE) == 1, NOT_STORED);
      helper.assertTrue(outside.isAlive(), TOOK_FROM_THE_CUT);
    });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theFarmerLeavesTheCutAlone(GameTestHelper helper) {
    plant(helper, IN_THE_CUT);
    plant(helper, IN_THE_FAR_LEG);
    RobotEntity robot =
        worker(helper, RobotTier.ADVANCED, Optional.of(CubitItems.FARMER_CARD.get()));

    helper.succeedWhen(() -> {
      helper.assertTrue(robot.inventory().countItem(Items.WHEAT) > 0, NOT_HARVESTED);
      helper.assertTrue(helper.getBlockState(IN_THE_CUT).equals(ripeWheat()), TOOK_FROM_THE_CUT);
    });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theFisherLeavesTheWaterOfTheCutAlone(GameTestHelper helper) {
    for (int x = POND_MIN_X; x <= POND_MAX_X; x++) {
      for (int z = POND_MIN_Z; z <= POND_MAX_Z; z++) {
        helper.setBlock(new BlockPos(x, FLOOR_Y, z), Blocks.WATER);
      }
    }
    RobotEntity robot =
        worker(helper, RobotTier.ADVANCED, Optional.of(CubitItems.FISHER_CARD.get()));

    helper.succeedWhen(
        () -> helper.assertValueEqual(robot.powerState(), PowerState.HIBERNATING_AWAY, NOT_ASLEEP));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theCookLeavesTheOvenOfTheCutAlone(GameTestHelper helper) {
    RobotEntity robot = worker(helper, RobotTier.ADVANCED, Optional.of(CubitItems.COOK_CARD.get()));
    ChestBlockEntity chest = chest(helper, robot);
    chest.setItem(0, new ItemStack(Items.BEEF, SOME));
    chest.setItem(1, new ItemStack(Items.COAL, SOME));
    AbstractFurnaceBlockEntity outside = smoker(helper, robot, IN_THE_CUT);
    AbstractFurnaceBlockEntity inside = smoker(helper, robot, IN_THE_FAR_LEG);

    helper.succeedWhen(() -> {
      helper.assertTrue(inside.getItem(INPUT_SLOT).is(Items.BEEF), NOT_COOKED);
      helper.assertTrue(outside.getItem(INPUT_SLOT).isEmpty(), TOOK_FROM_THE_CUT);
    });
  }

  /** The route has no point in the cut (answer 1599). */
  @GameTest(template = ARENA)
  public static void theRouteLeavesTheCutOut(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot =
        worker(helper, owner, RobotTier.ELITE, Optional.of(CubitItems.PATROLLER_CARD.get()));
    RobotPatrol patrol = robot.workArea().work().patrol();

    helper.assertValueEqual(
        patrol.click(owner, helper.absolutePos(IN_THE_CUT.below())),
        Optional.of(RouteMarking.Result.OUTSIDE_AREA),
        WRONG_RESULT);
    helper.assertValueEqual(
        patrol.click(owner, helper.absolutePos(INNER_CORNER)),
        Optional.of(RouteMarking.Result.ADDED),
        WRONG_RESULT);
    helper.succeed();
  }

  private static RobotEntity worker(GameTestHelper helper, RobotTier tier, Optional<Item> card) {
    return worker(helper, GameTestRobots.onlinePlayer(helper), tier, card);
  }

  private static RobotEntity worker(
      GameTestHelper helper, ServerPlayer owner, RobotTier tier, Optional<Item> card) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(CubitDataComponents.ROBOT_STATE.get(), new RobotState(tier, CHARGE, HEALTH));
    RobotEntity robot = GameTestRobots.placeRobot(helper, owner, item, ROBOT_FLOOR);
    card.ifPresent(
        found -> robot.workArea().work().slot().setItem(CARD_SLOT, new ItemStack(found)));
    robot.workArea().assign(AreaClicks.area(helper, robot, HEIGHT, YOU), owner);
    return robot;
  }

  private static ItemEntity drop(GameTestHelper helper, BlockPos pos) {
    Vec3 spot = helper.absoluteVec(Vec3.atBottomCenterOf(pos));
    ItemEntity item =
        new ItemEntity(helper.getLevel(), spot.x, spot.y, spot.z, new ItemStack(Items.COBBLESTONE));
    item.setDeltaMovement(Vec3.ZERO);
    helper.getLevel().addFreshEntity(item);
    return item;
  }

  private static ChestBlockEntity chest(GameTestHelper helper, RobotEntity robot) {
    helper.setBlock(CHEST, Blocks.CHEST);
    ServerPlayer owner = owner(robot);
    robot.docking().unload().click(owner, helper.absolutePos(CHEST));
    return helper.getBlockEntity(CHEST);
  }

  private static AbstractFurnaceBlockEntity smoker(
      GameTestHelper helper, RobotEntity robot, BlockPos pos) {
    helper.setBlock(pos, Blocks.SMOKER);
    robot.workArea().work().marks().click(owner(robot), helper.absolutePos(pos), Direction.UP);
    return helper.getBlockEntity(pos);
  }

  private static ServerPlayer owner(RobotEntity robot) {
    List<ServerPlayer> players = robot.getServer().getPlayerList().getPlayers();
    return players.stream()
        .filter(player -> robot.ownerId().filter(player.getUUID()::equals).isPresent())
        .findFirst()
        .orElseThrow();
  }

  private static void plant(GameTestHelper helper, BlockPos pos) {
    helper.setBlock(pos.below(), Blocks.FARMLAND);
    helper.setBlock(pos, ripeWheat());
  }

  private static BlockState ripeWheat() {
    return ((CropBlock) Blocks.WHEAT).getStateForAge(((CropBlock) Blocks.WHEAT).getMaxAge());
  }
}
