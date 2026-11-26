package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.LinkSpots;
import io.github.zenndg.cubit.block.NetworkData;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.network.LinkChunk;
import io.github.zenndg.cubit.domain.network.LinkSpot;
import io.github.zenndg.cubit.domain.network.NetworkLinks;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import io.github.zenndg.cubit.domain.robot.SecondClick;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotNetwork;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * The clicks of the Network app for the chosen Cubit: its antennas, with the second click in five
 * seconds to unlink one, and its Docks and chests (answers 2588, 2590 and 2600). A Dock marked for
 * it is also fed by the antenna of its chunk (answer 2589).
 */
final class TabletNetworkLinks {
  private static final Map<UUID, SecondClick<Asked>> ASKED = new ConcurrentHashMap<>();

  private TabletNetworkLinks() {}

  static void antenna(ServerPlayer player, RobotEntity robot, SignalAntennaBlockEntity antenna) {
    Optional<UUID> server = antenna.link().server();
    if (server.isEmpty()) {
      TabletNetworkApp.say(player, NetworkAppKeys.NOT_LINKED);
      return;
    }
    RobotNetwork network = robot.docking().network();
    LinkSpot spot = LinkSpots.of(player.level(), antenna.getBlockPos());
    NetworkLinks.Click click =
        network
            .links()
            .antenna(spot, server.get(), serverOf(player, network), network.limits().antennas());
    String name = antenna.shownName().value();
    switch (click.step()) {
      case LINKED -> {
        network.set(click.links());
        sayNamed(player, NetworkAppKeys.ANTENNA_LINKED, name, robot);
      }
      case CONFIRM -> unlink(player, robot, spot, name);
      case FULL -> say(player, NetworkAppKeys.ANTENNA_FULL, robot, network.limits().antennas());
      case OTHER_SERVER -> say(player, NetworkAppKeys.OTHER_SERVER, robot);
      case NOT_BESIDE -> say(player, NetworkAppKeys.NOT_BESIDE, robot);
      default -> TabletNetworkApp.say(player, NetworkAppKeys.SPLITS);
    }
  }

  /** The second click in time on the same antenna of the same Cubit unlinks it (answer 2600). */
  private static void unlink(ServerPlayer player, RobotEntity robot, LinkSpot spot, String name) {
    long now = player.serverLevel().getGameTime();
    Asked at = new Asked(robot.getUUID(), spot);
    SecondClick<Asked> asked = ASKED.remove(player.getUUID());
    boolean area = crossesArea(robot, spot.chunk());
    if (asked == null || !asked.confirmedBy(at, now)) {
      ASKED.put(player.getUUID(), new SecondClick<>(at, now));
      String key = area ? NetworkAppKeys.ANTENNA_CONFIRM_AREA : NetworkAppKeys.ANTENNA_CONFIRM;
      sayNamed(player, key, name, robot);
      return;
    }
    RobotNetwork network = robot.docking().network();
    network.set(network.links().without(spot));
    if (area) {
      robot.workArea().erase();
    }
    String key = area ? NetworkAppKeys.ANTENNA_UNLINKED_AREA : NetworkAppKeys.ANTENNA_UNLINKED;
    sayNamed(player, key, name, robot);
  }

  static void dock(ServerPlayer player, RobotEntity robot, InductionDockBlockEntity dock) {
    RobotNetwork network = robot.docking().network();
    LinkSpot spot = LinkSpots.of(player.level(), dock.getBlockPos());
    NetworkLinks.Click click = network.links().dock(spot, network.limits().docks());
    String name = dock.shownName().value();
    switch (click.step()) {
      case LINKED -> {
        network.set(click.links());
        feed(player, network.links(), dock.getBlockPos());
        sayNamed(player, NetworkAppKeys.DOCK_MARKED, name, robot);
      }
      case UNLINKED -> {
        network.set(click.links());
        sayNamed(player, NetworkAppKeys.DOCK_UNMARKED, name, robot);
      }
      case FULL -> say(player, NetworkAppKeys.DOCK_FULL, robot, network.limits().docks());
      default -> say(player, NetworkAppKeys.OUTSIDE, robot);
    }
  }

  static void chest(ServerPlayer player, RobotEntity robot, BlockPos pos) {
    RobotNetwork network = robot.docking().network();
    NetworkLinks.Click click =
        network.links().chest(LinkSpots.of(player.level(), pos), network.chestLimit());
    switch (click.step()) {
      case LINKED -> {
        network.set(click.links());
        say(player, NetworkAppKeys.CHEST_MARKED, robot);
      }
      case UNLINKED -> {
        network.set(click.links());
        say(player, NetworkAppKeys.CHEST_UNMARKED, robot);
      }
      case FULL -> say(player, NetworkAppKeys.CHEST_FULL, robot, network.chestLimit());
      default -> say(player, NetworkAppKeys.OUTSIDE, robot);
    }
  }

  /** The antenna of the chunk of the Dock feeds it, when it is there and loaded (2589). */
  private static void feed(ServerPlayer player, NetworkLinks links, BlockPos dock) {
    LinkChunk chunk = LinkSpots.of(player.level(), dock).chunk();
    links.antennas().stream()
        .filter(antenna -> antenna.chunk().equals(chunk))
        .map(LinkSpots::pos)
        .filter(player.level()::isLoaded)
        .map(player.level()::getBlockEntity)
        .filter(SignalAntennaBlockEntity.class::isInstance)
        .map(SignalAntennaBlockEntity.class::cast)
        .forEach(antenna -> antenna.feed(dock));
  }

  /** The Server of the antennas already linked, while one of them is still known (answer 2258). */
  private static Optional<UUID> serverOf(ServerPlayer player, RobotNetwork network) {
    NetworkRoster<GlobalPos> roster = NetworkData.of(player.server).roster();
    return network.links().antennas().stream()
        .flatMap(spot -> LinkSpots.global(spot).stream())
        .flatMap(place -> roster.antenna(place).stream())
        .map(NetworkRoster.Antenna::server)
        .findFirst();
  }

  private static boolean crossesArea(RobotEntity robot, LinkChunk chunk) {
    return robot
        .workArea()
        .area()
        .filter(area -> area.isIn(chunk.dimension()))
        .filter(area ->
            area.crosses(chunk.x() * LinkChunk.SIDE, chunk.z() * LinkChunk.SIDE, LinkChunk.SIDE))
        .isPresent();
  }

  private static void say(ServerPlayer player, String key, RobotEntity robot, Object... more) {
    Object[] values = new Object[more.length + 1];
    values[0] = robot.getDisplayName();
    System.arraycopy(more, 0, values, 1, more.length);
    player.displayClientMessage(Component.translatable(key, values), true);
  }

  private static void sayNamed(ServerPlayer player, String key, String name, RobotEntity robot) {
    TabletNetworkApp.say(player, key, name, robot.getDisplayName());
  }

  static void forget(ServerPlayer player) {
    ASKED.remove(player.getUUID());
  }

  /** The antenna of a Cubit that waits for the second click. */
  private record Asked(UUID robot, LinkSpot antenna) {}
}
