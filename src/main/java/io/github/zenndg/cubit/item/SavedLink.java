package io.github.zenndg.cubit.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import org.slf4j.Logger;

/**
 * The Network Server a Signal Antenna is linked to, with the name it had at the click, and the last
 * channel of the antenna, kept beside the other fields of its item (answers 2566 and 2567). A
 * channel a save does not know is dropped, and the antenna takes the first free one (rule 17).
 */
public record SavedLink(
    Optional<UUID> server, Optional<String> serverName, Optional<AntennaChannel> channel) {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String SERVER_FIELD = "server";
  private static final String NAME_FIELD = "server_name";
  private static final String CHANNEL_FIELD = "channel";
  private static final String UNKNOWN_CHANNEL = "Unknown channel {} in an antenna. Dropping it.";

  public static final SavedLink NONE =
      new SavedLink(Optional.empty(), Optional.empty(), Optional.empty());

  private static final MapCodec<Optional<AntennaChannel>> CHANNEL =
      Codec.STRING
          .optionalFieldOf(CHANNEL_FIELD)
          .xmap(
              key -> key.flatMap(SavedLink::channelOf),
              channel -> channel.map(AntennaChannel::key));

  /** A map codec, so the fields sit beside the others of {@link SavedNetwork}. */
  static final MapCodec<SavedLink> MAP_CODEC =
      RecordCodecBuilder.mapCodec(instance ->
          instance
              .group(
                  UUIDUtil.CODEC.optionalFieldOf(SERVER_FIELD).forGetter(SavedLink::server),
                  Codec.STRING.optionalFieldOf(NAME_FIELD).forGetter(SavedLink::serverName),
                  CHANNEL.forGetter(SavedLink::channel))
              .apply(instance, SavedLink::new));

  /** A new link keeps the channel only for the same Server, whose channels it belongs to. */
  public SavedLink linkedTo(UUID next, String name) {
    Optional<AntennaChannel> kept = server.filter(next::equals).flatMap(same -> channel);
    return new SavedLink(Optional.of(next), Optional.of(name), kept);
  }

  public SavedLink withChannel(AntennaChannel taken) {
    return new SavedLink(server, serverName, Optional.of(taken));
  }

  private static Optional<AntennaChannel> channelOf(String key) {
    Optional<AntennaChannel> channel = AntennaChannel.of(key);
    if (channel.isEmpty()) {
      LOGGER.warn(UNKNOWN_CHANNEL, key);
    }
    return channel;
  }
}
