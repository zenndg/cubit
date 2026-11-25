package io.github.zenndg.cubit.domain.behavior;

/** What came of calling a Cubit, as the owner is told (answers 2457 to 2465 and 2485). */
public enum CallResult {
  /** Close by, it walks to its owner. */
  COMING,
  /** It appeared beside its owner. */
  ARRIVED,
  /** Its chunk is loading. */
  SEARCHING,
  /** It already follows its owner nearby. */
  HERE,
  /** Too soon after the last call. */
  WAIT,
  /** Another Cubit of the player is being searched. */
  BUSY,
  /** No Cubit of the player, or it was not where it was last seen. */
  NOT_FOUND,
  /** No safe spot near the owner, so it stays where it is. */
  NO_SPOT;

  /** Whether the Cubit comes, now or when its chunk loads. */
  public boolean comes() {
    return this == COMING || this == ARRIVED || this == SEARCHING;
  }
}
