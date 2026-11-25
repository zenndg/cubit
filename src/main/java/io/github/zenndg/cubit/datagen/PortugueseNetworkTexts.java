package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.block.NetworkPlacement;
import io.github.zenndg.cubit.block.NetworkUpgrade;
import io.github.zenndg.cubit.registry.CubitBlocks;
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
  }
}
