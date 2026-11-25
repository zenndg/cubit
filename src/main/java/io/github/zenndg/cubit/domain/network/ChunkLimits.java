package io.github.zenndg.cubit.domain.network;

/** How many chunks the mod may hold, of a player and in the world, with 0 for no limit (2215). */
public record ChunkLimits(int perPlayer, int perWorld) {
  private static final String INVALID = "A chunk limit is never negative: ";
  private static final String SEPARATOR = ", ";

  public ChunkLimits {
    if (perPlayer < 0 || perWorld < 0) {
      throw new IllegalArgumentException(INVALID + perPlayer + SEPARATOR + perWorld);
    }
  }

  /** Whether one more fits, with what the others already hold. */
  public boolean fitsPlayer(int heldByOthersOfPlayer) {
    return perPlayer == 0 || heldByOthersOfPlayer < perPlayer;
  }

  public boolean fitsWorld(int heldByOthers) {
    return perWorld == 0 || heldByOthers < perWorld;
  }
}
