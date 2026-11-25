package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What decides whether a block holds its chunk, the reason the player can do least about first
 * (answers 2210 to 2215). A block that ran out only takes the chunk back with enough energy, so it
 * does not flicker (answer 2372).
 */
public record ChunkCall(boolean allowed, boolean switchedOn, boolean ownerHere, Room room) {
  /** The numbers of the energy: what the chunk costs and the charge that takes it back. */
  public record Price(Energy perTick, Energy resume) {}

  /** Whether the limits leave room, with what the other blocks hold. */
  public record Room(boolean ofPlayer, boolean ofWorld) {}

  public ChunkState decide(ChunkState before, Energy charge, Price price) {
    if (!allowed) {
      return ChunkState.LOCKED;
    }
    if (!switchedOn) {
      return ChunkState.SWITCHED_OFF;
    }
    if (!ownerHere) {
      return ChunkState.OWNER_OFFLINE;
    }
    if (!pays(before, charge, price)) {
      return ChunkState.NO_ENERGY;
    }
    if (!room.ofPlayer()) {
      return ChunkState.PLAYER_LIMIT;
    }
    return room.ofWorld() ? ChunkState.HOLDING : ChunkState.WORLD_LIMIT;
  }

  private static boolean pays(ChunkState before, Energy charge, Price price) {
    Energy needed = before == ChunkState.NO_ENERGY ? price.resume() : price.perTick();
    return charge.amount() >= needed.amount();
  }
}
