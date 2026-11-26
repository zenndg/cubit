package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What decides whether a block holds its chunk, the reason the player can do least about first
 * (answers 2210 to 2215).
 */
public record ChunkCall(boolean allowed, boolean switchedOn, boolean ownerHere, Room room) {
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

  /** Whether the limits leave room, with what the other blocks hold. */
  public record Room(boolean ofPlayer, boolean ofWorld) {}

  public ChunkState decide(ChunkCharge charge) {
    if (!allowed) {
      return ChunkState.LOCKED;
    }
    if (!switchedOn) {
      return ChunkState.SWITCHED_OFF;
    }
    if (!ownerHere) {
      return ChunkState.OWNER_OFFLINE;
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
