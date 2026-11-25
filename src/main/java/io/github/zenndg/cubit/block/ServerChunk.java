package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.ChunkKeys;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.network.ChunkCall;
import io.github.zenndg.cubit.domain.network.ChunkCharge;
import io.github.zenndg.cubit.domain.network.ChunkRoster;
import io.github.zenndg.cubit.domain.network.ChunkState;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

/**
 * The chunk of a Network Server: whether it holds it, what it pays each tick and why it lets it go
 * (answers 2209 to 2216 and 2372). The energy is looked at every tick, and the rest once a second
 * or as soon as it changes (rule 21).
 */
final class ServerChunk {
  private static final String DRAINED_KEY = "Drained";
  private static final int CHECK_TICKS = 20;

  private final Runnable changed;
  private ChunkCharge charge = ChunkCharge.CHARGED;
  private ChunkState state = ChunkState.SWITCHED_OFF;
  private int untilCheck;

  ServerChunk(Runnable changed) {
    this.changed = changed;
  }

  ChunkState state() {
    return state;
  }

  /** What it costs now, for the screen. */
  Energy cost() {
    return state.holds() ? keys().price().perTick() : Energy.ZERO;
  }

  /** Something the owner chose changed, so the next tick looks at everything. */
  void recheck() {
    untilCheck = 0;
  }

  /** The stock, with what the chunk cost in this tick. */
  EnergyStock tick(ServerLevel level, BlockPos pos, Asked asked, EnergyStock stock) {
    ChunkCall.Price price = keys().price();
    ChunkCharge next = charge.next(stock.energy().charge(), price);
    if (!next.equals(charge)) {
      charge = next;
      untilCheck = 0;
      changed.run();
    }
    if (untilCheck <= 0) {
      untilCheck = CHECK_TICKS;
      check(level, GlobalPos.of(level.dimension(), pos), asked);
    }
    untilCheck--;
    if (!state.holds()) {
      return stock;
    }
    EnergyStock.Spent spent = stock.spend(price.perTick());
    if (!spent.paid().isZero()) {
      changed.run();
    }
    return spent.stock();
  }

  private void check(ServerLevel level, GlobalPos place, Asked asked) {
    NetworkData data = NetworkData.of(level.getServer());
    state =
        asked
            .owner()
            .map(owner -> decide(level, place, owner, asked.on()))
            .orElse(ChunkState.OWNER_OFFLINE);
    apply(level, data, place, asked.owner());
  }

  private ChunkState decide(ServerLevel level, GlobalPos place, UUID owner, boolean on) {
    ChunkKeys keys = keys();
    boolean here =
        !keys.releasesWithOwnerOffline()
            || level.getServer().getPlayerList().getPlayer(owner) != null;
    ChunkCall.Room room =
        NetworkData.of(level.getServer()).chunks().room(place, owner, keys.limits());
    return new ChunkCall(keys.enabled(), on, here, room).decide(charge);
  }

  /** The data and the ticket follow the state; both do nothing when nothing changes. */
  private void apply(ServerLevel level, NetworkData data, GlobalPos place, Optional<UUID> owner) {
    ChunkRoster<GlobalPos> chunks = data.chunks();
    boolean listed =
        switch (state) {
          case HOLDING -> chunks.hold(place, owner.orElseThrow());
          case OWNER_OFFLINE -> chunks.await(place);
          default -> chunks.release(place);
        };
    if (listed) {
      data.setDirty();
    }
    NetworkTickets.set(level.getServer(), place, state.holds());
  }

  /** The server is gone, so nothing of its chunk is left. */
  void release(ServerLevel level, BlockPos pos) {
    state = ChunkState.SWITCHED_OFF;
    apply(
        level,
        NetworkData.of(level.getServer()),
        GlobalPos.of(level.dimension(), pos),
        Optional.empty());
  }

  void save(CompoundTag tag) {
    tag.putBoolean(DRAINED_KEY, charge.drained());
  }

  void load(CompoundTag tag) {
    charge = new ChunkCharge(tag.getBoolean(DRAINED_KEY));
  }

  private static ChunkKeys keys() {
    return CubitMod.CONFIG.network().chunks();
  }

  /** Who the server belongs to, and whether the chunk is asked for. */
  record Asked(Optional<UUID> owner, boolean on) {}
}
