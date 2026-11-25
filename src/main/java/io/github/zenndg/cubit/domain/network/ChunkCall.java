package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What decides whether a block holds its chunk, the reason the player can do least about first
 * (answers 2210 to 2215).
 */
public record ChunkCall(boolean allowed, boolean switchedOn, boolean ownerHere, Room room) {
  /** The numbers of the energy: what the chunk costs and the charge that takes it back. */
  public record Price(Energy perTick, Energy resume) {}

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
