package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.ChunkKeys;
import io.github.zenndg.cubit.domain.network.ChunkCall;
import io.github.zenndg.cubit.domain.network.ChunkRoster;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

/**
 * Brings the chunks of a network back when their owner comes back, the first at once and the others
 * one at a time, so no player feels a pause (answer 2413). A chunk whose block is gone is
 * forgotten.
 */
final class NetworkWake {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String GONE = "No network block of its owner at {}. Forgetting it.";

  private final NetworkData data;
  private final Set<GlobalPos> line = new LinkedHashSet<>();
  private final List<GlobalPos> unchecked = new ArrayList<>();
  private int rest;

  NetworkWake(NetworkData data) {
    this.data = data;
  }

  void ownerCame(UUID owner) {
    line.addAll(data.chunks().waitingOf(owner));
  }

  /** With the chunks kept while the owner is away, all of them come back with the world. */
  void worldOpened() {
    ChunkKeys keys = CubitMod.CONFIG.network().chunks();
    for (ChunkRoster.Entry<GlobalPos> entry : data.chunks().entries()) {
      if (!entry.waiting()) {
        unchecked.add(entry.place());
      } else if (!keys.releasesWithOwnerOffline()) {
        line.add(entry.place());
      }
    }
  }

  void tick(MinecraftServer server) {
    if (!unchecked.isEmpty()) {
      checkLoaded(server);
    }
    if (rest > 0) {
      rest--;
      return;
    }
    Iterator<GlobalPos> next = line.iterator();
    if (next.hasNext()) {
      GlobalPos place = next.next();
      next.remove();
      if (wake(server, place)) {
        rest = CubitMod.CONFIG.network().chunks().wakeIntervalTicks();
      }
    }
  }

  /** The chunks the world opened with, each one once it is loaded. */
  private void checkLoaded(MinecraftServer server) {
    unchecked.removeIf(place -> {
      ServerLevel level = server.getLevel(place.dimension());
      boolean loaded = level != null && level.isLoaded(place.pos());
      if (loaded || level == null) {
        check(server, place);
      }
      return loaded || level == null;
    });
  }

  /** True when a chunk was loaded. A place that does not fit the limits keeps waiting. */
  private boolean wake(MinecraftServer server, GlobalPos place) {
    Optional<UUID> owner = waitingOwner(place);
    if (owner.isEmpty() || !mayWake(server, place, owner.get())) {
      return false;
    }
    data.chunks().hold(place, owner.get());
    data.setDirty();
    if (!NetworkTickets.set(server, place, true)) {
      data.chunks().release(place);
      return false;
    }
    check(server, place);
    return true;
  }

  private Optional<UUID> waitingOwner(GlobalPos place) {
    return data.chunks().entries().stream()
        .filter(entry -> entry.waiting() && entry.place().equals(place))
        .map(ChunkRoster.Entry::owner)
        .findFirst();
  }

  private boolean mayWake(MinecraftServer server, GlobalPos place, UUID owner) {
    ChunkKeys keys = CubitMod.CONFIG.network().chunks();
    ChunkCall.Room room = data.chunks().room(place, owner, keys.limits());
    return keys.enabled()
        && (!keys.releasesWithOwnerOffline() || server.getPlayerList().getPlayer(owner) != null)
        && room.ofPlayer()
        && room.ofWorld();
  }

  /** The block itself decides from its next tick on, if it is still there. */
  private void check(MinecraftServer server, GlobalPos place) {
    ServerLevel level = server.getLevel(place.dimension());
    boolean there =
        level != null
            && level.getBlockEntity(place.pos()) instanceof NetworkBlockEntity block
            && data.chunks().entries().stream()
                .anyMatch(entry ->
                    entry.place().equals(place) && Ownership.owns(block.owner(), entry.owner()));
    if (!there) {
      LOGGER.warn(GONE, place);
      data.chunks().release(place);
      data.setDirty();
      NetworkTickets.set(server, place, false);
    }
  }
}
