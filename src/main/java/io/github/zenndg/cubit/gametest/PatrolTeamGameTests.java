package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The team of Galactic Patrollers in the world: all on one target, the call that brings a mate from
 * beyond its search and the rescue of a wounded one (answers 1643, 1644 and 1691).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PatrolTeamGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 200;
  private static final int FIRST_SHOTS = 15;
  private static final int TEAM_UP_TICKS = 2;
  private static final int FLOOR_Y = 1;
  private static final int STAND_Y = 2;
  private static final int FIRST_Z = 1;
  private static final int MIDDLE_Z = 2;
  private static final int LAST_Z = 4;
  private static final int LAST_X = 23;
  private static final int NEAR_X = 1;
  private static final int OWNER_X = 2;
  private static final int CLOSE_HOSTILE_X = 8;
  private static final int FAR_HOSTILE_X = 14;
  private static final int BEYOND_SEARCH_X = 22;
  private static final int CALLED_HOSTILE_X = 4;
  private static final int BY_THE_WOUNDED_X = 16;
  private static final int WOUNDED_X = 20;
  private static final int NEAR_THE_OWNER_X = 7;
  private static final int CARD_SLOT = 0;
  private static final Energy CHARGE = new Energy(1_000_000);
  private static final float HEALTH = 100f;
  private static final float WOUNDED_HEALTH = 20f;
  private static final String NOT_HIT = "The team should have hit this hostile";
  private static final String HIT = "The team should have left this hostile for later";
  private static final String NOT_CALLED = "The mate should have come to the call";

  private PatrolTeamGameTests() {}

  private static RobotEntity patroller(
      GameTestHelper helper, ServerPlayer owner, int x, int z, float health) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(
        CubitDataComponents.ROBOT_STATE.get(), new RobotState(RobotTier.ELITE, CHARGE, health));
    RobotEntity robot = GameTestRobots.placeRobot(helper, owner, item, new BlockPos(x, FLOOR_Y, z));
    robot
        .workArea()
        .work()
        .slot()
        .setItem(CARD_SLOT, new ItemStack(CubitItems.PATROLLER_CARD.get()));
    robot.workArea().assign(wholeArena(helper, robot), owner);
    return robot;
  }

  /** An area over the whole arena, so no one escorts and every hostile is within the chase. */
  private static WorkArea wholeArena(GameTestHelper helper, RobotEntity robot) {
    BlockPos first = helper.absolutePos(new BlockPos(0, FLOOR_Y, 0));
    BlockPos second = helper.absolutePos(new BlockPos(LAST_X, FLOOR_Y, LAST_Z));
    return WorkArea.between(
        new AreaCorner(first.getX(), first.getY(), first.getZ()),
        new AreaCorner(second.getX(), second.getY(), second.getZ()),
        robot.level().dimension().location().toString());
  }

  private static ServerPlayer owner(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    BlockPos stand = helper.absolutePos(new BlockPos(OWNER_X, STAND_Y, MIDDLE_Z));
    owner.setPos(stand.getX(), stand.getY(), stand.getZ());
    return owner;
  }

  /** A husk, which never burns in the sun, so only the team hurts it. */
  private static Husk hostile(GameTestHelper helper, int x) {
    return hostile(helper, x, MIDDLE_Z);
  }

  private static Husk hostile(GameTestHelper helper, int x, int z) {
    return helper.spawnWithNoFreeWill(EntityType.HUSK, new BlockPos(x, STAND_Y, z));
  }

  private static boolean hurt(LivingEntity mob) {
    return mob.getHealth() < mob.getMaxHealth();
  }

  /**
   * The hostiles come once every Patroller has had a tick and joined its team, as in a world where
   * they are already loaded.
   */
  private static void onceTheTeamIsUp(GameTestHelper helper, Runnable then) {
    helper.runAfterDelay(TEAM_UP_TICKS, then);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theTeamFightsOneTargetAtATime(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    patroller(helper, owner, NEAR_X, FIRST_Z, HEALTH);
    patroller(helper, owner, NEAR_X, LAST_Z, HEALTH);
    onceTheTeamIsUp(
        helper,
        () -> {
          Husk close = hostile(helper, CLOSE_HOSTILE_X);
          Husk far = hostile(helper, FAR_HOSTILE_X);
          helper.runAfterDelay(FIRST_SHOTS, () -> firstHit(helper, close, far));
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theCallBringsAMateFromBeyondItsSearch(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    patroller(helper, owner, NEAR_X, MIDDLE_Z, HEALTH);
    RobotEntity mate = patroller(helper, owner, BEYOND_SEARCH_X, MIDDLE_Z, HEALTH);
    onceTheTeamIsUp(helper, () -> hostile(helper, CALLED_HOSTILE_X));
    helper.succeedWhen(
        () -> helper.assertTrue(mate.workArea().work().patrolCombat().fighting(), NOT_CALLED));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theTeamComesToTheRescueOfAWoundedOne(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    patroller(helper, owner, NEAR_X, MIDDLE_Z, HEALTH);
    patroller(helper, owner, WOUNDED_X, MIDDLE_Z, WOUNDED_HEALTH);
    onceTheTeamIsUp(
        helper,
        () -> {
          Husk byTheWounded = hostile(helper, BY_THE_WOUNDED_X);
          // Off the line of fire to the other, or the orb would hit it on the way.
          Husk nearTheOwner = hostile(helper, NEAR_THE_OWNER_X, LAST_Z);
          helper.runAfterDelay(FIRST_SHOTS, () -> firstHit(helper, byTheWounded, nearTheOwner));
        });
  }

  /** The team shot the first and left the other for later. */
  private static void firstHit(GameTestHelper helper, LivingEntity first, LivingEntity later) {
    helper.assertTrue(hurt(first), NOT_HIT);
    helper.assertFalse(hurt(later), HIT);
    helper.succeed();
  }
}
