package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** When the Network brings chunks back: the world opens, or their owner comes (2210, 2407). */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class NetworkEvents {
  private NetworkEvents() {}

  @SubscribeEvent
  public static void onServerStarted(ServerStartedEvent event) {
    NetworkData.of(event.getServer()).wake().worldOpened();
  }

  @SubscribeEvent
  public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity().getServer() != null) {
      NetworkData.of(event.getEntity().getServer()).wake().ownerCame(event.getEntity().getUUID());
    }
  }

  @SubscribeEvent
  public static void onServerTick(ServerTickEvent.Post event) {
    NetworkData.of(event.getServer()).wake().tick(event.getServer());
  }
}
