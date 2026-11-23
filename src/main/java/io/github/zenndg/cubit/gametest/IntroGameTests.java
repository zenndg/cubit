package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.IntroLink;
import io.github.zenndg.cubit.domain.robot.OreFilter;
import io.github.zenndg.cubit.domain.robot.RobotColor;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotNaming;
import io.github.zenndg.cubit.item.RobotStateComponent;
import io.github.zenndg.cubit.registry.CubitAttachments;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The introduction of a new Cubit on its first placement, after the window of its name (answers
 * 1672 to 1682, 1750 to 1753 and 1880). The texts reach only the client, so the tests read what the
 * server keeps: the robot marked as introduced and the tick of the last link of the player.
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class IntroGameTests {
  private static final String ARENA = "arena";
  private static final int FLOOR_Y = 1;
  private static final int MIDDLE_Z = 2;
  private static final int FIRST_X = 3;
  private static final int SECOND_X = 6;
  private static final BlockPos FLOOR = new BlockPos(FIRST_X, FLOOR_Y, MIDDLE_Z);
  private static final BlockPos SECOND_FLOOR = new BlockPos(SECOND_X, FLOOR_Y, MIDDLE_Z);
  private static final float HEALTH = 25f;
  private static final Energy SOME_ENERGY = new Energy(1_000);
  private static final String INTRODUCED_KEY = "Introduced";
  private static final String INTRODUCED_FIELD = "introduced";
  private static final String NOT_INTRODUCED = "The robot should be introduced";
  private static final String STILL_NEW = "The robot should not be introduced";
  private static final String NO_LINK = "The link should have been shown now";
  private static final String LINK_SHOWN = "The link should not have been shown";
  private static final String NO_NAME = "";

  private IntroGameTests() {}

  @GameTest(template = ARENA)
  public static void aNewCubitIsIntroducedWhenPlaced(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    helper.assertTrue(robot.chatter().introduced(), NOT_INTRODUCED);
    helper.assertTrue(lastLink(owner) == IntroLink.NEVER_SHOWN, LINK_SHOWN);
    RobotNaming.answered(owner, robot.getUUID(), NO_NAME);
    helper.assertTrue(lastLink(owner) == helper.getLevel().getGameTime(), NO_LINK);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void aCubitPlacedAgainStaysQuiet(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    ItemStack item = GameTestRobots.collect(owner, robot);
    helper.assertTrue(stateOf(item).introduced(), NOT_INTRODUCED);
    owner.setData(CubitAttachments.INTRO_LINK_TIME, IntroLink.NEVER);
    RobotEntity again = GameTestRobots.placeRobot(helper, owner, item, SECOND_FLOOR);
    helper.assertTrue(again.chatter().introduced(), NOT_INTRODUCED);
    helper.assertTrue(lastLink(owner) == IntroLink.NEVER_SHOWN, LINK_SHOWN);
    helper.succeed();
  }

  /** A link shown a tick ago keeps the next new Cubit to the help alone (answer 1681). */
  @GameTest(template = ARENA)
  public static void theLinkWaitsForTheCooldown(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    long aTickAgo = helper.getLevel().getGameTime() - 1;
    owner.setData(CubitAttachments.INTRO_LINK_TIME, IntroLink.NEVER.shownAt(aTickAgo));
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    RobotNaming.answered(owner, robot.getUUID(), NO_NAME);
    helper.assertTrue(robot.chatter().introduced(), NOT_INTRODUCED);
    helper.assertTrue(lastLink(owner) == aTickAgo, LINK_SHOWN);
    helper.succeed();
  }

  /** With the chatter off, it says nothing and counts as introduced (answer 1753). */
  @GameTest(template = ARENA)
  public static void aCubitWithTheChatterOffIsIntroducedQuietly(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotState quiet =
        new RobotState(
                RobotTier.BASE,
                SOME_ENERGY,
                HEALTH,
                true,
                RobotColor.WHITE,
                true,
                OreFilter.EMPTY,
                false)
            .withIntroduced(false);
    RobotEntity robot = GameTestRobots.placeRobot(helper, owner, itemWith(quiet), FLOOR);
    helper.assertTrue(robot.chatter().introduced(), NOT_INTRODUCED);
    helper.assertTrue(lastLink(owner) == IntroLink.NEVER_SHOWN, LINK_SHOWN);
    helper.succeed();
  }

  /** Placing it straight on a dock is also its first placement (answer 1752). */
  @GameTest(template = ARENA)
  public static void aCubitPlacedOnTheDockIsIntroduced(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, Energy.ZERO);
    DockScene.useOnDock(helper, owner, new ItemStack(CubitItems.GENERAL_ROBOT.get()));
    RobotEntity robot = GameTestRobots.robots(helper).getFirst();
    RobotNaming.answered(owner, robot.getUUID(), NO_NAME);
    helper.assertTrue(robot.chatter().introduced(), NOT_INTRODUCED);
    helper.assertTrue(lastLink(owner) == helper.getLevel().getGameTime(), NO_LINK);
    helper.succeed();
  }

  /** The items of the published versions have no field, and their Cubits are quiet (1679). */
  @GameTest(template = ARENA)
  public static void anItemSavedWithoutTheFieldCountsAsIntroduced(GameTestHelper helper) {
    RobotState fresh = new RobotState(RobotTier.BASE, SOME_ENERGY, HEALTH).withIntroduced(false);
    CompoundTag saved =
        (CompoundTag) RobotStateComponent.CODEC.encodeStart(NbtOps.INSTANCE, fresh).getOrThrow();
    helper.assertFalse(decoded(saved).introduced(), STILL_NEW);
    saved.remove(INTRODUCED_FIELD);
    helper.assertTrue(decoded(saved).introduced(), NOT_INTRODUCED);
    helper.succeed();
  }

  /** The entity saves the mark, and a robot saved before it existed counts as introduced. */
  @GameTest(template = ARENA)
  public static void aRobotSavedWithoutTheKeyCountsAsIntroduced(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    saved.putBoolean(INTRODUCED_KEY, false);
    helper.assertFalse(GameTestRobots.loadRobot(helper, saved).chatter().introduced(), STILL_NEW);
    saved.remove(INTRODUCED_KEY);
    helper.assertTrue(
        GameTestRobots.loadRobot(helper, saved).chatter().introduced(), NOT_INTRODUCED);
    helper.succeed();
  }

  private static long lastLink(ServerPlayer player) {
    return player.getData(CubitAttachments.INTRO_LINK_TIME).lastShown();
  }

  private static ItemStack itemWith(RobotState state) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(CubitDataComponents.ROBOT_STATE.get(), state);
    return item;
  }

  private static RobotState stateOf(ItemStack item) {
    return item.get(CubitDataComponents.ROBOT_STATE.get());
  }

  private static RobotState decoded(CompoundTag saved) {
    return RobotStateComponent.CODEC.parse(NbtOps.INSTANCE, saved).getOrThrow();
  }
}
