package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.AntennaPlacement;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.item.SavedLink;
import io.github.zenndg.cubit.item.SavedNetwork;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

/**
 * Where a Signal Antenna may go and which channel it takes: the rules live in {@link
 * AntennaPlacement} and {@link NetworkRoster}, and this class reads the world for them (Delivery 4,
 * details 6 and 14, and answer 2569). Only the server decides, so the client never refuses.
 */
public final class AntennaPlacing {
  public static final String NOT_LINKED_KEY = "notice.cubit.antenna_not_linked";
  public static final String NO_SERVER_KEY = "notice.cubit.antenna_no_server";
  public static final String OTHER_OWNER_KEY = "notice.cubit.antenna_other_owner";
  public static final String OTHER_DIMENSION_KEY = "notice.cubit.antenna_other_dimension";
  public static final String CHUNK_TAKEN_KEY = "notice.cubit.antenna_chunk_taken";
  public static final String CHANNELS_FULL_KEY = "notice.cubit.antenna_channels_full";
  public static final String CHANNEL_MOVED_KEY = "notice.cubit.antenna_channel_moved";
  private static final String COLOR_PREFIX = "color.minecraft.";
  private static final Map<AntennaPlacement, String> KEYS = new EnumMap<>(AntennaPlacement.class);

  static {
    KEYS.put(AntennaPlacement.NOT_LINKED, NOT_LINKED_KEY);
    KEYS.put(AntennaPlacement.NO_SERVER, NO_SERVER_KEY);
    KEYS.put(AntennaPlacement.OTHER_OWNER, OTHER_OWNER_KEY);
    KEYS.put(AntennaPlacement.OTHER_DIMENSION, OTHER_DIMENSION_KEY);
    KEYS.put(AntennaPlacement.CHUNK_TAKEN, CHUNK_TAKEN_KEY);
    KEYS.put(AntennaPlacement.CHANNELS_FULL, CHANNELS_FULL_KEY);
  }

  private AntennaPlacing() {}

  static Optional<Component> refusal(Block block, BlockPlaceContext context) {
    if (!(context.getLevel() instanceof ServerLevel level)) {
      return Optional.empty();
    }
    AntennaPlacement result = AntennaPlacement.decide(check(block, context, level));
    return result.allows()
        ? Optional.empty()
        : Optional.of(Component.translatable(KEYS.get(result), block.getName()));
  }

  private static AntennaPlacement.Check check(
      Block block, BlockPlaceContext context, ServerLevel level) {
    SavedLink link = SavedNetwork.of(context.getItemInHand()).link();
    NetworkRoster<GlobalPos> roster = roster(level);
    Optional<NetworkRoster.Server<GlobalPos>> server = link.server().flatMap(roster::server);
    Optional<UUID> placer = Optional.ofNullable(context.getPlayer()).map(Player::getUUID);
    return new AntennaPlacement.Check(
        link.server().isPresent(),
        server.isPresent(),
        server.filter(found -> Ownership.sameOwner(Optional.of(found.owner()), placer)).isPresent(),
        server.filter(found -> found.place().dimension().equals(level.dimension())).isPresent(),
        NetworkChunk.has(level, context.getClickedPos(), block),
        server.flatMap(found -> channelFor(roster, found, link)).isPresent());
  }

  /**
   * The placed antenna takes its last channel while free, or the first free one with a notice, and
   * is listed with its Server (answer 2547).
   */
  static void placed(SignalAntennaBlockEntity antenna, Player player) {
    if (!(antenna.getLevel() instanceof ServerLevel level)) {
      return;
    }
    SavedLink link = antenna.link();
    NetworkRoster<GlobalPos> roster = roster(level);
    Optional<AntennaChannel> taken =
        link.server().flatMap(roster::server).flatMap(found -> channelFor(roster, found, link));
    if (taken.isEmpty()) {
      return;
    }
    if (link.channel().isPresent() && !link.channel().equals(taken)) {
      player.displayClientMessage(
          Component.translatable(
              CHANNEL_MOVED_KEY, colorOf(link.channel().get()), colorOf(taken.get())),
          true);
    }
    antenna.take(taken.get());
    listed(antenna);
  }

  /** Listed again when it loads, in case the data of the world lost it. */
  static void listed(SignalAntennaBlockEntity antenna) {
    if (!(antenna.getLevel() instanceof ServerLevel level)) {
      return;
    }
    SavedLink link = antenna.link();
    if (antenna.owner().isEmpty() || link.server().isEmpty() || link.channel().isEmpty()) {
      return;
    }
    NetworkData data = NetworkData.of(level.getServer());
    NetworkRoster.Antenna<GlobalPos> entry =
        new NetworkRoster.Antenna<>(
            placeOf(antenna, level),
            antenna.owner().get(),
            link.server().get(),
            link.channel().get());
    if (data.roster().putAntenna(entry)) {
      data.setDirty();
    }
  }

  /** The block is gone, so its channel is free again. */
  static void unlisted(SignalAntennaBlockEntity antenna) {
    if (antenna.getLevel() instanceof ServerLevel level) {
      NetworkData data = NetworkData.of(level.getServer());
      if (data.roster().removeAntenna(placeOf(antenna, level))) {
        data.setDirty();
      }
    }
  }

  /**
   * The arrows of the screen: the next free open channel to one side, which its Server knows at
   * once (answer 2309). False when nothing changed.
   */
  static boolean step(SignalAntennaBlockEntity antenna, int direction) {
    if (!(antenna.getLevel() instanceof ServerLevel level)) {
      return false;
    }
    SavedLink link = antenna.link();
    NetworkRoster<GlobalPos> roster = roster(level);
    Optional<NetworkRoster.Server<GlobalPos>> server = link.server().flatMap(roster::server);
    if (server.isEmpty() || link.channel().isEmpty()) {
      return false;
    }
    int open = CubitMod.CONFIG.network().server(server.get().tier()).channels();
    AntennaChannel now = link.channel().get();
    AntennaChannel next = now.step(direction, open, roster.taken(server.get().id()));
    if (next == now) {
      return false;
    }
    antenna.take(next);
    listed(antenna);
    return true;
  }

  /** The Server of the antenna as the data of the world knows it, also far away. */
  public static Optional<NetworkRoster.Server<GlobalPos>> serverOf(
      SignalAntennaBlockEntity antenna) {
    return antenna.getLevel() instanceof ServerLevel level
        ? antenna.link().server().flatMap(roster(level)::server)
        : Optional.empty();
  }

  /** The tier of the Server, known also with its chunk unloaded, opens its channels. */
  private static Optional<AntennaChannel> channelFor(
      NetworkRoster<GlobalPos> roster, NetworkRoster.Server<GlobalPos> server, SavedLink link) {
    int open = CubitMod.CONFIG.network().server(server.tier()).channels();
    return roster.channelFor(server.id(), link.channel(), open);
  }

  /** The name of a channel, from the colors of the game, in the language of the player. */
  public static Component colorOf(AntennaChannel channel) {
    return Component.translatable(COLOR_PREFIX + channel.key());
  }

  private static GlobalPos placeOf(SignalAntennaBlockEntity antenna, ServerLevel level) {
    return GlobalPos.of(level.dimension(), antenna.getBlockPos());
  }

  private static NetworkRoster<GlobalPos> roster(ServerLevel level) {
    return NetworkData.of(level.getServer()).roster();
  }
}
