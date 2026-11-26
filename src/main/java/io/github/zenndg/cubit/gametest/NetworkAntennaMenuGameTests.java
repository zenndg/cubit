package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkData;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.menu.SignalAntennaMenu;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The screen of the Signal Antenna: who may use it, its switches, its reserve and the arrows of the
 * channel (answers 2213, 2309 and 2548).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkAntennaMenuGameTests {
  private static final int MENU_ID = 1;
  private static final Energy KEPT = new Energy(400_000);
  private static final int LESS_PERCENT = 45;
  private static final UpsReserve LESS = UpsReserve.of(LESS_PERCENT);
  private static final String WRONG_MENU = "Only the owner keeps the screen open";
  private static final String PRESSED = "The button should do nothing";
  private static final String WRONG_CHOICE = "The button should change the antenna";
  private static final String WRONG_CHANNEL = "The arrow should move to the next free channel";

  private NetworkAntennaMenuGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void onlyTheOwnerUsesTheScreen(GameTestHelper helper) {
    ServerPlayer owner = player(helper);
    ServerPlayer stranger = player(helper);
    SignalAntennaMenu menu = menu(owner, antenna(helper, owner));

    helper.assertTrue(menu.stillValid(owner), WRONG_MENU);
    helper.assertFalse(menu.stillValid(stranger), WRONG_MENU);
    helper.assertFalse(menu.clickMenuButton(stranger, SignalAntennaMenu.ENABLE_BUTTON), PRESSED);
    helper.succeed();
  }

  /** Switched off, its reserve does not change (answer 2548). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theSwitchesAndTheReserveFollowTheButtons(GameTestHelper helper) {
    ServerPlayer owner = player(helper);
    SignalAntennaBlockEntity antenna = antenna(helper, owner);
    UpsReserve standard = antenna.ups();
    SignalAntennaMenu menu = menu(owner, antenna);

    menu.clickMenuButton(owner, SignalAntennaMenu.ENABLE_BUTTON);
    helper.assertFalse(antenna.enabled(), WRONG_CHOICE);
    helper.assertFalse(menu.clickMenuButton(owner, SignalAntennaMenu.UPS_LESS_BUTTON), PRESSED);
    helper.assertValueEqual(antenna.ups(), standard, PRESSED);
    menu.clickMenuButton(owner, SignalAntennaMenu.ENABLE_BUTTON);
    helper.assertTrue(menu.clickMenuButton(owner, SignalAntennaMenu.UPS_LESS_BUTTON), WRONG_CHOICE);
    helper.assertValueEqual(antenna.ups(), LESS, WRONG_CHOICE);
    menu.clickMenuButton(owner, SignalAntennaMenu.CHUNK_BUTTON);
    menu.clickMenuButton(owner, SignalAntennaMenu.WALL_BUTTON);
    helper.assertFalse(antenna.chunkLoading(), WRONG_CHOICE);
    helper.assertTrue(antenna.showsWall(), WRONG_CHOICE);
    helper.succeed();
  }

  /** The arrows skip the taken channels, and the Server knows the new one at once (2309). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theArrowsMoveTheChannel(GameTestHelper helper) {
    ServerPlayer owner = player(helper);
    SignalAntennaBlockEntity antenna = antenna(helper, owner);
    SignalAntennaMenu menu = menu(owner, antenna);

    helper.assertTrue(
        menu.clickMenuButton(owner, SignalAntennaMenu.CHANNEL_FORWARD_BUTTON), WRONG_CHANNEL);

    helper.assertValueEqual(
        antenna.link().channel(), Optional.of(AntennaChannel.BLUE), WRONG_CHANNEL);
    helper.assertValueEqual(
        NetworkData.of(helper.getLevel().getServer())
            .roster()
            .taken(antenna.link().server().orElseThrow()),
        Set.of(AntennaChannel.BLUE),
        WRONG_CHANNEL);
    helper.succeed();
  }

  private static ServerPlayer player(GameTestHelper helper) {
    ServerPlayer player = GameTestRobots.onlinePlayer(helper);
    BlockPos beside = helper.absolutePos(NetworkScene.otherFloor(helper).north(2).above());
    player.moveTo(beside, 0, 0);
    return player;
  }

  private static SignalAntennaBlockEntity antenna(GameTestHelper helper, ServerPlayer owner) {
    NetworkServerBlockEntity server = NetworkScene.placeServer(helper, owner, RobotTier.BASE, KEPT);
    server.setChunkLoading(false);
    SignalAntennaBlockEntity antenna = NetworkScene.placeAntenna(helper, owner);
    antenna.setChunkLoading(true);
    return antenna;
  }

  private static SignalAntennaMenu menu(ServerPlayer owner, SignalAntennaBlockEntity antenna) {
    return SignalAntennaMenu.onServer(MENU_ID, owner.getInventory(), antenna);
  }
}
