package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.gametest.NetworkScene.TestNetwork;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Network app of the Tablet: the antennas, Docks and chests of the chosen Cubit, their
 * refusals, the energy of a Dock without a Cubit, and the energy the antenna gives its Docks above
 * its reserve (Delivery 5, details 4 to 6, 10 to 14 and 17 and 18, and answers 2599 to 2604).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkAppGameTests {
  private static final Energy FULL_ANTENNA = new Energy(600_000);
  private static final Energy AT_THE_RESERVE = new Energy(300_000);
  private static final Energy CHARGE = new Energy(100_000);
  private static final float HEALTH = 25f;
  private static final int FEED_TICKS = 40;
  private static final String NOT_LINKED = "The chosen Cubit should link what was clicked";
  private static final String LINKED = "The click should have been refused";
  private static final String NOT_FED = "The antenna should feed the Dock";
  private static final String FED = "The antenna should no longer feed the Dock";
  private static final String KEPT = "Unlinking the antenna should take its Docks out";
  private static final String NO_ENERGY = "The Dock should get energy from its antenna";
  private static final String FROM_RESERVE = "The antenna should keep its reserve from the Docks";
  private static final String NOT_FORGOTTEN = "Picking up the Cubit should forget its links";

  private NetworkAppGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void theChosenCubitLinksItsAntennaDockAndChest(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = NetworkScene.network(helper, owner, FULL_ANTENNA);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    BlockPos chest = chest(helper, NetworkScene.chestFloor(helper));

    NetworkAppClicks.choose(owner, robot);
    NetworkAppClicks.click(owner, network.antenna().getBlockPos());
    NetworkAppClicks.click(owner, network.dock().getBlockPos());
    NetworkAppClicks.click(owner, chest);

    var links = robot.docking().network().links();
    helper.assertValueEqual(links.antennas().size(), 1, NOT_LINKED);
    helper.assertValueEqual(links.docks().size(), 1, NOT_LINKED);
    helper.assertValueEqual(links.chests().size(), 1, NOT_LINKED);
    helper.assertTrue(network.antenna().feeds(network.dock().getBlockPos()), NOT_FED);
    helper.succeed();
  }

  /** A Base Cubit links no antenna, and so marks nothing (answer 2226). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aBaseCubitLinksNoAntenna(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = NetworkScene.network(helper, owner, FULL_ANTENNA);
    RobotEntity robot = robot(helper, owner, RobotTier.BASE);

    NetworkAppClicks.choose(owner, robot);
    NetworkAppClicks.click(owner, network.antenna().getBlockPos());
    NetworkAppClicks.click(owner, network.dock().getBlockPos());

    helper.assertTrue(robot.docking().network().links().isEmpty(), LINKED);
    helper.succeed();
  }

  /** A chest out of the chunks of the antennas of the Cubit is refused (detail 10). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aChestOutOfTheChunksIsRefused(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = NetworkScene.network(helper, owner, FULL_ANTENNA);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    BlockPos chest = chest(helper, NetworkScene.nextChunkFloor(helper));

    NetworkAppClicks.choose(owner, robot);
    NetworkAppClicks.click(owner, network.antenna().getBlockPos());
    NetworkAppClicks.click(owner, chest);

    helper.assertTrue(robot.docking().network().links().chests().isEmpty(), LINKED);
    helper.succeed();
  }

  /**
   * The second click unlinks the antenna, its Dock leaves the Cubit and keeps its energy (2600).
   */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theSecondClickUnlinksTheAntennaWithItsDock(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = NetworkScene.network(helper, owner, FULL_ANTENNA);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    BlockPos antenna = network.antenna().getBlockPos();

    NetworkAppClicks.choose(owner, robot);
    NetworkAppClicks.click(owner, antenna);
    NetworkAppClicks.click(owner, network.dock().getBlockPos());
    NetworkAppClicks.click(owner, antenna);
    helper.assertValueEqual(robot.docking().network().links().antennas().size(), 1, KEPT);
    NetworkAppClicks.click(owner, antenna);

    helper.assertTrue(robot.docking().network().links().isEmpty(), KEPT);
    helper.assertTrue(network.antenna().feeds(network.dock().getBlockPos()), NOT_FED);
    helper.succeed();
  }

  /** Without a Cubit, the antenna and a Dock feed it, and two clicks cut it (2588 and 2604). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void withoutACubitTheAntennaOnlyFeedsTheDock(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = NetworkScene.network(helper, owner, FULL_ANTENNA);
    BlockPos dock = network.dock().getBlockPos();

    NetworkAppClicks.click(owner, network.antenna().getBlockPos());
    NetworkAppClicks.click(owner, dock);
    helper.assertTrue(network.antenna().feeds(dock), NOT_FED);
    NetworkAppClicks.click(owner, dock);
    helper.assertTrue(network.antenna().feeds(dock), NOT_FED);
    NetworkAppClicks.click(owner, dock);

    helper.assertFalse(network.antenna().feeds(dock), FED);
    helper.succeed();
  }

  /** The antenna gives its Dock what passes its reserve (detail 12 and answer 2599). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theAntennaFeedsItsDockAboveItsReserve(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = feeding(helper, owner, FULL_ANTENNA);

    helper.succeedWhen(() -> {
      helper.assertFalse(network.dock().energy().isZero(), NO_ENERGY);
      helper.assertTrue(network.antenna().energy().amount() < FULL_ANTENNA.amount(), NO_ENERGY);
    });
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theAntennaKeepsItsReserveFromTheDocks(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = feeding(helper, owner, AT_THE_RESERVE);

    helper.runAfterDelay(
        FEED_TICKS,
        () -> {
          helper.assertTrue(network.dock().energy().isZero(), FROM_RESERVE);
          helper.assertTrue(network.dock().network().shown().isZero(), FROM_RESERVE);
          helper.succeed();
        });
  }

  /** Picking up the Cubit leaves its links behind (answer 2251). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void pickingUpTheCubitForgetsItsLinks(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    TestNetwork network = NetworkScene.network(helper, owner, FULL_ANTENNA);
    RobotEntity robot = robot(helper, owner, RobotTier.ADVANCED);
    NetworkAppClicks.choose(owner, robot);
    NetworkAppClicks.click(owner, network.antenna().getBlockPos());

    ItemStack item = GameTestRobots.collect(owner, robot);
    RobotEntity placed =
        GameTestRobots.placeRobot(helper, owner, item, NetworkScene.robotFloor(helper));

    helper.assertTrue(placed.docking().network().links().isEmpty(), NOT_FORGOTTEN);
    helper.succeed();
  }

  /** The test network with its Dock fed by the antenna, without a Cubit. */
  private static TestNetwork feeding(GameTestHelper helper, ServerPlayer owner, Energy energy) {
    TestNetwork network = NetworkScene.network(helper, owner, energy);
    NetworkAppClicks.click(owner, network.antenna().getBlockPos());
    NetworkAppClicks.click(owner, network.dock().getBlockPos());
    return network;
  }

  private static RobotEntity robot(GameTestHelper helper, ServerPlayer owner, RobotTier tier) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(CubitDataComponents.ROBOT_STATE.get(), new RobotState(tier, CHARGE, HEALTH));
    return GameTestRobots.placeRobot(helper, owner, item, NetworkScene.robotFloor(helper));
  }

  private static BlockPos chest(GameTestHelper helper, BlockPos floor) {
    helper.setBlock(floor.above(), Blocks.CHEST);
    return helper.absolutePos(floor.above());
  }

  private static ServerPlayer owner(GameTestHelper helper) {
    return GameTestRobots.readyForScreens(
        GameTestRobots.onlinePlayerAt(helper, NetworkScene.robotFloor(helper)));
  }
}
