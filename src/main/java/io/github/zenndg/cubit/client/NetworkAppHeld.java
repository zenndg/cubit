package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Whether the player holds the Tablet in the Network app, which shows the arrows of the links, the
 * floating names and the walls of the chunks (answers 2425, 2542 and 2592).
 */
final class NetworkAppHeld {
  private NetworkAppHeld() {}

  static boolean by(Player player) {
    return inNetworkApp(player.getMainHandItem()) || inNetworkApp(player.getOffhandItem());
  }

  /** Whether this stack is a Tablet in the Network app. */
  static boolean inNetworkApp(ItemStack stack) {
    return stack.is(CubitItems.TABLET.get()) && TabletItem.appOf(stack) == TabletApp.NETWORK;
  }
}
