package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.network.AntennaChannel;
import java.util.Optional;
import net.minecraft.util.FastColor;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * The parts of the antenna painted light for the game to tint: the band and the ring in the color
 * of the channel, and their threads (answer 2556). Without a channel they keep their light paint.
 */
final class ChannelBones {
  private static final String BAND = "channel_band";
  private static final String RING = "channel_ring";
  private static final String THREAD = "channel_thread";
  private static final String THREAD_RING = "channel_thread_ring";

  private ChannelBones() {}

  static int colour(GeoBone bone, Optional<AntennaChannel> channel, int colour) {
    if (channel.isEmpty()) {
      return colour;
    }
    String name = bone.getName();
    if (BAND.equals(name) || RING.equals(name)) {
      return FastColor.ARGB32.multiply(colour, ChannelTint.band(channel.get()));
    }
    if (THREAD.equals(name) || THREAD_RING.equals(name)) {
      return FastColor.ARGB32.multiply(colour, ChannelTint.thread(channel.get()));
    }
    return colour;
  }
}
