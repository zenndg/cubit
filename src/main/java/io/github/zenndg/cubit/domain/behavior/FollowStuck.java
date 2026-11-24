package io.github.zenndg.cubit.domain.behavior;

/**
 * A robot that follows and has no way to its owner: without getting closer for a while, it appears
 * near the owner, once until it has a way again (answer 2092).
 *
 * @param nearest how close it got since it lost the way
 * @param ticks for how long it has not advanced
 * @param spent whether it already appeared near the owner this time
 */
public record FollowStuck(double nearest, int ticks, boolean spent) {
  public static final FollowStuck START = new FollowStuck(Double.MAX_VALUE, 0, false);

  private static final double PROGRESS_BLOCKS = 0.5;
  private static final String INVALID = "Invalid follow: ";

  public FollowStuck {
    if (Double.isNaN(nearest) || ticks < 0) {
      throw new IllegalArgumentException(INVALID + nearest);
    }
  }

  public FollowStuck update(boolean hasWay, double distance, int elapsed) {
    if (hasWay) {
      return START;
    }
    return distance < nearest - PROGRESS_BLOCKS
        ? new FollowStuck(distance, 0, spent)
        : new FollowStuck(nearest, ticks + elapsed, spent);
  }

  public boolean isStuck(int limit) {
    return !spent && ticks >= limit;
  }

  public FollowStuck appeared() {
    return new FollowStuck(Double.MAX_VALUE, 0, true);
  }
}
