package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.CallResult;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.player.RobotCalls;
import io.github.zenndg.cubit.player.TabletSelection;
import io.github.zenndg.cubit.registry.CubitAttachments;
import io.github.zenndg.cubit.registry.CubitEntities;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * A Cubit comes to its owner from anywhere: after a real death, by a call and by the command,
 * always the same one, with what it carries (answers 2450 to 2465, 2482 to 2486 and 2500).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RobotCallGameTests {
  private static final String ARENA = "arena";
  private static final BlockPos FLOOR = new BlockPos(4, 1, 2);
  private static final BlockPos SECOND_FLOOR = new BlockPos(6, 1, 2);
  private static final Energy CHARGE = new Energy(100_000);
  private static final double ARRIVAL = 5.5;
  private static final double FAR = 40;
  private static final double IN_THE_AIR = 60;
  private static final double HALF = 0.5;
  private static final int AFTER_A_WHILE = 60;

  /** Much less than the ticket lasts by itself. */
  private static final int FREE_WITHIN = 300;

  private static final int NETHER_CHUNK = 4;
  private static final int CHUNKS_AROUND = 2;
  private static final int CHUNK = 16;
  private static final int NETHER_Y = 64;
  private static final String NOT_NEAR = "The Cubit should be beside its owner";
  private static final String MOVED = "The Cubit should have stayed";
  private static final String LOST_ITEM = "The Cubit lost what it carried";
  private static final String NOT_ONE = "There should be exactly one Cubit";
  private static final String WRONG_STATE = "The Cubit has the wrong state";
  private static final String WRONG_FOLLOWER = "The wrong Cubit follows the player";
  private static final String WRONG_RESULT = "Wrong answer to the call";
  private static final String HELD = "The chunk should be free again";

  private RobotCallGameTests() {}

  private static RobotEntity withItem(RobotEntity robot) {
    robot.inventory().setItem(0, new ItemStack(Items.DIAMOND));
    return robot;
  }

  private static Vec3 home(GameTestHelper helper) {
    return helper.absoluteVec(Vec3.atBottomCenterOf(FLOOR.above()));
  }

  private static void goHome(GameTestHelper helper, ServerPlayer player) {
    Vec3 home = home(helper);
    player.moveTo(home.x, home.y, home.z);
  }

  private static Optional<RobotEntity> loaded(GameTestHelper helper, UUID id) {
    for (ServerLevel level : helper.getLevel().getServer().getAllLevels()) {
      if (level.getEntity(id) instanceof RobotEntity robot) {
        return Optional.of(robot);
      }
    }
    return Optional.empty();
  }

  private static void assertBeside(GameTestHelper helper, UUID id, ServerPlayer owner) {
    RobotEntity robot = loaded(helper, id).orElseThrow(() -> new GameTestAssertException(NOT_NEAR));
    helper.assertTrue(
        robot.level() == owner.level() && robot.distanceTo(owner) < ARRIVAL, NOT_NEAR);
    helper.assertTrue(robot.inventory().getItem(0).is(Items.DIAMOND), LOST_ITEM);
    int count = 0;
    for (ServerLevel level : helper.getLevel().getServer().getAllLevels()) {
      count +=
          level
              .getEntities(
                  CubitEntities.GENERAL_ROBOT.get(), each -> each.ownerId().equals(robot.ownerId()))
              .size();
    }
    helper.assertValueEqual(count, 1, NOT_ONE);
  }

  private static Optional<UUID> follower(ServerPlayer player) {
    return player.getData(CubitAttachments.FOLLOWING_ROBOT).robot();
  }

  /** The case of answer 2452: the owner waits on the death screen until the chunk is gone. */
  @GameTest(template = ARENA, timeoutTicks = FarAwayScene.TIMEOUT)
  public static void itComesBackAfterTheChunkOfTheDeathUnloads(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = withItem(GameTestRobots.placeNewRobot(helper, owner, FLOOR));
    UUID id = robot.getUUID();
    FarAwayScene scene = FarAwayScene.of(helper, owner, robot);
    ServerPlayer[] back = new ServerPlayer[1];

    helper
        .startSequence()
        .thenWaitUntil(scene::assertLoaded)
        .thenExecute(scene::ownerDies)
        .thenWaitUntil(scene::assertUnloaded)
        .thenExecute(() -> back[0] = scene.ownerRespawns())
        .thenWaitUntil(() -> assertBeside(helper, id, back[0]))
        .thenExecute(() -> {
          helper.assertValueEqual(follower(back[0]), Optional.of(id), WRONG_FOLLOWER);
          RobotEntity here = loaded(helper, id).orElseThrow();
          helper.assertTrue(here.powerState() == PowerState.ACTIVE, WRONG_STATE);
          here.discard();
        })
        .thenSucceed();
  }

  @GameTest(template = ARENA, timeoutTicks = FarAwayScene.TIMEOUT)
  public static void itComesBackWithAnOwnerWhoRespawnsAtOnce(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = withItem(GameTestRobots.placeNewRobot(helper, owner, FLOOR));
    UUID id = robot.getUUID();
    FarAwayScene scene = FarAwayScene.of(helper, owner, robot);
    ServerPlayer[] back = new ServerPlayer[1];

    helper
        .startSequence()
        .thenWaitUntil(scene::assertLoaded)
        .thenExecute(scene::ownerDies)
        .thenExecute(() -> back[0] = scene.ownerRespawns())
        .thenWaitUntil(() -> assertBeside(helper, id, back[0]))
        .thenExecute(() -> loaded(helper, id).orElseThrow().discard())
        .thenSucceed();
  }

  @GameTest(template = ARENA, timeoutTicks = FarAwayScene.TIMEOUT)
  public static void aCallBringsItFromAnUnloadedChunkAndFreesIt(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = withItem(GameTestRobots.placeNewRobot(helper, owner, FLOOR));
    UUID id = robot.getUUID();
    FarAwayScene scene = FarAwayScene.of(helper, owner, robot);

    helper
        .startSequence()
        .thenWaitUntil(scene::assertLoaded)
        .thenExecute(() -> scene.ownerLeaves(home(helper)))
        .thenWaitUntil(scene::assertUnloaded)
        .thenExecute(() ->
            helper.assertValueEqual(RobotCalls.call(owner, id), CallResult.SEARCHING, WRONG_RESULT))
        .thenExecute(() ->
            helper.assertValueEqual(RobotCalls.call(owner, id), CallResult.BUSY, WRONG_RESULT))
        .thenWaitUntil(() -> assertBeside(helper, id, owner))
        .thenIdle(FREE_WITHIN)
        .thenExecute(() -> helper.assertTrue(scene.chunkIsFree(), HELD))
        .thenExecute(() ->
            helper.assertTrue(
                TabletSelection.entriesFor(owner).getFirst().where().loaded(), NOT_ONE))
        .thenSucceed();
  }

  /** The search stops with its owner, and the Cubit stays where it is (answer 2485). */
  @GameTest(template = ARENA, timeoutTicks = FarAwayScene.TIMEOUT)
  public static void aDeathEndsTheSearch(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = withItem(GameTestRobots.placeNewRobot(helper, owner, FLOOR));
    UUID id = robot.getUUID();
    FarAwayScene scene = FarAwayScene.of(helper, owner, robot);

    helper
        .startSequence()
        .thenWaitUntil(scene::assertLoaded)
        .thenExecute(() -> scene.ownerLeaves(home(helper)))
        .thenWaitUntil(scene::assertUnloaded)
        .thenExecute(() -> {
          RobotCalls.call(owner, id);
          scene.ownerDies();
        })
        .thenIdle(AFTER_A_WHILE)
        .thenExecute(() -> helper.assertTrue(loaded(helper, id).isEmpty(), MOVED))
        .thenIdle(FREE_WITHIN)
        .thenExecute(() -> helper.assertTrue(scene.chunkIsFree(), HELD))
        .thenExecute(() -> {
          var entries = TabletSelection.entriesFor(owner);
          helper.assertValueEqual(entries.size(), 1, NOT_ONE);
          helper.assertFalse(entries.getFirst().where().loaded(), MOVED);
        })
        .thenSucceed();
  }

  /** A snapshot of a Cubit that is not there anymore makes none, and the search gives up (2465). */
  @GameTest(template = ARENA, timeoutTicks = FarAwayScene.TIMEOUT)
  public static void aCubitThatIsNotThereIsNeverMade(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    UUID id = robot.getUUID();
    RobotEntity ghost = GameTestRobots.loadRobot(helper, robot.saveWithoutId(new CompoundTag()));
    BlockPos away = robot.blockPosition().south(FarAwayScene.AWAY);
    ghost.moveTo(away.getX(), away.getY(), away.getZ());
    robot.discard();
    NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(ghost, helper.getLevel()));

    helper
        .startSequence()
        .thenExecute(() ->
            helper.assertValueEqual(RobotCalls.call(owner, id), CallResult.SEARCHING, WRONG_RESULT))
        .thenIdle(CubitMod.CONFIG.calls().timeoutTicks() + AFTER_A_WHILE)
        .thenExecute(() -> helper.assertTrue(loaded(helper, id).isEmpty(), NOT_ONE))
        .thenExecute(
            () -> helper.assertFalse(RobotCalls.call(owner, id) == CallResult.BUSY, WRONG_RESULT))
        .thenSucceed();
  }

  /** From another dimension, with what it carries, and also the one that follows (2450, 2501). */
  @GameTest(template = ARENA, timeoutTicks = FarAwayScene.TIMEOUT)
  public static void aCallBringsItFromAnotherDimension(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = withItem(GameTestRobots.placeNewRobot(helper, owner, FLOOR));
    goHome(helper, owner);
    UUID id = robot.getUUID();
    ServerLevel nether = helper.getLevel().getServer().getLevel(Level.NETHER);
    nether.setChunkForced(NETHER_CHUNK, NETHER_CHUNK, true);
    for (int x = -CHUNKS_AROUND; x <= CHUNKS_AROUND; x++) {
      for (int z = -CHUNKS_AROUND; z <= CHUNKS_AROUND; z++) {
        nether.getChunk(NETHER_CHUNK + x, NETHER_CHUNK + z);
      }
    }
    Vec3 spot = new Vec3(NETHER_CHUNK * CHUNK + HALF, NETHER_Y, NETHER_CHUNK * CHUNK + HALF);
    robot.changeDimension(
        new DimensionTransition(nether, spot, Vec3.ZERO, 0, 0, DimensionTransition.DO_NOTHING));

    helper
        .startSequence()
        .thenWaitUntil(
            () -> helper.assertTrue(nether.getEntity(id) instanceof RobotEntity, NOT_NEAR))
        .thenExecute(() -> {
          helper.assertValueEqual(RobotCalls.call(owner, id), CallResult.ARRIVED, WRONG_RESULT);
          nether.setChunkForced(NETHER_CHUNK, NETHER_CHUNK, false);
        })
        .thenWaitUntil(() -> assertBeside(helper, id, owner))
        .thenSucceed();
  }

  @GameTest(template = ARENA)
  public static void withoutASafeSpotItStays(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = withItem(GameTestRobots.placeNewRobot(helper, owner, FLOOR));
    Vec3 before = robot.position();
    owner.moveTo(before.x, before.y + IN_THE_AIR, before.z);

    helper.assertValueEqual(
        RobotCalls.call(owner, robot.getUUID()), CallResult.NO_SPOT, WRONG_RESULT);
    helper.assertTrue(robot.position().equals(before), MOVED);
    helper.succeed();
  }

  /** It comes switched off, and the one that follows keeps following (answer 2483). */
  @GameTest(template = ARENA)
  public static void withoutChargeItComesSwitchedOff(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity following = DockScene.placeRobot(helper, owner, FLOOR, CHARGE);
    RobotEntity empty = DockScene.placeRobot(helper, owner, SECOND_FLOOR, Energy.ZERO);
    goHome(helper, owner);
    empty.moveTo(empty.getX() + FAR, empty.getY(), empty.getZ());

    helper.assertValueEqual(
        RobotCalls.call(owner, empty.getUUID()), CallResult.ARRIVED, WRONG_RESULT);
    helper.assertTrue(empty.distanceTo(owner) < ARRIVAL, NOT_NEAR);
    helper.assertFalse(empty.powerState() == PowerState.ACTIVE, WRONG_STATE);
    helper.assertValueEqual(follower(owner), Optional.of(following.getUUID()), WRONG_FOLLOWER);
    helper.assertTrue(following.powerState() == PowerState.ACTIVE, WRONG_STATE);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void closeByItWalks(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity first = DockScene.placeRobot(helper, owner, FLOOR, CHARGE);
    RobotEntity second = DockScene.placeRobot(helper, owner, SECOND_FLOOR, CHARGE);
    goHome(helper, owner);
    Vec3 before = second.position();

    helper.assertValueEqual(
        RobotCalls.call(owner, second.getUUID()), CallResult.COMING, WRONG_RESULT);
    helper.assertTrue(second.position().equals(before), MOVED);
    helper.assertTrue(second.powerState() == PowerState.ACTIVE, WRONG_STATE);
    helper.assertTrue(first.powerState() == PowerState.INACTIVE_SWITCHED_OFF, WRONG_STATE);
    helper.assertValueEqual(RobotCalls.call(owner, first.getUUID()), CallResult.WAIT, WRONG_RESULT);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void nobodyCallsTheCubitOfAnother(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    ServerPlayer stranger = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    Vec3 before = robot.position();

    helper.assertValueEqual(
        RobotCalls.call(stranger, robot.getUUID()), CallResult.NOT_FOUND, WRONG_RESULT);
    helper.assertTrue(robot.position().equals(before), MOVED);
    helper.succeed();
  }
}
