package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.LinkSpots;
import io.github.zenndg.cubit.block.ShowsName;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.network.LinkSpot;
import io.github.zenndg.cubit.domain.network.NetworkLinks;
import io.github.zenndg.cubit.domain.ownership.FloatingName;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.network.NetworkLinksPayload;
import io.github.zenndg.cubit.network.NetworkLinksPayload.Kind;
import io.github.zenndg.cubit.network.NetworkLinksPayload.Mark;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * The arrows of the Network app: over each antenna of the chosen Cubit, in the color of its
 * channel, over each of its Docks and chests, and, in gray, over the Docks that its antennas, or
 * the antenna clicked without a Cubit, only feed (answers 2592 and 2595). Only loaded blocks in the
 * dimension of the player count (rule 22).
 */
final class TabletNetworkView {
  private static final String NO_DETAIL = "";
  private static final int NO_NUMBER = 0;
  private static final int ONE_BLOCK = 1;

  private TabletNetworkView() {}

  static NetworkLinksPayload of(ServerPlayer player) {
    ServerLevel level = player.serverLevel();
    Optional<RobotEntity> chosen = TabletUse.chosen(player);
    NetworkLinks links =
        chosen.map(robot -> robot.docking().network().links()).orElse(NetworkLinks.NONE);
    String cubit = chosen.map(robot -> robot.getDisplayName().getString()).orElse(NO_DETAIL);
    List<Mark> marks = new ArrayList<>();
    Set<BlockPos> antennas = new LinkedHashSet<>();
    for (BlockPos pos : here(level, links.antennas())) {
      at(level, pos, SignalAntennaBlockEntity.class)
          .ifPresent(antenna -> {
            antennas.add(pos);
            marks.add(antennaMark(antenna));
          });
    }
    TabletNetworkFeed.pending(player)
        .filter(clicked -> clicked.dimension().equals(level.dimension()))
        .map(GlobalPos::pos)
        .ifPresent(antennas::add);
    List<BlockPos> docks = here(level, links.docks());
    for (BlockPos pos : docks) {
      at(level, pos, InductionDockBlockEntity.class)
          .ifPresent(dock -> marks.add(mark(dock, Kind.DOCK, cubit)));
    }
    marks.addAll(chests(level, links.chests(), cubit));
    marks.addAll(fedOnly(level, antennas, docks));
    return new NetworkLinksPayload(marks);
  }

  private static List<Mark> chests(ServerLevel level, List<LinkSpot> chests, String cubit) {
    List<Mark> marks = new ArrayList<>();
    for (BlockPos pos : here(level, chests)) {
      freeSlots(level, pos)
          .ifPresent(
              free -> marks.add(new Mark(pos, Kind.CHEST, cubit, NO_DETAIL, free, ONE_BLOCK)));
    }
    return marks;
  }

  /** The Docks those antennas feed that are not marked for the chosen Cubit. */
  private static List<Mark> fedOnly(
      ServerLevel level, Set<BlockPos> antennas, List<BlockPos> marked) {
    List<Mark> marks = new ArrayList<>();
    for (BlockPos pos : antennas) {
      List<BlockPos> fed =
          at(level, pos, SignalAntennaBlockEntity.class)
              .map(SignalAntennaBlockEntity::fedDocks)
              .orElse(List.of());
      for (BlockPos dock : fed) {
        if (!marked.contains(dock)) {
          at(level, dock, InductionDockBlockEntity.class)
              .ifPresent(found -> marks.add(mark(found, Kind.FED, NO_DETAIL)));
        }
      }
    }
    return marks;
  }

  private static Mark antennaMark(SignalAntennaBlockEntity antenna) {
    String channel = antenna.link().channel().map(found -> found.key()).orElse(NO_DETAIL);
    return mark(antenna, Kind.ANTENNA, channel);
  }

  private static <T extends BlockEntity & ShowsName> Mark mark(T block, Kind kind, String detail) {
    return new Mark(
        block.getBlockPos(), kind, block.shownName().value(), detail, NO_NUMBER, tall(block));
  }

  /** The height of the block in blocks, the one its floating name stands over. */
  private static <T extends BlockEntity & ShowsName> int tall(T block) {
    double top = block.nameSpot().y() - FloatingName.ABOVE_TOP;
    return (int) Math.round(top) - block.getBlockPos().getY();
  }

  private static List<BlockPos> here(ServerLevel level, List<LinkSpot> spots) {
    return spots.stream()
        .filter(spot -> LinkSpots.isIn(spot, level))
        .map(LinkSpots::pos)
        .filter(level::isLoaded)
        .toList();
  }

  private static <T extends BlockEntity> Optional<T> at(
      ServerLevel level, BlockPos pos, Class<T> kind) {
    return level.isLoaded(pos)
        ? Optional.ofNullable(level.getBlockEntity(pos)).filter(kind::isInstance).map(kind::cast)
        : Optional.empty();
  }

  private static Optional<Integer> freeSlots(ServerLevel level, BlockPos pos) {
    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
    if (handler == null) {
      return Optional.empty();
    }
    int free = 0;
    for (int slot = 0; slot < handler.getSlots(); slot++) {
      free += handler.getStackInSlot(slot).isEmpty() ? 1 : 0;
    }
    return Optional.of(free);
  }
}
