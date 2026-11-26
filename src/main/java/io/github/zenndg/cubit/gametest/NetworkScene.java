package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.SavedBigStack;
import io.github.zenndg.cubit.item.SavedLink;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Builds a network in the one chunk that the structure of the test always holds whole, so no test
 * shares a chunk with another.
 */
final class NetworkScene {
  static final String CHUNK = "chunk";
  private static final BlockPos SERVER = new BlockPos(4, 1, 4);
  private static final BlockPos RELAY = new BlockPos(10, 1, 4);
  private static final BlockPos OTHER = new BlockPos(4, 1, 10);
  private static final BlockPos DOCK = new BlockPos(10, 1, 10);
  private static final BlockPos CHEST = new BlockPos(13, 1, 13);
  private static final BlockPos ROBOT = new BlockPos(7, 1, 13);

  private NetworkScene() {}

  static BlockPos serverFloor(GameTestHelper helper) {
    return inChunk(helper, SERVER);
  }

  static BlockPos relayFloor(GameTestHelper helper) {
    return inChunk(helper, RELAY);
  }

  /** Another place of the same chunk, for a second block of a kind. */
  static BlockPos otherFloor(GameTestHelper helper) {
    return inChunk(helper, OTHER);
  }

  /** Where the Dock of the test network stands, in the chunk of its antenna. */
  static BlockPos dockFloor(GameTestHelper helper) {
    return inChunk(helper, DOCK);
  }

  /** Where a chest stands, in the chunk of the antenna. */
  static BlockPos chestFloor(GameTestHelper helper) {
    return inChunk(helper, CHEST);
  }

  static BlockPos robotFloor(GameTestHelper helper) {
    return inChunk(helper, ROBOT);
  }

  /** The last block before the chunk or the first one after it, whichever the structure has. */
  static BlockPos nextChunkFloor(GameTestHelper helper) {
    BlockPos corner = inChunk(helper, BlockPos.ZERO);
    int x = corner.getX() > 0 ? -1 : SectionPos.SECTION_SIZE;
    return inChunk(helper, new BlockPos(x, SERVER.getY(), SERVER.getZ()));
  }

  private static BlockPos inChunk(GameTestHelper helper, BlockPos offset) {
    BlockPos origin = helper.absolutePos(BlockPos.ZERO);
    int x = Math.floorMod(-origin.getX(), SectionPos.SECTION_SIZE);
    int z = Math.floorMod(-origin.getZ(), SectionPos.SECTION_SIZE);
    return offset.offset(x, 0, z);
  }

  static ItemStack serverItem(RobotTier tier, Energy energy, SavedBigStack... items) {
    ItemStack item = new ItemStack(CubitItems.NETWORK_SERVER.get());
    item.set(
        CubitDataComponents.NETWORK_STATE.get(),
        new SavedNetwork(
            tier, energy.amount(), List.of(items), Optional.empty(), true, false, SavedLink.NONE));
    return item;
  }

  static NetworkServerBlockEntity placeServer(
      GameTestHelper helper, Player owner, RobotTier tier, Energy energy) {
    return placeServer(helper, owner, serverItem(tier, energy));
  }

  static NetworkServerBlockEntity placeServer(GameTestHelper helper, Player owner, ItemStack item) {
    GameTestRobots.use(helper, owner, item, serverFloor(helper));
    return helper.getBlockEntity(serverFloor(helper).above());
  }

  static SolarRelayBlockEntity placeRelay(GameTestHelper helper, Player owner) {
    ItemStack item = new ItemStack(CubitItems.SOLAR_RELAY.get());
    GameTestRobots.use(helper, owner, item, relayFloor(helper));
    return helper.getBlockEntity(relayFloor(helper).above());
  }

  /** An antenna linked to the server of the scene, in the same chunk. */
  static SignalAntennaBlockEntity placeAntenna(GameTestHelper helper, Player owner) {
    ItemStack item = new ItemStack(CubitItems.SIGNAL_ANTENNA.get());
    clickServer(helper, owner, item);
    GameTestRobots.use(helper, owner, item, otherFloor(helper));
    return helper.getBlockEntity(otherFloor(helper).above());
  }

  /** An antenna with energy in its battery, linked to the server of the scene. */
  static SignalAntennaBlockEntity placeAntenna(GameTestHelper helper, Player owner, Energy energy) {
    ItemStack item = new ItemStack(CubitItems.SIGNAL_ANTENNA.get());
    item.set(CubitDataComponents.NETWORK_STATE.get(), SavedNetwork.onlyEnergy(energy.amount()));
    clickServer(helper, owner, item);
    GameTestRobots.use(helper, owner, item, otherFloor(helper));
    return helper.getBlockEntity(otherFloor(helper).above());
  }

  /**
   * The test network of Delivery 5 (detail 17): a Server, without its chunk switch so it never
   * fights the structure for it, and an antenna with energy, in one chunk, and a Dock beside them.
   * The areas of the next deliveries are drawn inside it.
   */
  static TestNetwork network(GameTestHelper helper, Player owner, Energy antennaEnergy) {
    NetworkServerBlockEntity server = placeServer(helper, owner, RobotTier.BASE, Energy.ZERO);
    server.setChunkLoading(false);
    SignalAntennaBlockEntity antenna = placeAntenna(helper, owner, antennaEnergy);
    InductionDockBlockEntity dock =
        DockScene.placeDock(helper, owner, dockFloor(helper), Energy.ZERO);
    return new TestNetwork(server, antenna, dock);
  }

  /** The blocks of the test network. */
  record TestNetwork(
      NetworkServerBlockEntity server,
      SignalAntennaBlockEntity antenna,
      InductionDockBlockEntity dock) {}

  static void clickServer(GameTestHelper helper, Player player, ItemStack stack) {
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    DockScene.clickWithHeldItem(helper, player, serverFloor(helper).above());
  }

  static void clickRelay(GameTestHelper helper, Player player, ItemStack stack) {
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    DockScene.clickWithHeldItem(helper, player, relayFloor(helper).above());
  }
}
