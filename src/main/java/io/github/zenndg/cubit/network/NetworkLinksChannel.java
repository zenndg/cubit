package io.github.zenndg.cubit.network;

import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * The arrows of the Network app, from the server to the client, in a class of their own so the
 * registry of the other packets stays within its size.
 */
public final class NetworkLinksChannel {
  private static Consumer<NetworkLinksPayload> display = links -> {};

  private NetworkLinksChannel() {}

  static void register(PayloadRegistrar registrar) {
    registrar.playToClient(
        NetworkLinksPayload.TYPE,
        NetworkLinksPayload.STREAM_CODEC,
        (payload, context) -> display.accept(payload));
  }

  /** Set by the client, which draws them, so this common class never loads one of the client. */
  public static void setDisplay(Consumer<NetworkLinksPayload> shown) {
    display = shown;
  }

  public static void send(ServerPlayer player, NetworkLinksPayload links) {
    PacketDistributor.sendToPlayer(player, links);
  }
}
