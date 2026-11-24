package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.domain.xp.PlayerXp;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.domain.xp.XpSettings;
import io.github.zenndg.cubit.domain.xp.XpState;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotXp;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The XP of the Cubit: the orbs, the trade with its owner, the saves and the Dock (1814 to 2083).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class XpGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 600;
  private static final int WAIT_TICKS = 40;
  private static final BlockPos FLOOR = new BlockPos(2, 1, 2);
  private static final Vec3 BESIDE = new Vec3(3.5, 2.2, 2.5);
  private static final Energy CHARGE = new Energy(100_000);
  private static final Energy FULL_CHARGE = new Energy(300_000);
  private static final Energy PLENTY = new Energy(250_000);
  private static final float HEALTH = 25f;
  private static final int RATE = 20;
  private static final int CAPACITY = 8_000;
  private static final int SMALL_ORB = 5;
  private static final int BIG_ORB = 30;
  private static final int MERGED = 3;
  private static final int ROOM = 10;
  private static final int LEVEL = 5;
  private static final int MANY_LEVELS = 40;
  private static final String NOT_TAKEN = "The orb should be in the tank";
  private static final String TAKEN = "The orb should stay on the ground";
  private static final String WRONG_FLUID = "Wrong fluid in the tank";
  private static final String WRONG_REST = "The rest of the orb should stay";
  private static final String NOT_DELETED = "The orb should be deleted";
  private static final String NOT_KEPT = "The XP should be kept";
  private static final String NOT_MOVED = "A level should move";
  private static final String MOVED = "No XP should move";
  private static final String LOST = "XP was lost or made";
  private static final String NOT_LEFT = "The Cubit should leave the Dock";
  private static final String NOT_EMPTY = "It should leave only with an empty tank";
  private static final String NOT_RETURNED = "The full Cubit should empty at the Dock";

  private XpGameTests() {}

  private static RobotEntity place(GameTestHelper helper, Player owner, XpState xp) {
    return place(helper, owner, xp, CHARGE);
  }

  private static RobotEntity place(GameTestHelper helper, Player owner, XpState xp, Energy energy) {
    return GameTestRobots.placeRobot(helper, owner, item(xp, energy), FLOOR);
  }

  private static ItemStack item(XpState xp, Energy energy) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(
        CubitDataComponents.ROBOT_STATE.get(),
        new RobotState(RobotTier.BASE, energy, HEALTH).withXp(xp));
    return item;
  }

  private static XpState holding(int fluid, XpMode mode) {
    return new XpState(fluid, XpSettings.INITIAL.withMode(mode));
  }

  private static Player owner(GameTestHelper helper) {
    return helper.makeMockPlayer(GameType.SURVIVAL);
  }

  private static ExperienceOrb drop(GameTestHelper helper, int value) {
    Vec3 spot = helper.absoluteVec(BESIDE);
    ExperienceOrb orb = new ExperienceOrb(helper.getLevel(), spot.x, spot.y, spot.z, value);
    orb.setDeltaMovement(Vec3.ZERO);
    helper.getLevel().addFreshEntity(orb);
    return orb;
  }

  private static int fluid(RobotEntity robot) {
    return robot.pickup().xp().tank().fluid();
  }

  /** Every orb a merged one stands for goes in (answer 1822). */
  @GameTest(template = ARENA)
  public static void aCubitTakesTheOrbsItTouches(GameTestHelper helper) {
    RobotEntity robot = place(helper, owner(helper), XpState.EMPTY);
    drop(helper, SMALL_ORB).count = MERGED;

    helper.succeedWhen(
        () -> helper.assertValueEqual(fluid(robot), SMALL_ORB * MERGED * RATE, NOT_TAKEN));
  }

  /** The part that fits goes in, and the rest stays, with nothing lost (answer 2065). */
  @GameTest(template = ARENA)
  public static void aBigOrbGivesOnlyThePartThatFits(GameTestHelper helper) {
    RobotEntity robot = place(helper, owner(helper), holding(CAPACITY - ROOM * RATE, XpMode.STORE));
    drop(helper, BIG_ORB);

    helper.runAfterDelay(
        WAIT_TICKS,
        () -> {
          helper.assertValueEqual(fluid(robot), CAPACITY, WRONG_FLUID);
          int left =
              helper.getEntities(EntityType.EXPERIENCE_ORB).stream()
                  .mapToInt(orb -> orb.value * orb.count)
                  .sum();
          helper.assertValueEqual(left, BIG_ORB - ROOM, WRONG_REST);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA)
  public static void ignoringDeletesTheOrbs(GameTestHelper helper) {
    RobotEntity robot = place(helper, owner(helper), holding(0, XpMode.IGNORE));
    ExperienceOrb orb = drop(helper, SMALL_ORB);

    helper.succeedWhen(() -> {
      helper.assertTrue(orb.isRemoved(), NOT_DELETED);
      helper.assertValueEqual(fluid(robot), 0, WRONG_FLUID);
    });
  }

  /** The orb the game takes to another player is theirs, in every mode (answer 1933). */
  @GameTest(template = ARENA)
  public static void theOrbOfAnotherPlayerStays(GameTestHelper helper) {
    RobotEntity robot = place(helper, owner(helper), XpState.EMPTY);
    ExperienceOrb orb = drop(helper, SMALL_ORB);
    Player other = owner(helper);
    other.moveTo(orb.position());
    orb.followingPlayer = other;

    helper.runAfterDelay(
        WAIT_TICKS / 2,
        () -> {
          helper.assertValueEqual(fluid(robot), 0, TAKEN);
          helper.succeed();
        });
  }

  /** In the save, in the item and back in the world (answer 1838). */
  @GameTest(template = ARENA)
  public static void theXpStaysInTheSaveAndInTheItem(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    XpState kept = new XpState(CAPACITY / 2, new XpSettings(XpMode.STORE, true));
    RobotEntity robot = place(helper, owner, kept);

    RobotEntity loaded = GameTestRobots.loadRobot(helper, robot.saveWithoutId(new CompoundTag()));
    helper.assertValueEqual(loaded.pickup().xp().state(), kept, NOT_KEPT);

    ItemStack item = GameTestRobots.collect(owner, robot);
    RobotState saved = item.get(CubitDataComponents.ROBOT_STATE.get());
    helper.assertTrue(saved != null && saved.xp().equals(kept), NOT_KEPT);
    helper.assertValueEqual(saved.damaged().xp(), kept, NOT_KEPT);
    helper.succeed();
  }

  /** A level each time, both ways, and nothing is lost or made (answers 2071 and 2076). */
  @GameTest(template = ARENA)
  public static void aLevelGoesToTheTankAndBack(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    RobotXp xp = place(helper, owner, XpState.EMPTY).pickup().xp();
    owner.setExperienceLevels(MANY_LEVELS);
    owner.setExperiencePoints(1);
    long all = total(owner);

    for (int click = 0; click < LEVEL; click++) {
      boolean moved = xp.send(owner);
      helper.assertValueEqual(total(owner) + xp.tank().fluid() / RATE, all, LOST);
      helper.assertTrue(moved || xp.tank().fluid() == CAPACITY, NOT_MOVED);
    }
    int level = owner.experienceLevel;
    helper.assertTrue(level < MANY_LEVELS, NOT_MOVED);
    helper.assertTrue(xp.receive(owner), NOT_MOVED);
    helper.assertValueEqual(total(owner) + xp.tank().fluid() / RATE, all, LOST);
    helper.succeed();
  }

  private static long total(Player player) {
    return PlayerXp.of(player.experienceLevel, player.experienceProgress).total();
  }

  /** Only the owner, and only with the Cubit active (answers 2073 and 2077). */
  @GameTest(template = ARENA)
  public static void onlyTheOwnerOfAnActiveCubitTrades(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    ServerPlayer stranger = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    owner.setExperienceLevels(LEVEL);
    stranger.setExperienceLevels(LEVEL);
    RobotXp active = place(helper, owner, holding(CAPACITY / 2, XpMode.STORE)).pickup().xp();
    helper.assertFalse(active.send(stranger) || active.receive(stranger), MOVED);

    BlockPos other = FLOOR.east(2);
    ItemStack empty = item(holding(CAPACITY / 2, XpMode.STORE), Energy.ZERO);
    RobotXp inactive = GameTestRobots.placeRobot(helper, owner, empty, other).pickup().xp();
    helper.assertFalse(inactive.send(owner) || inactive.receive(owner), MOVED);
    helper.succeed();
  }

  /** In the mode Ignore the XP a player sends stays in the tank (answer 2080). */
  @GameTest(template = ARENA)
  public static void ignoringKeepsWhatThePlayerSends(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    RobotXp xp = place(helper, owner, holding(0, XpMode.IGNORE)).pickup().xp();
    owner.setExperienceLevels(LEVEL);

    helper.assertTrue(xp.send(owner), NOT_MOVED);
    helper.assertValueEqual(xp.tank().fluid(), PlayerXp.neededAt(LEVEL - 1) * RATE, WRONG_FLUID);
    helper.succeed();
  }

  /** The XP passes to the Dock, and even a full Cubit leaves only with none (1835 and 2067). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aDockedCubitEmptiesItsTankAndThenLeaves(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = DockScene.placeDock(helper, owner, DockScene.FLOOR, PLENTY);
    RobotEntity robot = docked(helper, owner);

    helper.succeedWhen(() -> {
      helper.assertValueEqual(fluid(robot) + dock.xp().tank().fluid(), CAPACITY, LOST);
      helper.assertTrue(robot.powerState() != PowerState.HIBERNATING, NOT_LEFT);
      helper.assertValueEqual(fluid(robot), 0, NOT_EMPTY);
    });
  }

  private static RobotEntity docked(GameTestHelper helper, ServerPlayer owner) {
    DockScene.useOnDock(helper, owner, item(holding(CAPACITY, XpMode.STORE), FULL_CHARGE));
    return GameTestRobots.robots(helper).getFirst();
  }

  /** A full tank in the mode Return to Dock goes there by itself (answers 1827 and 1934). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aFullCubitReturnsToTheDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = DockScene.placeDock(helper, owner, DockScene.FLOOR, PLENTY);
    ItemStack full = item(holding(CAPACITY, XpMode.RETURN_TO_DOCK), CHARGE);
    RobotEntity robot =
        GameTestRobots.placeRobot(helper, owner, full, DockScene.FLOOR.west(MERGED));

    helper.succeedWhen(() -> {
      helper.assertTrue(dock.xp().tank().fluid() > 0, NOT_RETURNED);
      helper.assertValueEqual(fluid(robot) + dock.xp().tank().fluid(), CAPACITY, LOST);
    });
  }

  /** The item of the Dock keeps its XP (answer 1838). */
  @GameTest(template = ARENA)
  public static void theItemOfTheDockKeepsItsXp(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = DockScene.placeDock(helper, owner, DockScene.FLOOR, PLENTY);
    dock.xp().setTank(dock.xp().tank().fill(LEVEL, CubitMod.CONFIG.xp().rate()).tank());

    helper.assertValueEqual(
        dock.collectComponents().get(CubitDataComponents.DOCK_XP.get()).fluid(),
        LEVEL * RATE,
        NOT_KEPT);
    helper.succeed();
  }
}
