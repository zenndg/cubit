package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.DefenseMode;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.PatrolKills;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Galactic Patroller in the world: the locked mode, the armor of the card, the pistol from a
 * distance, the blade near, the head of a kill, who it attacks by the signs of the game, the alert
 * before the first shot and the look at the owner after the last kill (answers 1552, 1557, 1559,
 * 1566, 1623, 1629, 1633 and 1701 to 1705).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PatrollerGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 200;
  private static final int FLOOR_Y = 1;
  private static final int STAND_Y = 2;
  private static final int MIDDLE_Z = 2;
  private static final int ROBOT_X = 1;
  private static final int FAR_X = 7;
  private static final int NEAR_X = 3;
  private static final int CARD_SLOT = 0;
  private static final int NEXT_TICKS = 2;
  private static final int FIGHT_TICKS = 60;
  private static final int LUCKY = 0;
  private static final int UNLUCKY = 99;
  private static final int ELITE_ARMOR = 6;
  private static final Energy CHARGE = new Energy(1_000_000);
  private static final float HEALTH = 100f;
  private static final BlockPos ROBOT_FLOOR = new BlockPos(ROBOT_X, FLOOR_Y, MIDDLE_Z);
  private static final BlockPos FAR = new BlockPos(FAR_X, STAND_Y, MIDDLE_Z);
  private static final BlockPos NEAR = new BlockPos(NEAR_X, STAND_Y, MIDDLE_Z);
  private static final String NOT_LOCKED = "The card should lock the Aggressive mode";
  private static final String WRONG_ARMOR = "Wrong armor of the card";
  private static final String NOT_HIT = "The Patroller should have hit the hostile";
  private static final String NO_ENERGY_SPENT = "The shot should have cost energy";
  private static final String WRONG_HEAD = "Wrong head after the kill";
  private static final String SPARED = "The Patroller should have attacked the mob";
  private static final String ATTACKED = "The Patroller should have left the mob alone";
  private static final String NO_ALERT = "The Patroller should hold the alert before it shoots";
  private static final String NO_CHECK = "The Patroller should look at its owner after the kill";
  private static final float ONE_SHOT = 1f;

  private PatrollerGameTests() {}

  /** The owner stands by the robot, which follows and would jump to a far owner. */
  private static RobotEntity patroller(GameTestHelper helper, ServerPlayer owner) {
    BlockPos stand = helper.absolutePos(ROBOT_FLOOR.above());
    owner.setPos(stand.getX(), stand.getY(), stand.getZ() - 1);
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(
        CubitDataComponents.ROBOT_STATE.get(), new RobotState(RobotTier.ELITE, CHARGE, HEALTH));
    RobotEntity robot = GameTestRobots.placeRobot(helper, owner, item, ROBOT_FLOOR);
    robot
        .workArea()
        .work()
        .slot()
        .setItem(CARD_SLOT, new ItemStack(CubitItems.PATROLLER_CARD.get()));
    return robot;
  }

  @GameTest(template = ARENA)
  public static void theCardLocksTheAggressiveMode(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    helper.assertTrue(robot.defense().mode() == DefenseMode.AGGRESSIVE, NOT_LOCKED);
    helper.assertFalse(robot.defense().choose(owner, DefenseMode.PEACEFUL), NOT_LOCKED);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theCardGivesArmorAndTakesItBack(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    helper.runAfterDelay(
        NEXT_TICKS,
        () -> {
          helper.assertTrue(robot.getArmorValue() == ELITE_ARMOR, WRONG_ARMOR);
          robot.workArea().work().slot().setItem(CARD_SLOT, ItemStack.EMPTY);
          helper.runAfterDelay(
              NEXT_TICKS,
              () -> {
                helper.assertTrue(robot.getArmorValue() == 0, WRONG_ARMOR);
                helper.succeed();
              });
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void thePistolShootsAFarHostile(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, FAR);
    helper.runAfterDelay(
        FIGHT_TICKS,
        () -> {
          helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), NOT_HIT);
          helper.assertTrue(robot.state().energy().amount() < CHARGE.amount(), NO_ENERGY_SPENT);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theBladeHitsANearHostile(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    patroller(helper, owner);
    Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, NEAR);
    helper.runAfterDelay(
        FIGHT_TICKS,
        () -> {
          helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), NOT_HIT);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA)
  public static void aBladeKillMayGiveTheHead(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, FAR);
    Spider spider = helper.spawnWithNoFreeWill(EntityType.SPIDER, NEAR);
    PatrolKills.dropHead(robot, zombie, UNLUCKY);
    helper.assertTrue(robot.inventory().countItem(Items.ZOMBIE_HEAD) == 0, WRONG_HEAD);
    PatrolKills.dropHead(robot, spider, LUCKY);
    PatrolKills.dropHead(robot, zombie, LUCKY);
    helper.assertTrue(robot.inventory().countItem(Items.ZOMBIE_HEAD) == 1, WRONG_HEAD);
    helper.succeed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aCalmEndermanIsAttacked(GameTestHelper helper) {
    attacks(helper, EntityType.ENDERMAN, true);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aHoglinIsAttacked(GameTestHelper helper) {
    attacks(helper, EntityType.HOGLIN, true);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aShulkerIsAttacked(GameTestHelper helper) {
    attacks(helper, EntityType.SHULKER, true);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aCalmBeeIsLeftAlone(GameTestHelper helper) {
    attacks(helper, EntityType.BEE, false);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anIronGolemIsLeftAlone(GameTestHelper helper) {
    attacks(helper, EntityType.IRON_GOLEM, false);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aBeeChasingTheOwnerIsAttacked(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    patroller(helper, owner);
    Mob bee = helper.spawnWithNoFreeWill(EntityType.BEE, FAR);
    bee.setTarget(owner);
    helper.runAfterDelay(FIGHT_TICKS, () -> assertHurt(helper, bee, true));
  }

  private static <T extends Mob> void attacks(
      GameTestHelper helper, EntityType<T> type, boolean expected) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    patroller(helper, owner);
    T mob = helper.spawnWithNoFreeWill(type, FAR);
    helper.runAfterDelay(FIGHT_TICKS, () -> assertHurt(helper, mob, expected));
  }

  private static void assertHurt(GameTestHelper helper, LivingEntity mob, boolean expected) {
    boolean hurt = mob.getHealth() < mob.getMaxHealth();
    helper.assertTrue(hurt == expected, expected ? SPARED : ATTACKED);
    helper.succeed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theAlertComesBeforeTheFirstShot(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, FAR);
    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(phase(robot) == PatrolWatch.Phase.ALERT, NO_ALERT))
        .thenExecute(() -> helper.assertFalse(husk.getHealth() < husk.getMaxHealth(), NO_ALERT))
        .thenWaitUntil(() -> helper.assertTrue(husk.getHealth() < husk.getMaxHealth(), NOT_HIT))
        .thenSucceed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theLastKillBringsALookAtTheOwner(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, FAR);
    husk.setHealth(ONE_SHOT);
    helper.succeedWhen(
        () -> helper.assertTrue(phase(robot) == PatrolWatch.Phase.OWNER_CHECK, NO_CHECK));
  }

  private static PatrolWatch.Phase phase(RobotEntity robot) {
    return robot.workArea().work().patrolCombat().watch().phase();
  }
}
