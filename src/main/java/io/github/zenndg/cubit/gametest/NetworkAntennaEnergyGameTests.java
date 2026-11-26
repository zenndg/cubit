package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.AntennaSignal;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The signal of the Signal Antenna and the energy its Network Server sends it through the Solar
 * Relay (Delivery 4, details 9 to 11 and 16, and answers 2550 to 2552).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkAntennaEnergyGameTests {
  /** More than one look of the antenna for its server, once a second. */
  private static final int SETTLE_TICKS = 45;

  private static final Energy FULL = new Energy(1_200_000);
  private static final Energy LOW = new Energy(100_000);
  private static final int HALF_PERCENT = 50;
  private static final UpsReserve HALF = UpsReserve.of(HALF_PERCENT);
  private static final String NO_SIGNAL = "The antenna should have signal";
  private static final String SIGNAL = "The antenna should be without signal";
  private static final String NOT_FED = "The server should feed the antenna";
  private static final String FED = "The server should keep its reserve";
  private static final String TOO_MUCH = "An antenna takes up to its input per tick";

  private NetworkAntennaEnergyGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void aServerWithItsRelayFeedsTheAntenna(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner, FULL);
    NetworkScene.placeRelay(helper, owner);
    SignalAntennaBlockEntity antenna = NetworkScene.placeAntenna(helper, owner);

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(antenna.signal(), AntennaSignal.State.LINKED, NO_SIGNAL);
          helper.assertFalse(antenna.energy().isZero(), NOT_FED);
          helper.assertTrue(server.antennasSent().amount() <= input().amount(), TOO_MUCH);
          helper.assertValueEqual(server.antennasReceiving(), 1, NOT_FED);
          helper.succeed();
        });
  }

  /** Without the relay of its pair there is no signal, and nothing is sent (answer 2552). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void withoutTheRelayThereIsNoSignal(GameTestHelper helper) {
    Player owner = player(helper);
    server(helper, owner, FULL);
    SignalAntennaBlockEntity antenna = NetworkScene.placeAntenna(helper, owner);

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(antenna.signal(), AntennaSignal.State.NO_SIGNAL, SIGNAL);
          helper.assertTrue(antenna.energy().isZero(), FED);
          helper.succeed();
        });
  }

  /** The reserve of the server stays for its own chunk (answer 2550). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theServerSendsNothingFromItsReserve(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner, LOW);
    server.setUps(HALF);
    NetworkScene.placeRelay(helper, owner);
    SignalAntennaBlockEntity antenna = NetworkScene.placeAntenna(helper, owner);

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          helper.assertValueEqual(antenna.signal(), AntennaSignal.State.LINKED, NO_SIGNAL);
          helper.assertTrue(antenna.energy().isZero(), FED);
          helper.assertTrue(server.antennasSent().isZero(), FED);
          helper.succeed();
        });
  }

  private static Energy input() {
    return CubitMod.CONFIG.network().antennas().spec().inputPerTick();
  }

  private static NetworkServerBlockEntity server(
      GameTestHelper helper, Player owner, Energy energy) {
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(helper, owner, RobotTier.BASE, energy);
    server.setChunkLoading(false);
    return server;
  }

  private static Player player(GameTestHelper helper) {
    return helper.makeMockPlayer(GameType.SURVIVAL);
  }
}
