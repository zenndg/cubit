package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.block.NetworkPlacement;
import io.github.zenndg.cubit.block.NetworkUpgrade;
import io.github.zenndg.cubit.registry.CubitBlocks;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.function.BiConsumer;

/** The Network, in English, the base language (answer 2275). */
final class NetworkTexts {
  private static final String SERVER = "Network Server";
  private static final String RELAY = "Solar Relay";
  private static final String TAKEN = "This chunk already has a %s";
  private static final String NEEDS_SUPPORT = "The %s needs a solid block below";
  private static final String NEEDS_ROOM = "The %s needs two blocks of height";
  private static final String UPGRADED = "Network Server upgraded to %s!";
  private static final String NEEDS_TIER = "The Network Server needs to be %s first";
  private static final String ALREADY_TIER = "The Network Server is already %s";
  private static final String RELAY_FOLLOWS = "The Solar Relay follows its Network Server";

  private static final String SERVER_STARTUP_SOUND = "Network Server starts up";
  private static final String SERVER_FANS_SOUND = "Network Server fans hum";
  private static final String SERVER_POWER_LOSS_SOUND = "Network Server loses power";
  private static final String SERVER_UPGRADE_SOUND = "Network Server upgrades";
  private static final String SERVER_UPS_WARNING_SOUND = "Network Server runs on UPS";
  private static final String RELAY_PANEL_OPEN_SOUND = "Solar Relay opens";
  private static final String RELAY_PANEL_CLOSE_SOUND = "Solar Relay closes";
  private static final String RELAY_PANEL_LOCK_SOUND = "Solar Relay locks";
  private static final String RELAY_TRANSMIT_SOUND = "Solar Relay transmits";

  private NetworkTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(CubitBlocks.NETWORK_SERVER.get().getDescriptionId(), SERVER);
    add.accept(CubitBlocks.SOLAR_RELAY.get().getDescriptionId(), RELAY);
    add.accept(NetworkPlacement.TAKEN_KEY, TAKEN);
    add.accept(NetworkPlacement.NEEDS_SUPPORT_KEY, NEEDS_SUPPORT);
    add.accept(NetworkPlacement.NEEDS_ROOM_KEY, NEEDS_ROOM);
    add.accept(NetworkUpgrade.UPGRADED_KEY, UPGRADED);
    add.accept(NetworkUpgrade.NEEDS_TIER_KEY, NEEDS_TIER);
    add.accept(NetworkUpgrade.ALREADY_TIER_KEY, ALREADY_TIER);
    add.accept(NetworkUpgrade.RELAY_FOLLOWS_KEY, RELAY_FOLLOWS);
    addSounds(add);
  }

  private static void addSounds(BiConsumer<String, String> add) {
    add.accept(
        CubitSoundProvider.serverSubtitleKey(CubitSounds.SERVER_STARTUP_NAME),
        SERVER_STARTUP_SOUND);
    add.accept(
        CubitSoundProvider.serverSubtitleKey(CubitSounds.SERVER_FANS_NAME), SERVER_FANS_SOUND);
    add.accept(
        CubitSoundProvider.serverSubtitleKey(CubitSounds.SERVER_POWER_LOSS_NAME),
        SERVER_POWER_LOSS_SOUND);
    add.accept(
        CubitSoundProvider.serverSubtitleKey(CubitSounds.SERVER_UPGRADE_NAME),
        SERVER_UPGRADE_SOUND);
    add.accept(
        CubitSoundProvider.serverSubtitleKey(CubitSounds.SERVER_UPS_WARNING_NAME),
        SERVER_UPS_WARNING_SOUND);
    add.accept(
        CubitSoundProvider.relaySubtitleKey(CubitSounds.RELAY_PANEL_OPEN_NAME),
        RELAY_PANEL_OPEN_SOUND);
    add.accept(
        CubitSoundProvider.relaySubtitleKey(CubitSounds.RELAY_PANEL_CLOSE_NAME),
        RELAY_PANEL_CLOSE_SOUND);
    add.accept(
        CubitSoundProvider.relaySubtitleKey(CubitSounds.RELAY_PANEL_LOCK_NAME),
        RELAY_PANEL_LOCK_SOUND);
    add.accept(
        CubitSoundProvider.relaySubtitleKey(CubitSounds.RELAY_TRANSMIT_NAME), RELAY_TRANSMIT_SOUND);
  }
}
