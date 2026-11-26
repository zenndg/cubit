package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.block.AntennaLinking;
import io.github.zenndg.cubit.block.AntennaPlacing;
import io.github.zenndg.cubit.block.NetworkPlacement;
import io.github.zenndg.cubit.block.NetworkUpgrade;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.menu.ScreenKeys;
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
  private static final String NAME_DOCK = "Name your Dock";
  private static final String NAME_SERVER = "Name your Server";
  private static final String NAME_RELAY = "Name your Solar Relay";
  private static final String ANTENNA = "Signal Antenna";
  private static final String ANTENNA_NOT_LINKED = "Link it to a Server first";
  private static final String ANTENNA_NO_SERVER = "Its Network Server is gone";
  private static final String ANTENNA_OTHER_OWNER = "This Server belongs to another player";
  private static final String ANTENNA_AWAY = "Its Network Server is in another dimension";
  private static final String ANTENNA_CHUNK_TAKEN = "This chunk already has a Signal Antenna";
  private static final String ANTENNA_CHANNELS_FULL = "Its Network Server has no free channel";
  private static final String ANTENNA_MOVED = "Channel %s is taken, so it took %s";
  private static final String ANTENNA_LINKED = "Linked to %s";
  private static final String NAME_ANTENNA = "Name your Signal Antenna";

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
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.DOCK), NAME_DOCK);
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.SERVER), NAME_SERVER);
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.RELAY), NAME_RELAY);
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.ANTENNA), NAME_ANTENNA);
    addAntenna(add);
  }

  private static void addAntenna(BiConsumer<String, String> add) {
    add.accept(CubitBlocks.SIGNAL_ANTENNA.get().getDescriptionId(), ANTENNA);
    add.accept(AntennaPlacing.NOT_LINKED_KEY, ANTENNA_NOT_LINKED);
    add.accept(AntennaPlacing.NO_SERVER_KEY, ANTENNA_NO_SERVER);
    add.accept(AntennaPlacing.OTHER_OWNER_KEY, ANTENNA_OTHER_OWNER);
    add.accept(AntennaPlacing.OTHER_DIMENSION_KEY, ANTENNA_AWAY);
    add.accept(AntennaPlacing.CHUNK_TAKEN_KEY, ANTENNA_CHUNK_TAKEN);
    add.accept(AntennaPlacing.CHANNELS_FULL_KEY, ANTENNA_CHANNELS_FULL);
    add.accept(AntennaPlacing.CHANNEL_MOVED_KEY, ANTENNA_MOVED);
    add.accept(AntennaLinking.LINKED_KEY, ANTENNA_LINKED);
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
