package io.github.zenndg.cubit.domain.behavior;

/**
 * The stops of the round of the Galactic Patroller: the blocks walked since the last stop, and the
 * stop it is in, when it looks left and right twice, slowly, and then ahead before it walks on. It
 * stops at each point, at each corner and every so many blocks walked, and any stop starts the
 * count over (answers 1615 to 1618).
 */
public record PatrolStops(double walked, int left, int total) {
  public static final PatrolStops START = new PatrolStops(0, 0, 0);

  /** Two looks to each side, then ahead: the stop splits into five equal parts (answer 1615). */
  private static final int LOOKS = 4;

  private static final int PARTS = LOOKS + 1;

  /** Where the head turns, from the way it faced when it stopped. */
  public enum Gaze {
    LEFT,
    RIGHT,
    AHEAD
  }

  public PatrolStops {
    walked = Math.max(0, walked);
    left = Math.max(0, left);
    total = Math.max(left, total);
  }

  public boolean pausing() {
    return left > 0;
  }

  /** A stop now, at a point or a corner, which starts the count of blocks over. */
  public PatrolStops stop(int ticks) {
    return new PatrolStops(0, ticks, ticks);
  }

  /** The blocks walked in a tick, which bring the next stop once they reach the distance. */
  public PatrolStops walked(double blocks, PatrolSettings.Round round) {
    double next = walked + Math.max(0, blocks);
    return next >= round.stopDistance()
        ? stop(round.stopTicks())
        : new PatrolStops(next, left, total);
  }

  public PatrolStops tick() {
    return new PatrolStops(walked, left - 1, total);
  }

  public Gaze gaze() {
    int part = total / PARTS;
    if (!pausing() || part == 0) {
      return Gaze.AHEAD;
    }
    int look = (total - left) / part;
    if (look >= LOOKS) {
      return Gaze.AHEAD;
    }
    return look % 2 == 0 ? Gaze.LEFT : Gaze.RIGHT;
  }
}
