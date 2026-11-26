package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.LinkSpots;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.network.LinkSpot;
import io.github.zenndg.cubit.domain.robot.SecondClick;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.registry.CubitEntities;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

/**
 * Without a chosen Cubit, a click on an antenna and then on a Dock of its chunk feeds the Dock, and
 * the same on a fed Dock cuts its energy at the second click (answers 2588, 2589 and 2604). What
 * waits for the next click lives only in the memory of the server.
 */
final class TabletNetworkFeed {
  private static final Map<UUID, GlobalPos> ANTENNA = new ConcurrentHashMap<>();
  private static final Map<UUID, SecondClick<GlobalPos>> ASKED = new ConcurrentHashMap<>();

  private TabletNetworkFeed() {}

  static void antenna(ServerPlayer player, SignalAntennaBlockEntity antenna) {
    ASKED.remove(player.getUUID());
    ANTENNA.put(player.getUUID(), GlobalPos.of(player.level().dimension(), antenna.getBlockPos()));
    TabletNetworkApp.say(player, NetworkAppKeys.FEED_NEXT, antenna.shownName().value());
  }

  /** The antenna clicked without a Cubit, whose Docks the arrows show (answer 2592). */
  static Optional<GlobalPos> pending(ServerPlayer player) {
    return Optional.ofNullable(ANTENNA.get(player.getUUID()));
  }

  static boolean waits(ServerPlayer player) {
    return ANTENNA.containsKey(player.getUUID());
  }

  static void dock(ServerPlayer player, InductionDockBlockEntity dock) {
    Optional<SignalAntennaBlockEntity> antenna = clicked(player);
    if (antenna.isEmpty()) {
      forget(player);
      TabletNetworkApp.say(player, NetworkAppKeys.PICK);
      return;
    }
    String dockName = dock.shownName().value();
    String antennaName = antenna.get().shownName().value();
    BlockPos pos = dock.getBlockPos();
    if (!new ChunkPos(pos).equals(new ChunkPos(antenna.get().getBlockPos()))) {
      TabletNetworkApp.say(player, NetworkAppKeys.NOT_IN_CHUNK, dockName, antennaName);
    } else if (!antenna.get().feeds(pos)) {
      antenna.get().feed(pos);
      TabletNetworkApp.say(player, NetworkAppKeys.FED, antennaName, dockName);
    } else {
      cut(player, antenna.get(), dock);
    }
  }

  /** Always asks first, saying how many loaded Cubits use the Dock (answer 2604). */
  private static void cut(
      ServerPlayer player, SignalAntennaBlockEntity antenna, InductionDockBlockEntity dock) {
    GlobalPos at = GlobalPos.of(player.level().dimension(), dock.getBlockPos());
    long now = player.serverLevel().getGameTime();
    SecondClick<GlobalPos> asked = ASKED.remove(player.getUUID());
    if (asked != null && asked.confirmedBy(at, now)) {
      antenna.unfeed(dock.getBlockPos());
      say(player, NetworkAppKeys.UNFED, antenna, dock);
      return;
    }
    ASKED.put(player.getUUID(), new SecondClick<>(at, now));
    TabletNetworkApp.say(
        player, NetworkAppKeys.FEED_CONFIRM, dock.shownName().value(), users(player, dock));
  }

  /** The loaded Cubits of the player with this Dock marked. */
  private static long users(ServerPlayer player, InductionDockBlockEntity dock) {
    LinkSpot spot = LinkSpots.of(player.level(), dock.getBlockPos());
    long count = 0;
    for (ServerLevel each : player.server.getAllLevels()) {
      count +=
          each.getEntities(CubitEntities.GENERAL_ROBOT.get(), robot -> uses(player, robot, spot))
              .size();
    }
    return count;
  }

  private static boolean uses(ServerPlayer player, RobotEntity robot, LinkSpot spot) {
    return robot.ownerId().filter(player.getUUID()::equals).isPresent()
        && robot.docking().network().links().docks().contains(spot);
  }

  /** The antenna clicked before, while it is still there and loaded. */
  private static Optional<SignalAntennaBlockEntity> clicked(ServerPlayer player) {
    GlobalPos at = ANTENNA.get(player.getUUID());
    ServerLevel level = at == null ? null : player.server.getLevel(at.dimension());
    if (level == null || !level.isLoaded(at.pos())) {
      return Optional.empty();
    }
    return level.getBlockEntity(at.pos()) instanceof SignalAntennaBlockEntity antenna
        ? Optional.of(antenna)
        : Optional.empty();
  }

  private static void say(
      ServerPlayer player,
      String key,
      SignalAntennaBlockEntity antenna,
      InductionDockBlockEntity dock) {
    TabletNetworkApp.say(player, key, antenna.shownName().value(), dock.shownName().value());
  }

  static void forget(ServerPlayer player) {
    ANTENNA.remove(player.getUUID());
    ASKED.remove(player.getUUID());
  }
}
