package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockQueue;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.level.Level;

/**
 * The robot in a dock and the ones waiting for it. A robot reports itself on every tick, so one
 * that left, by any way, frees the dock.
 */
final class DockPresence {
  private static final long ROBOT_LOST_TICKS = 3;

  private UUID occupant;
  private long occupantSeen;
  private boolean robotFull;
  private DockQueue queue = DockQueue.EMPTY;

  void reportRobot(Level level, UUID robot, boolean full) {
    occupant = robot;
    robotFull = full;
    occupantSeen = level.getGameTime();
  }

  boolean isOccupied(Level level) {
    return occupant != null
        && level != null
        && level.getGameTime() - occupantSeen < ROBOT_LOST_TICKS;
  }

  boolean robotFull() {
    return robotFull;
  }

  Optional<UUID> occupant(Level level) {
    return isOccupied(level) ? Optional.of(occupant) : Optional.empty();
  }

  void reportWaiting(Level level, UUID robot) {
    queue = queue.report(robot, level.getGameTime());
  }

  int waitingCount(Level level) {
    return level == null ? 0 : queue.waitingCount(level.getGameTime());
  }

  /** Nobody waiting also counts: the robot asking is then the next one. */
  boolean isNextInLine(Level level, UUID robot) {
    return queue.first(level.getGameTime()).filter(first -> !first.equals(robot)).isEmpty();
  }
}
