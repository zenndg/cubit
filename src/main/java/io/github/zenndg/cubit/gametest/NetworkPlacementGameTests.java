package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkBlock;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitBlocks;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Where the blocks of the Network go, who breaks them and how they pair (answers 2255, 2259, 2268
 * and 2271).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkPlacementGameTests {
  private static final float EXPLOSION_POWER = 8f;
  private static final String NO_OWNER = "Who places it owns it";
  private static final String PLACED = "It should be refused";
  private static final String KEPT = "The refused item should stay in the hand";
  private static final String NOT_PAIRED = "The server and the relay should be paired";
  private static final String PAIRED = "They should not be paired";
  private static final String WRONG_TIER = "The relay should show the tier of its server";
  private static final String STRANGER_MINES = "Only the owner mines it";
  private static final String MOVABLE = "A piston should not move it";

  private NetworkPlacementGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void theServerTakesTwoBlocksAndHasAnOwner(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);

    helper.assertBlockPresent(
        CubitBlocks.NETWORK_SERVER.get(), NetworkScene.serverFloor(helper).above(2));
    helper.assertValueEqual(server.owner(), Optional.of(owner.getUUID()), NO_OWNER);
    helper.assertTrue(server.isFirst(), NO_OWNER);
    helper.succeed();
  }

  /** One of each per chunk, of any owner (answer 2271). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aSecondServerInTheChunkIsRefused(GameTestHelper helper) {
    server(helper, player(helper));

    assertRefused(helper, CubitItems.NETWORK_SERVER.get().getDefaultInstance());
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aSecondRelayInTheChunkIsRefused(GameTestHelper helper) {
    NetworkScene.placeRelay(helper, player(helper));

    assertRefused(helper, CubitItems.SOLAR_RELAY.get().getDefaultInstance());
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theNextChunkTakesAnotherServer(GameTestHelper helper) {
    Player owner = player(helper);
    server(helper, owner);
    BlockPos other = NetworkScene.nextChunkFloor(helper);

    GameTestRobots.use(helper, owner, CubitItems.NETWORK_SERVER.get().getDefaultInstance(), other);

    helper.assertBlockPresent(CubitBlocks.NETWORK_SERVER.get(), other.above());
    helper.succeed();
  }

  /** It needs a solid top only to be placed, and stays without it (answer 2255). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void itNeedsASolidTopOnlyToBePlaced(GameTestHelper helper) {
    Player owner = player(helper);
    BlockPos floor = NetworkScene.otherFloor(helper);
    helper.setBlock(floor, Blocks.OAK_FENCE);
    useOn(helper, owner, CubitItems.NETWORK_SERVER.get().getDefaultInstance(), floor);
    helper.assertBlockNotPresent(CubitBlocks.NETWORK_SERVER.get(), floor.above());

    server(helper, owner);
    helper.setBlock(NetworkScene.serverFloor(helper), Blocks.AIR);

    helper.assertBlockPresent(
        CubitBlocks.NETWORK_SERVER.get(), NetworkScene.serverFloor(helper).above());
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void itNeedsRoomForItsTwoBlocks(GameTestHelper helper) {
    BlockPos floor = NetworkScene.otherFloor(helper);
    helper.setBlock(floor.above(2), Blocks.STONE);

    assertRefused(helper, CubitItems.SOLAR_RELAY.get().getDefaultInstance());
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theServerAndTheRelayOfTheOwnerPair(GameTestHelper helper) {
    Player owner = player(helper);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);
    helper.assertTrue(relay.server().isEmpty(), PAIRED);

    NetworkServerBlockEntity server =
        NetworkScene.placeServer(helper, owner, RobotTier.ELITE, Energy.ZERO);

    helper.assertValueEqual(relay.server(), Optional.of(server), NOT_PAIRED);
    helper.assertValueEqual(server.relay(), Optional.of(relay), NOT_PAIRED);
    assertRelayShows(helper, RobotTier.ELITE);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theRelayOfAnotherOwnerDoesNotPair(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, player(helper));
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, player(helper));

    helper.assertTrue(server.relay().isEmpty() && relay.server().isEmpty(), PAIRED);
    helper.succeed();
  }

  /** Without its server the band goes back to grey (answer 2374). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void breakingTheServerLeavesTheRelayAlone(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkScene.placeServer(helper, owner, RobotTier.ULTIMATE, Energy.ZERO);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);
    assertRelayShows(helper, RobotTier.ULTIMATE);

    helper.destroyBlock(NetworkScene.serverFloor(helper).above());

    helper.assertTrue(relay.server().isEmpty(), PAIRED);
    assertRelayShows(helper, RobotTier.BASE);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void breakingTheRelayLeavesTheServerAlone(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    NetworkScene.placeRelay(helper, owner);

    helper.destroyBlock(NetworkScene.relayFloor(helper).above(2));

    helper.assertTrue(server.relay().isEmpty(), PAIRED);
    helper.succeed();
  }

  /** With two put by a command, only the one at the lowest position counts (detail 1). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void onlyTheFirstOfTwoServersCounts(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity first = server(helper, owner);
    BlockPos second = NetworkScene.otherFloor(helper).above();
    helper.setBlock(second, CubitBlocks.NETWORK_SERVER.get());
    NetworkServerBlockEntity other = helper.getBlockEntity(second);
    NetworkScene.placeRelay(helper, owner);

    helper.assertTrue(first.isFirst() && first.relay().isPresent(), NOT_PAIRED);
    helper.assertFalse(other.isFirst() || other.relay().isPresent(), PAIRED);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aStrangerInSurvivalCannotMineIt(GameTestHelper helper) {
    Player owner = player(helper);
    Player stranger = player(helper);
    server(helper, owner);
    BlockPos pos = NetworkScene.serverFloor(helper).above();
    BlockState state = helper.getBlockState(pos);

    helper.assertTrue(
        state.getDestroyProgress(owner, helper.getLevel(), helper.absolutePos(pos)) > 0, NO_OWNER);
    helper.assertFalse(
        state.onDestroyedByPlayer(
            helper.getLevel(), helper.absolutePos(pos), stranger, true, state.getFluidState()),
        STRANGER_MINES);
    helper.assertBlockPresent(CubitBlocks.NETWORK_SERVER.get(), pos);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void explosionsAndPistonsDoNotMoveIt(GameTestHelper helper) {
    server(helper, player(helper));
    BlockPos pos = NetworkScene.serverFloor(helper).above();
    Vec3 center = helper.absoluteVec(Vec3.atCenterOf(pos.above(2)));

    helper
        .getLevel()
        .explode(
            null, center.x, center.y, center.z, EXPLOSION_POWER, Level.ExplosionInteraction.TNT);

    helper.assertBlockPresent(CubitBlocks.NETWORK_SERVER.get(), pos);
    helper.assertValueEqual(
        helper.getBlockState(pos).getPistonPushReaction(), PushReaction.BLOCK, MOVABLE);
    helper.succeed();
  }

  private static Player player(GameTestHelper helper) {
    return helper.makeMockPlayer(GameType.SURVIVAL);
  }

  private static NetworkServerBlockEntity server(GameTestHelper helper, Player owner) {
    return NetworkScene.placeServer(helper, owner, RobotTier.BASE, Energy.ZERO);
  }

  private static void assertRefused(GameTestHelper helper, ItemStack item) {
    Player player = player(helper);
    BlockPos floor = NetworkScene.otherFloor(helper);

    GameTestRobots.use(helper, player, item, floor);

    helper.assertFalse(
        helper.getBlockState(floor.above()).getBlock() instanceof NetworkBlock, PLACED);
    helper.assertValueEqual(player.getMainHandItem().getCount(), 1, KEPT);
    helper.succeed();
  }

  private static void assertRelayShows(GameTestHelper helper, RobotTier tier) {
    BlockPos lower = NetworkScene.relayFloor(helper).above();
    for (BlockPos pos : new BlockPos[] {lower, lower.above()}) {
      helper.assertValueEqual(
          helper.getBlockState(pos).getValue(NetworkBlock.TIER).tier(), tier, WRONG_TIER);
    }
  }

  private static void useOn(GameTestHelper helper, Player player, ItemStack stack, BlockPos on) {
    BlockPos absolute = helper.absolutePos(on);
    BlockHitResult hit =
        new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
  }
}
