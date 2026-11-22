package io.github.zenndg.cubit.domain.behavior;

/**
 * The stops of the round of the Galactic Patroller: the blocks walked since the last stop, the leg
 * it walks before the next one, and the stop it is in, when it looks left and right twice, slowly,
 * and then ahead before it walks on. It stops at each point, at each corner and at the end of each
 * leg, and any stop starts the count over with a new leg. The caller draws each stop and each leg
 * (answers 1615 to 1618 and 1628).
 */
public record PatrolStops(double walked, int leg, int left, int total) {
  /** Nothing walked and no leg yet: the caller draws the first one. */
  public static final PatrolStops START = new PatrolStops(0, 0, 0, 0);

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
    leg = Math.max(0, leg);
    left = Math.max(0, left);
    total = Math.max(left, total);
  }

  public boolean pausing() {
    return left > 0;
  }

  public boolean hasLeg() {
    return leg > 0;
  }

  /** The leg to walk before the next stop, when there is none yet. */
  public PatrolStops withLeg(int blocks) {
    return new PatrolStops(walked, blocks, left, total);
  }

  /** A stop now, which starts the count of blocks over with the next leg. */
  public PatrolStops stop(int ticks, int nextLeg) {
    return new PatrolStops(0, nextLeg, ticks, ticks);
  }

  /** The blocks walked in a tick, toward the end of the leg. */
  public PatrolStops walked(double blocks) {
    return new PatrolStops(walked + Math.max(0, blocks), leg, left, total);
  }

  /** The leg is walked, so a stop is due, never while it already stops. */
  public boolean due() {
    return !pausing() && hasLeg() && walked >= leg;
  }

  public PatrolStops tick() {
    return new PatrolStops(walked, leg, left - 1, total);
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
