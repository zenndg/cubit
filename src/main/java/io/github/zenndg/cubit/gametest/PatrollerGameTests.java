package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.DefenseMode;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.PatrolKills;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotJobLook;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitEntities;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Galactic Patroller in the world: the locked mode, the armor of the card, the pistol from a
 * distance, the blade near, the head of a kill, who it attacks by the signs of the game, the alert
 * before the first shot, the look at the owner after the last kill, the search for a hostile that
 * got away and the care of a wounded one (answers 1552, 1557, 1559, 1566, 1623, 1624, 1629, 1633,
 * 1686 and 1701 to 1705).
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
  private static final String NO_ORB = "The pistol should have fired its orb";
  private static final String WRONG_HEAD = "Wrong head after the kill";
  private static final String SPARED = "The Patroller should have attacked the mob";
  private static final String ATTACKED = "The Patroller should have left the mob alone";
  private static final String NO_ALERT = "The Patroller should hold the alert before it shoots";
  private static final String NO_CHECK = "The Patroller should look at its owner after the kill";
  private static final String NOT_SHOWN = "The client should see the round with the visor up";
  private static final String ALERT_NOT_SHOWN = "The client should see the alert, visor down";
  private static final float ONE_SHOT = 1f;
  private static final float WOUNDED = 20f;
  private static final float ALWAYS_DROPS = 2f;
  private static final int GHAST_X = 19;
  private static final int FIREBALL_X = 12;
  private static final int FIREBALL_POWER = 1;
  private static final String NOT_RETURNED = "The orb should have sent the fireball back";
  private static final String NOT_ESCORTED = "The Patroller should escort one it never attacks";
  private static final String ESCORTED_A_TARGET = "The Patroller should refuse to escort a hostile";
  private static final String ESCORT_NOT_KEPT = "The save should keep the escort";
  private static final String ESCORT_IN_ITEM = "The item should not keep the escort";
  private static final String NOT_ESCORTING = "The Patroller should walk to the one it escorts";
  private static final int ESCORTED_X = 20;
  private static final double ESCORT_NEAR = 4;
  private static final String NOT_STORED = "The drops of a kill should go into the inventory";
  private static final String NO_CARE = "A wounded Patroller should shoot even a near hostile";
  private static final String NO_INVESTIGATION = "The Patroller should look where the hostile was";
  private static final String NOT_BACK = "The Patroller should go back to its round after looking";

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
  /** A husk, which never burns in the sun, and the orb in the world prove the shot (1726). */
  public static void thePistolShootsAFarHostile(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    patroller(helper, owner);
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, FAR);
    helper.succeedWhen(() -> {
      helper.assertFalse(helper.getEntities(CubitEntities.LASER_ORB.get()).isEmpty(), NO_ORB);
      helper.assertTrue(husk.getHealth() < husk.getMaxHealth(), NOT_HIT);
    });
  }

  /** A husk, which never burns in the sun, so only the blade hurts it (answer 1727). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theBladeHitsANearHostile(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    patroller(helper, owner);
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, NEAR);
    helper.runAfterDelay(
        FIGHT_TICKS,
        () -> {
          helper.assertTrue(husk.getHealth() < husk.getMaxHealth(), NOT_HIT);
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

  /** The breeze sends the orb back, so only the blade hurts it (answer 1696). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aBreezeIsFoughtWithTheBlade(GameTestHelper helper) {
    attacks(helper, EntityType.BREEZE, true);
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

  /**
   * The client reads the phase and the visor, for the looks of the PR 4 (answers 1749 and 1756).
   */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theClientSeesTheWatchAndTheVisor(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    helper
        .startSequence()
        .thenExecuteAfter(
            1,
            () ->
                helper.assertTrue(
                    RobotJobLook.patrolPhase(robot) == PatrolWatch.Phase.ROUND
                        && !RobotJobLook.visorDown(robot),
                    NOT_SHOWN))
        .thenExecute(() -> helper.spawnWithNoFreeWill(EntityType.HUSK, FAR))
        .thenWaitUntil(() ->
            helper.assertTrue(
                RobotJobLook.patrolPhase(robot) == PatrolWatch.Phase.ALERT
                    && RobotJobLook.visorDown(robot),
                ALERT_NOT_SHOWN))
        .thenSucceed();
  }

  private static PatrolWatch.Phase phase(RobotEntity robot) {
    return robot.workArea().work().patrolCombat().watch().phase();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aHostileThatGetsAwayIsLookedFor(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, FAR);
    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(phase(robot) == PatrolWatch.Phase.FIGHT, NOT_HIT))
        .thenExecute(husk::discard)
        .thenWaitUntil(() ->
            helper.assertTrue(phase(robot) == PatrolWatch.Phase.INVESTIGATION, NO_INVESTIGATION))
        .thenWaitUntil(() -> helper.assertTrue(phase(robot) == PatrolWatch.Phase.ROUND, NOT_BACK))
        .thenSucceed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aWoundedPatrollerShootsEvenANearHostile(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    robot.setHealth(WOUNDED);
    helper.spawnWithNoFreeWill(EntityType.HUSK, NEAR);
    // An orb in the world is the proof of the pistol, as walking also spends the battery.
    helper.succeedWhen(() ->
        helper.assertFalse(helper.getEntities(CubitEntities.LASER_ORB.get()).isEmpty(), NO_CARE));
  }

  /** A held item with a chance of two always drops, so the test needs no luck (answer 1635). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theDropsOfAKillGoIntoTheInventory(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, FAR);
    husk.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    husk.setDropChance(EquipmentSlot.MAINHAND, ALWAYS_DROPS);
    husk.setHealth(ONE_SHOT);
    helper.succeedWhen(
        () -> helper.assertTrue(robot.inventory().countItem(Items.IRON_SWORD) == 1, NOT_STORED));
  }

  /** A fireball of a ghast heading for it is shot back (answers 1687, 1734 and 1735). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aFireballHeadingForItIsShotBack(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    patroller(helper, owner);
    Ghast ghast =
        helper.spawnWithNoFreeWill(EntityType.GHAST, new BlockPos(GHAST_X, STAND_Y, MIDDLE_Z));
    BlockPos from = helper.absolutePos(new BlockPos(FIREBALL_X, STAND_Y, MIDDLE_Z));
    BlockPos to = helper.absolutePos(ROBOT_FLOOR.above());
    Vec3 toward = Vec3.atCenterOf(to).subtract(Vec3.atCenterOf(from));
    LargeFireball fireball = new LargeFireball(helper.getLevel(), ghast, toward, FIREBALL_POWER);
    fireball.setPos(Vec3.atCenterOf(from));
    helper.getLevel().addFreshEntity(fireball);
    helper.succeedWhen(
        () -> helper.assertTrue(fireball.getOwner() instanceof RobotEntity, NOT_RETURNED));
  }

  /** It escorts one it never attacks and refuses a hostile (answers 1655 and 1656). */
  @GameTest(template = ARENA)
  public static void itEscortsOnlyOneItNeverAttacks(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, FAR);
    Mob villager = helper.spawnWithNoFreeWill(EntityType.VILLAGER, NEAR);
    helper.assertFalse(robot.workArea().work().escort().mark(husk), ESCORTED_A_TARGET);
    helper.assertTrue(robot.workArea().work().escort().mark(villager), NOT_ESCORTED);
    helper.assertTrue(robot.workArea().work().escort().escorts(villager.getUUID()), NOT_ESCORTED);
    helper.succeed();
  }

  /** The escort lives in the save of the entity, never in the item (answer 1659). */
  @GameTest(template = ARENA)
  public static void theSaveKeepsTheEscortButTheItemDoesNot(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Mob villager = helper.spawnWithNoFreeWill(EntityType.VILLAGER, NEAR);
    robot.workArea().work().escort().mark(villager);
    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    RobotEntity loaded = GameTestRobots.loadRobot(helper, saved);
    helper.assertTrue(
        loaded.workArea().work().escort().escorts(villager.getUUID()), ESCORT_NOT_KEPT);
    ItemStack item = GameTestRobots.collect(owner, robot);
    RobotEntity placed = GameTestRobots.placeRobot(helper, owner, item, ROBOT_FLOOR);
    helper.assertFalse(placed.workArea().work().escort().active(), ESCORT_IN_ITEM);
    helper.succeed();
  }

  /** It follows the one it escorts, in place of its owner (answers 1657 and 1658). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void itWalksToTheOneItEscorts(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = patroller(helper, owner);
    Mob villager =
        helper.spawnWithNoFreeWill(
            EntityType.VILLAGER, new BlockPos(ESCORTED_X, STAND_Y, MIDDLE_Z));
    robot.workArea().work().escort().mark(villager);
    helper.succeedWhen(
        () -> helper.assertTrue(robot.distanceTo(villager) < ESCORT_NEAR, NOT_ESCORTING));
  }
}
