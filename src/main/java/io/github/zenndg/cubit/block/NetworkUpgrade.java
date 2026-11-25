package io.github.zenndg.cubit.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A Network Server goes up with a Module, as the Dock. The Solar Relay follows its server, so a
 * Module does nothing to it and stays in the hand (Delivery 3, detail 4).
 */
public final class NetworkUpgrade {
  public static final String UPGRADED_KEY = "notice.cubit.network_server_upgraded";
  public static final String NEEDS_TIER_KEY = "notice.cubit.network_server_needs_tier";
  public static final String ALREADY_TIER_KEY = "notice.cubit.network_server_already_tier";
  public static final String RELAY_FOLLOWS_KEY = "notice.cubit.solar_relay_follows";
  private static final ModuleUpgrade.Texts TEXTS =
      new ModuleUpgrade.Texts(UPGRADED_KEY, NEEDS_TIER_KEY, ALREADY_TIER_KEY);

  private NetworkUpgrade() {}

  static void apply(NetworkServerBlockEntity server, Player player, ItemStack stack) {
    ModuleUpgrade.apply(server.tier(), server::upgradeTo, TEXTS, player, stack);
  }

  static void tellRelay(Player player) {
    player.displayClientMessage(Component.translatable(RELAY_FOLLOWS_KEY), true);
  }
}
