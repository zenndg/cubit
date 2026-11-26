package io.github.zenndg.cubit.domain.network;

/**
 * Why an antenna is not placed, shown on the action bar with the item kept in the hand, or none
 * (answers 2271, 2301, 2546 and the Delivery 4, detail 14).
 */
public enum AntennaPlacement {
  READY,
  NOT_LINKED,
  NO_SERVER,
  OTHER_DIMENSION,
  CHUNK_TAKEN,
  CHANNELS_FULL;

  public static AntennaPlacement decide(Check check) {
    if (!check.linked()) {
      return NOT_LINKED;
    }
    if (!check.serverExists()) {
      return NO_SERVER;
    }
    if (!check.sameDimension()) {
      return OTHER_DIMENSION;
    }
    if (check.chunkTaken()) {
      return CHUNK_TAKEN;
    }
    return check.channelFree() ? READY : CHANNELS_FULL;
  }

  public boolean allows() {
    return this == READY;
  }

  /**
   * What the game found: whether the item knows a Server, the Server still exists, it is in the
   * same dimension, the chunk already has an antenna and an open channel is free.
   */
  public record Check(
      boolean linked,
      boolean serverExists,
      boolean sameDimension,
      boolean chunkTaken,
      boolean channelFree) {}
}
