package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.BlockNaming;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.network.BlockNamedPayload;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The window of the name of a new Dock, Network Server and Solar Relay (answers 2420, 2422). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BlockNamingGameTests {
  private static final String ARENA = "arena";
  private static final String GIVEN = "Home";
  private static final String LATER = "Away";
  private static final String ANVIL = "Anvil";
  private static final String NO_NAME = "";
  private static final int FAR = 40;
  private static final String NOT_NAMED = "The window should name the block";
  private static final String NAMED_TWICE = "The window answers only once";
  private static final String NAMED_BY_OTHER = "Only who placed it answers the window";
  private static final String NAMED = "The window should not be open";
  private static final String WRONG_STANDARD = "The standard name should stay";

  private BlockNamingGameTests() {}

  @GameTest(template = ARENA)
  public static void theWindowNamesANewDockOnce(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = newDock(helper, owner);

    answer(owner, dock.getBlockPos(), GIVEN);
    helper.assertValueEqual(dock.identity().name().value(), GIVEN, NOT_NAMED);
    answer(owner, dock.getBlockPos(), LATER);
    helper.assertValueEqual(dock.identity().name().value(), GIVEN, NAMED_TWICE);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void anEmptyAnswerKeepsTheStandardName(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = newDock(helper, owner);
    String standard = dock.identity().name().value();

    answer(owner, dock.getBlockPos(), NO_NAME);
    helper.assertValueEqual(dock.identity().name().value(), standard, WRONG_STANDARD);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void onlyWhoPlacedItAnswersTheWindow(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = newDock(helper, owner);
    String standard = dock.identity().name().value();

    answer(DockScene.ownerBeside(helper), dock.getBlockPos(), GIVEN);
    helper.assertValueEqual(dock.identity().name().value(), standard, NAMED_BY_OTHER);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void aFarAnswerNamesNothing(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = newDock(helper, owner);
    String standard = dock.identity().name().value();
    owner.moveTo(owner.getX(), owner.getY() + FAR, owner.getZ());

    answer(owner, dock.getBlockPos(), GIVEN);
    helper.assertValueEqual(dock.identity().name().value(), standard, WRONG_STANDARD);
    helper.succeed();
  }

  /** A broken block keeps its id in the item, so placing it again opens nothing (answer 2422). */
  @GameTest(template = ARENA)
  public static void aBlockPlacedAgainOpensNoWindow(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = newDock(helper, owner);
    answer(owner, dock.getBlockPos(), NO_NAME);
    ItemStack item = new ItemStack(CubitItems.INDUCTION_DOCK.get());
    item.applyComponents(dock.collectComponents());
    helper.destroyBlock(DockScene.DOCK);

    GameTestRobots.use(helper, owner, item, DockScene.FLOOR);
    InductionDockBlockEntity placed = helper.getBlockEntity(DockScene.DOCK);
    String standard = placed.identity().name().value();
    answer(owner, placed.getBlockPos(), GIVEN);

    helper.assertValueEqual(placed.identity().name().value(), standard, NAMED);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void anItemNamedOnAnAnvilOpensNoWindow(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    ItemStack item = new ItemStack(CubitItems.INDUCTION_DOCK.get());
    item.set(DataComponents.CUSTOM_NAME, Component.literal(ANVIL));
    GameTestRobots.use(helper, owner, item, DockScene.FLOOR);
    InductionDockBlockEntity dock = helper.getBlockEntity(DockScene.DOCK);

    answer(owner, dock.getBlockPos(), GIVEN);
    helper.assertValueEqual(dock.identity().name().value(), ANVIL, NAMED);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theWindowNamesTheBlocksOfTheNetwork(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, NetworkScene.otherFloor(helper));
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(
            helper, owner, CubitItems.NETWORK_SERVER.get().getDefaultInstance());
    server.setChunkLoading(false);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);
    stand(helper, owner, NetworkScene.serverFloor(helper));
    answer(owner, server.getBlockPos(), GIVEN);
    stand(helper, owner, NetworkScene.relayFloor(helper));
    answer(owner, relay.getBlockPos(), LATER);

    helper.assertValueEqual(server.identity().name().value(), GIVEN, NOT_NAMED);
    helper.assertValueEqual(relay.identity().name().value(), LATER, NOT_NAMED);
    helper.succeed();
  }

  private static InductionDockBlockEntity newDock(GameTestHelper helper, ServerPlayer owner) {
    return DockScene.placeDock(helper, owner, DockScene.FLOOR, Energy.ZERO);
  }

  private static void answer(ServerPlayer player, BlockPos pos, String name) {
    BlockNaming.answered(player, new BlockNamedPayload(pos, name));
  }

  private static void stand(GameTestHelper helper, ServerPlayer player, BlockPos floor) {
    BlockPos beside = helper.absolutePos(floor.south(2).above());
    player.moveTo(beside, 0, 0);
  }
}
