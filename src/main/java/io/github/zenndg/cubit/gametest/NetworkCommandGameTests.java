package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkCommands;
import io.github.zenndg.cubit.block.NetworkData;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The command of the operator: releasing the chunks of a player switches the chunk of each block
 * off, so it does not take it back (answers 2286 and 2578).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkCommandGameTests {
  /** The chunk is checked once a second. */
  private static final int CHECK_TICKS = 45;

  private static final Energy PLENTY = new Energy(1_000_000);
  private static final String NOT_HOLDING = "The server should hold its chunk first";
  private static final String NOT_RELEASED = "The command should release the chunk";
  private static final String TAKEN_BACK = "The server should not take its chunk back";

  private NetworkCommandGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void releasingSwitchesTheChunkOff(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(helper, owner, RobotTier.BASE, PLENTY);
    server.setChunkLoading(true);
    GlobalPos place = GlobalPos.of(helper.getLevel().dimension(), server.getBlockPos());

    helper.runAfterDelay(
        CHECK_TICKS,
        () -> {
          helper.assertValueEqual(server.chunkState(), ChunkState.HOLDING, NOT_HOLDING);
          int released =
              NetworkCommands.release(helper.getLevel().getServer(), Optional.of(owner.getUUID()));
          helper.assertValueEqual(released, 1, NOT_RELEASED);
          helper.assertFalse(server.chunkLoading(), NOT_RELEASED);
          helper.runAfterDelay(
              CHECK_TICKS,
              () -> {
                helper.assertValueEqual(server.chunkState(), ChunkState.SWITCHED_OFF, TAKEN_BACK);
                helper.assertFalse(
                    NetworkData.of(helper.getLevel().getServer()).chunks().holds(place),
                    TAKEN_BACK);
                helper.succeed();
              });
        });
  }
}
