package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.PowerBankItem;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The energy of the Network Server and of the Solar Relay (answers 2217 to 2223 and 2326). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkEnergyGameTests {
  private static final int TIMEOUT = 400;

  /** The 3 s of the delay and the 4 s of the motion, with room to spare. */
  private static final int OPEN_TICKS = 250;

  private static final int SLOT = 0;
  private static final int DUST_COUNT = 3;

  /** A Base server and its relay. */
  private static final long UPKEEP = 22;

  private static final int SLOT_TICKS = 10;
  private static final int BANK_TICKS = 20;
  private static final int OVER_THE_RELAY = 3;
  private static final int PUSHED = 1_000_000;
  private static final int BASE_INPUT = 20_000;
  private static final int ULTIMATE_INPUT = 160_000;
  private static final int RELAY_INPUT = 5_000;
  private static final long DUST_ENERGY = 5_000;
  private static final long BASE_SUN = 500;
  private static final long ULTIMATE_SUN = 4_000;
  private static final long ULTIMATE_MOON = 1_000;
  private static final Energy BANK_CHARGE = new Energy(60_000);
  private static final Energy BANK_CAPACITY = new Energy(210_000);
  private static final Energy KEPT = new Energy(400_000);
  private static final long ADVANCED_CAPACITY = 4_800_000;
  private static final String WRONG_INPUT = "The input should stop at its limit per tick";
  private static final String GIVES = "No energy should ever leave by a cable";
  private static final String WRONG_SUM = "Energy was created or lost";
  private static final String NOT_GENERATED = "The sun should go straight to the server";
  private static final String GENERATED = "It should not generate";
  private static final String NOT_OPEN = "The panel should be open";
  private static final String NOT_STORED = "The panel should stay stored";
  private static final String WRONG_TIER = "Wrong tier of the server";
  private static final String WRONG_HAND = "Wrong Module left in the hand";

  private NetworkEnergyGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void theCablesStopAtTheInputOfTheTier(GameTestHelper helper) {
    Player owner = player(helper);
    assertInput(
        helper,
        NetworkScene.placeServer(helper, owner, RobotTier.BASE, Energy.ZERO).energyStorage(),
        BASE_INPUT);
    assertInput(helper, NetworkScene.placeRelay(helper, owner).energyStorage(), RELAY_INPUT);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theUltimateTakesMoreByCable(GameTestHelper helper) {
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(helper, player(helper), RobotTier.ULTIMATE, Energy.ZERO);

    helper.runAfterDelay(
        1,
        () -> {
          assertInput(helper, server.energyStorage(), ULTIMATE_INPUT);
          helper.succeed();
        });
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void redstoneFeedsBothBlocks(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(helper, owner, RobotTier.BASE, Energy.ZERO);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);
    server.setChunkLoading(false);
    server.slot().setItem(SLOT, new ItemStack(Items.REDSTONE, DUST_COUNT));
    relay.slot().setItem(SLOT, new ItemStack(Items.REDSTONE, DUST_COUNT));

    helper.runAfterDelay(
        SLOT_TICKS,
        () -> {
          long spent = DUST_ENERGY * DUST_COUNT - server.energy().amount();
          helper.assertTrue(spent > 0 && spent <= UPKEEP * (SLOT_TICKS + 1), WRONG_SUM);
          helper.assertTrue(spent % UPKEEP == 0, WRONG_SUM);
          helper.assertValueEqual(relay.energy().amount(), DUST_ENERGY * DUST_COUNT, WRONG_SUM);
          helper.succeed();
        });
  }

  /** The bank in the slot empties into the battery and stays there (answer 2263). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aBankInTheSlotEmptiesIntoTheBattery(GameTestHelper helper) {
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, player(helper));
    ItemStack bank = CubitItems.BASE_POWER_BANK.get().getDefaultInstance();
    PowerBankItem.store(bank, new Battery(BANK_CAPACITY, BANK_CHARGE));
    relay.slot().setItem(SLOT, bank);

    helper.runAfterDelay(
        BANK_TICKS,
        () -> {
          ItemStack left = relay.slot().getItem(SLOT);
          helper.assertTrue(PowerBankItem.batteryOf(left).isEmpty(), WRONG_SUM);
          helper.assertValueEqual(relay.energy(), BANK_CHARGE, WRONG_SUM);
          helper.succeed();
        });
  }

  /** Straight to the server, and never through the battery of the relay (detail 18). */
  @GameTest(
      template = NetworkScene.CHUNK,
      timeoutTicks = TIMEOUT,
      batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void theSunGoesStraightToTheServer(GameTestHelper helper) {
    assertGenerates(helper, RobotTier.BASE, BASE_SUN);
  }

  @GameTest(
      template = NetworkScene.CHUNK,
      timeoutTicks = TIMEOUT,
      batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void theRelayFollowsTheTierOfItsServer(GameTestHelper helper) {
    assertGenerates(helper, RobotTier.ULTIMATE, ULTIMATE_SUN);
  }

  /** The default batch starts at midnight. */
  @GameTest(template = NetworkScene.CHUNK, timeoutTicks = TIMEOUT)
  public static void theUltimateGeneratesByTheMoon(GameTestHelper helper) {
    assertGenerates(helper, RobotTier.ULTIMATE, ULTIMATE_MOON);
  }

  @GameTest(template = NetworkScene.CHUNK, timeoutTicks = TIMEOUT)
  public static void theEliteKeepsThePanelInAtNight(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkScene.placeServer(helper, owner, RobotTier.ELITE, Energy.ZERO);

    assertStored(helper, NetworkScene.placeRelay(helper, owner), SunState.NIGHT);
  }

  /** Without a server the panel stays in, and the battery is kept (answer 2374). */
  @GameTest(
      template = NetworkScene.CHUNK,
      timeoutTicks = TIMEOUT,
      batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void withoutAServerThePanelStaysIn(GameTestHelper helper) {
    assertStored(helper, NetworkScene.placeRelay(helper, player(helper)), SunState.GENERATING);
  }

  @GameTest(
      template = NetworkScene.CHUNK,
      timeoutTicks = TIMEOUT,
      batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void aRoofKeepsThePanelIn(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkScene.placeServer(helper, owner, RobotTier.BASE, Energy.ZERO);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);
    helper.setBlock(NetworkScene.relayFloor(helper).above(OVER_THE_RELAY), Blocks.STONE);

    assertStored(helper, relay, SunState.COVERED);
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theModuleRaisesTheServerAndKeepsAll(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    owner.setGameMode(GameType.SURVIVAL);
    NetworkServerBlockEntity server = NetworkScene.placeServer(helper, owner, RobotTier.BASE, KEPT);

    NetworkScene.clickServer(helper, owner, new ItemStack(CubitItems.ADVANCED_MODULE.get()));

    helper.assertValueEqual(server.tier(), RobotTier.ADVANCED, WRONG_TIER);
    helper.assertValueEqual(server.capacity().amount(), ADVANCED_CAPACITY, WRONG_TIER);
    helper.assertValueEqual(server.energy(), KEPT, WRONG_SUM);
    helper.assertTrue(owner.getMainHandItem().isEmpty(), WRONG_HAND);
    helper.succeed();
  }

  /** The relay follows its server, so a Module does nothing to it (detail 4). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aModuleStaysInTheHandOnTheRelay(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    owner.setGameMode(GameType.SURVIVAL);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);

    NetworkScene.clickRelay(helper, owner, new ItemStack(CubitItems.ADVANCED_MODULE.get()));

    helper.assertValueEqual(relay.tier(), RobotTier.BASE, WRONG_TIER);
    helper.assertValueEqual(owner.getMainHandItem().getCount(), 1, WRONG_HAND);
    helper.succeed();
  }

  private static Player player(GameTestHelper helper) {
    return helper.makeMockPlayer(GameType.SURVIVAL);
  }

  private static void assertInput(GameTestHelper helper, IEnergyStorage storage, int limit) {
    helper.assertValueEqual(storage.receiveEnergy(PUSHED, true), limit, WRONG_INPUT);
    helper.assertValueEqual(storage.receiveEnergy(PUSHED, false), limit, WRONG_INPUT);
    helper.assertValueEqual(storage.receiveEnergy(PUSHED, false), 0, WRONG_INPUT);
    helper.assertValueEqual(storage.getEnergyStored(), limit, WRONG_SUM);
    helper.assertValueEqual(storage.extractEnergy(PUSHED, false), 0, GIVES);
  }

  private static void assertGenerates(GameTestHelper helper, RobotTier tier, long rate) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = NetworkScene.placeServer(helper, owner, tier, Energy.ZERO);
    server.setChunkLoading(false);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);

    helper.runAfterDelay(
        OPEN_TICKS,
        () -> {
          helper.assertValueEqual(relay.solar().panel(), PanelState.DEPLOYED, NOT_OPEN);
          helper.assertValueEqual(relay.solar().given().amount(), rate, NOT_GENERATED);
          helper.assertTrue(server.energy().amount() >= rate, NOT_GENERATED);
          helper.assertTrue(relay.energy().isZero(), WRONG_SUM);
          helper.succeed();
        });
  }

  private static void assertStored(
      GameTestHelper helper, SolarRelayBlockEntity relay, SunState reason) {
    helper.runAfterDelay(
        OPEN_TICKS,
        () -> {
          helper.assertValueEqual(relay.solar().panel(), PanelState.STORED, NOT_STORED);
          helper.assertValueEqual(relay.solar().state(), reason, NOT_STORED);
          helper.assertTrue(relay.solar().given().isZero(), GENERATED);
          helper.succeed();
        });
  }
}
