package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.AntennaSignal;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import io.github.zenndg.cubit.domain.network.SignalDistance;
import io.github.zenndg.cubit.item.SavedLink;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * What a Signal Antenna knows of its Network Server: whether it reaches it, how long it has been
 * without it and what the distance costs. It has signal with its Server in the same dimension, in a
 * loaded chunk, with energy and with its Solar Relay (answer 2552). The Server is looked for once a
 * second, and only in a loaded chunk, never loading it (rules 21 and 22).
 */
final class AntennaUplink {
  private static final String NO_SIGNAL_KEY = "NoSignalTicks";
  private static final int LOOK_TICKS = 20;

  private AntennaSignal signal = AntennaSignal.LINKED;
  private NetworkServerBlockEntity server;
  private Energy distance = Energy.ZERO;
  private int untilLook;

  /** One tick of the signal. True when its state changed, for the clients and the save. */
  boolean tick(ServerLevel level, BlockPos pos, SavedLink link) {
    if (server != null && server.isRemoved()) {
      server = null;
    }
    if (untilLook <= 0) {
      untilLook = LOOK_TICKS;
      look(level, pos, link);
    }
    untilLook--;
    int loss = lossTicks();
    AntennaSignal.State before = signal.state(loss);
    signal = signal.next(reaches(), loss);
    return signal.state(loss) != before;
  }

  private void look(ServerLevel level, BlockPos pos, SavedLink link) {
    Optional<NetworkRoster.Server<GlobalPos>> listed =
        link.server().flatMap(NetworkData.of(level.getServer()).roster()::server);
    server = listed.map(found -> loaded(level, found)).orElse(null);
    distance =
        listed
            .map(found -> found.place().pos())
            .map(at ->
                distance()
                    .cost(SignalDistance.blocks(pos.getX(), pos.getZ(), at.getX(), at.getZ())))
            .orElse(Energy.ZERO);
  }

  private static NetworkServerBlockEntity loaded(
      ServerLevel level, NetworkRoster.Server<GlobalPos> listed) {
    BlockPos at = listed.place().pos();
    if (!listed.place().dimension().equals(level.dimension()) || !level.isLoaded(at)) {
      return null;
    }
    return level.getBlockEntity(at) instanceof NetworkServerBlockEntity found
            && sameId(found, listed.id())
        ? found
        : null;
  }

  private static boolean sameId(NetworkServerBlockEntity found, UUID id) {
    return found.identity().id().equals(id);
  }

  private boolean reaches() {
    return server != null
        && !server.isRemoved()
        && !server.energy().isZero()
        && server.relay().isPresent();
  }

  /** Whether its Server holds the chunk the antenna is in, so it does not pay again (2264). */
  boolean sharesChunk(BlockPos pos) {
    return server != null
        && !server.isRemoved()
        && server.chunkState().holds()
        && new ChunkPos(server.getBlockPos()).equals(new ChunkPos(pos));
  }

  AntennaSignal.State state() {
    return signal.state(lossTicks());
  }

  /** The whole minutes left without signal before it stops. */
  int minutesLeft() {
    return signal.minutesLeft(lossTicks());
  }

  /** What the distance to its Server costs per tick, while it works (answer 2310). */
  Energy distanceCost() {
    return distance;
  }

  void save(CompoundTag tag) {
    tag.putInt(NO_SIGNAL_KEY, signal.ticksWithout());
  }

  /** A broken count starts again with signal (rule 17). */
  void load(CompoundTag tag) {
    int saved = tag.getInt(NO_SIGNAL_KEY);
    signal = saved < 0 ? AntennaSignal.LINKED : new AntennaSignal(saved);
  }

  private static int lossTicks() {
    return CubitMod.CONFIG.network().antennas().spec().signalLossTicks();
  }

  private static SignalDistance distance() {
    return CubitMod.CONFIG.network().antennas().distance();
  }
}
