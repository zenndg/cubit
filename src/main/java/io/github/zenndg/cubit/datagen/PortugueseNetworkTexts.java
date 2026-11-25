package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.block.NetworkPlacement;
import io.github.zenndg.cubit.block.NetworkUpgrade;
import io.github.zenndg.cubit.registry.CubitBlocks;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.function.BiConsumer;

/** The Network, in Portuguese, with the same keys as the English one (answer 2275). */
final class PortugueseNetworkTexts {
  private static final String SERVER = "Servidor da Rede";
  private static final String RELAY = "Relé Solar";
  private static final String TAKEN = "Este chunk já tem um %s";
  private static final String NEEDS_SUPPORT = "O %s precisa de um bloco sólido embaixo";
  private static final String NEEDS_ROOM = "O %s precisa de dois blocos de altura";
  private static final String UPGRADED = "Servidor da Rede evoluiu para %s!";
  private static final String NEEDS_TIER = "O Servidor da Rede precisa ser %s antes";
  private static final String ALREADY_TIER = "O Servidor da Rede já é %s";
  private static final String RELAY_FOLLOWS = "O Relé Solar segue o Servidor da Rede";

  private static final String SERVER_STARTUP_SOUND = "Servidor da Rede liga";
  private static final String SERVER_FANS_SOUND = "Ventoinhas do Servidor da Rede zunem";
  private static final String SERVER_POWER_LOSS_SOUND = "Servidor da Rede fica sem energia";
  private static final String SERVER_UPGRADE_SOUND = "Servidor da Rede evolui";
  private static final String SERVER_UPS_WARNING_SOUND = "Servidor da Rede no Nobreak";
  private static final String RELAY_PANEL_OPEN_SOUND = "Relé Solar abre";
  private static final String RELAY_PANEL_CLOSE_SOUND = "Relé Solar fecha";
  private static final String RELAY_PANEL_LOCK_SOUND = "Relé Solar trava";
  private static final String RELAY_TRANSMIT_SOUND = "Relé Solar transmite";

  private PortugueseNetworkTexts() {}

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
