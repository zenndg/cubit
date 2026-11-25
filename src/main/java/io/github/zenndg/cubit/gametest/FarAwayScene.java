package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.entity.RobotEntity;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * An owner and a Cubit in a chunk that only the owner keeps loaded, far from the arena, which the
 * test server never unloads. The connection of the owner is not ticked by the server, so the scene
 * ticks the dead owner, whom the game then takes out of the world.
 */
final class FarAwayScene {
  static final int TIMEOUT = 2_000;
  static final int AWAY = 600;
  private static final double HALF = 0.5;
  private static final double BESIDE = 2;
  private static final String NOT_LOADED = "The far chunk should be loaded";
  private static final String STILL_LOADED = "The far chunk should have unloaded";

  private final GameTestHelper helper;
  private final ServerPlayer owner;
  private final UUID robot;
  private final BlockPos place;

  private FarAwayScene(GameTestHelper helper, ServerPlayer owner, UUID robot, BlockPos place) {
    this.helper = helper;
    this.owner = owner;
    this.robot = robot;
    this.place = place;
  }

  static FarAwayScene of(GameTestHelper helper, ServerPlayer owner, RobotEntity robot) {
    BlockPos place = robot.blockPosition().south(AWAY);
    ServerLevel level = helper.getLevel();
    level.getChunk(place);
    moveOwner(owner, place);
    robot.moveTo(place.getX() + BESIDE, place.getY(), place.getZ() + HALF);
    FarAwayScene scene = new FarAwayScene(helper, owner, robot.getUUID(), place);
    helper.onEachTick(scene::tickTheDead);
    return scene;
  }

  private static void moveOwner(ServerPlayer owner, BlockPos place) {
    owner.moveTo(place.getX() + HALF, place.getY(), place.getZ() + HALF);
    owner.serverLevel().getChunkSource().move(owner);
  }

  private void tickTheDead() {
    if (owner.isDeadOrDying() && !owner.isRemoved()) {
      owner.doTick();
    }
  }

  void assertLoaded() {
    ServerLevel level = helper.getLevel();
    helper.assertTrue(
        level.isPositionEntityTicking(place) && level.getEntity(robot) != null, NOT_LOADED);
  }

  void assertUnloaded() {
    helper.assertTrue(helper.getLevel().getEntity(robot) == null, STILL_LOADED);
  }

  /** Nothing holds the far chunk anymore. */
  boolean chunkIsFree() {
    return !helper.getLevel().isLoaded(place);
  }

  /** The owner leaves alone, as by a command, and the far chunk unloads behind. */
  void ownerLeaves(Vec3 there) {
    moveOwner(owner, BlockPos.containing(there));
  }

  /** A real death, as the game does it. */
  void ownerDies() {
    owner.setHealth(0);
    owner.die(owner.damageSources().genericKill());
  }

  /** The click on the respawn button. */
  ServerPlayer ownerRespawns() {
    return helper
        .getLevel()
        .getServer()
        .getPlayerList()
        .respawn(owner, false, Entity.RemovalReason.KILLED);
  }
}
