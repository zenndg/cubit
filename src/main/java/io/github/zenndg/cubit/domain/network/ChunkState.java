package io.github.zenndg.cubit.domain.network;

/** Whether a block holds its chunk, and why not, for its screen (answers 2210 to 2215 and 2372). */
public enum ChunkState {
  HOLDING,
  /** The config of the world switched every chunk of the mod off (answer 2214). */
  LOCKED,
  SWITCHED_OFF,
  OWNER_OFFLINE,
  NO_ENERGY,
  PLAYER_LIMIT,
  WORLD_LIMIT,
  /** An antenna in the chunk its Server holds, which it does not pay again (answer 2264). */
  SHARED,
  /** An antenna stopped without signal, which waits to come back with it (answer 2553). */
  NO_SIGNAL;

  public boolean holds() {
    return this == HOLDING;
  }

  /** Nothing is held, and the place in the line is kept, to come back by itself. */
  public boolean waits() {
    return this == OWNER_OFFLINE || this == NO_SIGNAL;
  }
}
