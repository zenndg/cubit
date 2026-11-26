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
  private static final String ANTENNA_RAISE_SOUND = "Antena de Sinal se ergue";
  private static final String ANTENNA_LOCK_SOUND = "Antena de Sinal trava o sinal";
  private static final String ANTENNA_LOST_SOUND = "Antena de Sinal perde o sinal";
  private static final String ANTENNA_REGAINED_SOUND = "Antena de Sinal recupera o sinal";
  private static final String ANTENNA_LINK_SOUND = "Antena de Sinal se liga";
  private static final String NAME_DOCK = "Dê um nome ao seu Dock";
  private static final String NAME_SERVER = "Dê um nome ao seu Servidor";
  private static final String NAME_RELAY = "Dê um nome ao seu Relé Solar";
  private static final String ANTENNA = "Antena de Sinal";
  private static final String ANTENNA_NOT_LINKED = "Ligue-a a um Servidor primeiro";
  private static final String ANTENNA_NO_SERVER = "O Servidor da Rede dela não existe mais";
  private static final String ANTENNA_OTHER_OWNER = "Este Servidor é de outro player";
  private static final String ANTENNA_AWAY = "O Servidor da Rede dela está em outra dimensão";
  private static final String ANTENNA_CHUNK_TAKEN = "Este chunk já tem uma Antena de Sinal";
  private static final String ANTENNA_CHANNELS_FULL = "O Servidor da Rede dela não tem canal livre";
  private static final String ANTENNA_MOVED = "O canal %s está ocupado, e ela ficou com o %s";
  private static final String ANTENNA_LINKED = "Ligada a %s";
  private static final String NAME_ANTENNA = "Dê um nome à sua Antena de Sinal";

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
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.DOCK), NAME_DOCK);
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.SERVER), NAME_SERVER);
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.RELAY), NAME_RELAY);
    add.accept(ScreenKeys.blockNameTitle(BlockName.Kind.ANTENNA), NAME_ANTENNA);
    addAntenna(add);
    addAntennaSounds(add);
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

  private static void addAntennaSounds(BiConsumer<String, String> add) {
    add.accept(
        CubitSoundProvider.antennaSubtitleKey(CubitSounds.ANTENNA_RAISE_NAME), ANTENNA_RAISE_SOUND);
    add.accept(
        CubitSoundProvider.antennaSubtitleKey(CubitSounds.ANTENNA_LOCK_NAME), ANTENNA_LOCK_SOUND);
    add.accept(
        CubitSoundProvider.antennaSubtitleKey(CubitSounds.ANTENNA_LOST_NAME), ANTENNA_LOST_SOUND);
    add.accept(
        CubitSoundProvider.antennaSubtitleKey(CubitSounds.ANTENNA_REGAINED_NAME),
        ANTENNA_REGAINED_SOUND);
    add.accept(
        CubitSoundProvider.antennaSubtitleKey(CubitSounds.ANTENNA_LINK_NAME), ANTENNA_LINK_SOUND);
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
