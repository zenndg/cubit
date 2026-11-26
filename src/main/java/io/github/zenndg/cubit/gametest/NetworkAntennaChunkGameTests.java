package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkData;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The chunk of a Signal Antenna: it holds its own, shares the one its Server holds and follows its
 * switch (answers 2213, 2264 and 2553).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkAntennaChunkGameTests {
  /** The antenna fills with the energy of its server, and its chunk is checked once a second. */
  private static final int SETTLE_TICKS = 70;

  private static final Energy PLENTY = new Energy(1_000_000);
  private static final String WRONG_STATE = "Wrong state of the chunk of the antenna";
  private static final String WRONG_LIST = "Wrong list of chunks in the data of the world";

  private NetworkAntennaChunkGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void anAntennaInAnotherChunkHoldsIt(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    serverWithRelay(helper, owner);
    BlockPos floor = NetworkScene.nextChunkFloor(helper);
    SignalAntennaBlockEntity antenna = place(helper, owner, linked(helper, owner), floor);

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(antenna.chunkState(), ChunkState.HOLDING, WRONG_STATE);
          helper.assertTrue(
              NetworkData.of(helper.getLevel().getServer())
                  .chunks()
                  .holds(GlobalPos.of(helper.getLevel().dimension(), antenna.getBlockPos())),
              WRONG_LIST);
          helper.succeed();
        });
  }

  /** In the chunk its Server holds, it pays nothing for it again (answer 2264). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void anAntennaSharesTheChunkOfItsServer(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    serverWithRelay(helper, owner);
    SignalAntennaBlockEntity antenna = NetworkScene.placeAntenna(helper, owner);

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(antenna.chunkState(), ChunkState.SHARED, WRONG_STATE);
          helper.succeed();
        });
  }

  /** The switch is kept in the item, as in the Server (answer 2213). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void anAntennaSwitchedOffLetsItsChunkGo(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    serverWithRelay(helper, owner);
    ItemStack item = linked(helper, owner);
    SavedNetwork saved = SavedNetwork.of(item);
    item.set(
        CubitDataComponents.NETWORK_STATE.get(),
        new SavedNetwork(
            saved.tier(),
            saved.energy(),
            saved.items(),
            saved.ups(),
            false,
            saved.showWall(),
            saved.link()));
    SignalAntennaBlockEntity antenna =
        place(helper, owner, item, NetworkScene.nextChunkFloor(helper));

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(antenna.chunkState(), ChunkState.SWITCHED_OFF, WRONG_STATE);
          helper.assertFalse(antenna.chunkLoading(), WRONG_STATE);
          helper.succeed();
        });
  }

  private static void serverWithRelay(GameTestHelper helper, ServerPlayer owner) {
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(helper, owner, RobotTier.BASE, PLENTY);
    NetworkScene.placeRelay(helper, owner);
    server.setChunkLoading(true);
  }

  private static ItemStack linked(GameTestHelper helper, ServerPlayer owner) {
    ItemStack item = new ItemStack(CubitItems.SIGNAL_ANTENNA.get());
    NetworkScene.clickServer(helper, owner, item);
    return item;
  }

  private static SignalAntennaBlockEntity place(
      GameTestHelper helper, ServerPlayer owner, ItemStack item, BlockPos floor) {
    GameTestRobots.use(helper, owner, item, floor);
    return helper.getBlockEntity(floor.above());
  }
}
