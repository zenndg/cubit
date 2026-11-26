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
  WORLD_LIMIT;

  public boolean holds() {
    return this == HOLDING;
  }
}
