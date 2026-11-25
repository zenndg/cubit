package io.github.zenndg.cubit.domain.behavior;

import java.util.UUID;

/**
 * A Cubit in an unloaded chunk, on its way to be found. Its owner called it or respawned, and the
 * search gives up after a while, so no chunk stays loaded (answers 2456, 2464 and 2485).
 */
public record CallSearch(UUID robot, Cause cause, int ticksLeft) {
  private static final String INVALID = "A search lasts: ";

  /** Who started it: a call of the owner, or the owner who respawned. */
  public enum Cause {
    CALL,
    RESPAWN
  }

  public CallSearch {
    if (ticksLeft < 0) {
      throw new IllegalArgumentException(INVALID + ticksLeft);
    }
  }

  public CallSearch tick() {
    return new CallSearch(robot, cause, Math.max(0, ticksLeft - 1));
  }

  public boolean isOver() {
    return ticksLeft == 0;
  }
}
