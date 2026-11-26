package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What decides whether a block holds its chunk, the reason the player can do least about first
 * (answers 2210 to 2215). An antenna also shares the chunk its Server holds and waits for its
 * signal (answers 2264 and 2553).
 */
public record ChunkCall(
    boolean allowed, boolean switchedOn, boolean ownerHere, Room room, Reach reach) {
  /** A block that holds its own chunk and always reaches, as the Network Server. */
  public ChunkCall(boolean allowed, boolean switchedOn, boolean ownerHere, Room room) {
    this(allowed, switchedOn, ownerHere, room, Reach.OWN);
  }

  /** The numbers of the energy: what the chunk costs and the charge that takes it back. */
  public record Price(Energy perTick, Energy resume) {
    private static final long TICKS_PER_SECOND = 20;

    /**
     * The charge that takes the chunk back pays some seconds of the whole upkeep, never less than
     * the minimum nor than one tick of the chunk, so it does not flicker (answers 2372 and 2438).
     */
    public static Price of(Energy perTick, Energy standby, Energy minimum, int seconds) {
      long whole = perTick.plus(standby).amount() * TICKS_PER_SECOND * seconds;
      long resume = Math.max(Math.max(whole, minimum.amount()), perTick.amount());
      return new Price(perTick, new Energy(resume));
    }
  }

  /**
   * Whether its chunk is already held by its Server, and whether it still works, with signal or
   * before it stops.
   */
  public record Reach(boolean shared, boolean signal) {
    public static final Reach OWN = new Reach(false, true);
  }

  /** Whether the limits leave room, with what the other blocks hold. */
  public record Room(boolean ofPlayer, boolean ofWorld) {}

  public ChunkState decide(ChunkCharge charge) {
    if (!allowed) {
      return ChunkState.LOCKED;
    }
    if (!switchedOn) {
      return ChunkState.SWITCHED_OFF;
    }
    if (reach.shared()) {
      return ChunkState.SHARED;
    }
    if (!ownerHere) {
      return ChunkState.OWNER_OFFLINE;
    }
    if (!reach.signal()) {
      return ChunkState.NO_SIGNAL;
    }
    if (charge.drained()) {
      return ChunkState.NO_ENERGY;
    }
    if (!room.ofPlayer()) {
      return ChunkState.PLAYER_LIMIT;
    }
    return room.ofWorld() ? ChunkState.HOLDING : ChunkState.WORLD_LIMIT;
  }
}
