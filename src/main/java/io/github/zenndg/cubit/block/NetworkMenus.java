package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.menu.NetworkServerMenu;
import io.github.zenndg.cubit.menu.SignalAntennaMenu;
import io.github.zenndg.cubit.menu.SolarRelayMenu;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;

/** Opens the screen of each block of the Network, for its owner. */
final class NetworkMenus {
  private NetworkMenus() {}

  static void open(NetworkServerBlockEntity server, Player player) {
    open(
        server,
        player,
        (id, inventory, opener) -> NetworkServerMenu.onServer(id, inventory, server),
        data -> NetworkServerMenu.writeOpening(data, server));
  }

  static void open(SolarRelayBlockEntity relay, Player player) {
    open(
        relay,
        player,
        (id, inventory, opener) -> SolarRelayMenu.onServer(id, inventory, relay),
        data -> SolarRelayMenu.writeOpening(data, relay));
  }

  static void open(SignalAntennaBlockEntity antenna, Player player) {
    open(
        antenna,
        player,
        (id, inventory, opener) -> SignalAntennaMenu.onServer(id, inventory, antenna),
        data -> SignalAntennaMenu.writeOpening(data, antenna));
  }

  private static void open(
      NetworkBlockEntity block,
      Player player,
      MenuConstructor menu,
      Consumer<RegistryFriendlyByteBuf> opening) {
    Component title = Component.literal(block.identity().name().value());
    player.openMenu(new SimpleMenuProvider(menu, title), opening);
  }
}
