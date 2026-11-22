package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolStops;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.phys.Vec3;

/**
 * The stops of the round of the Galactic Patroller, for the goal of its route and for the edges of
 * its area: while a stop lasts it stands still and turns its head slowly, from the way it faced,
 * and the blocks it walks count toward the next stop (answers 1614 to 1618).
 */
final class PatrolPause {
  private static final float LOOK_ANGLE = 70f;
  private static final float LOOK_SPEED = 4f;

  private final RobotEntity robot;
  private final Supplier<Optional<Float>> start;
  private float facing;
  private boolean stopped;
  private Vec3 last;

  PatrolPause(RobotEntity robot) {
    this(robot, Optional::empty);
  }

  /**
   * A stop that starts its look from a given way, as the outside of the area on its edges, or else
   * from the way it faced (answer 1626).
   */
  PatrolPause(RobotEntity robot, Supplier<Optional<Float>> start) {
    this.robot = robot;
    this.start = start;
  }

  /** True while it stands for a stop, so the goal does nothing else in the tick. */
  boolean hold() {
    RobotPatrol patrol = robot.workArea().work().patrol();
    if (!patrol.pausing()) {
      stopped = false;
      return false;
    }
    if (!stopped) {
      stopped = true;
      facing = start.get().orElse(robot.getYRot());
      robot.getNavigation().stop();
    }
    look(patrol.gaze());
    patrol.waitOut();
    last = robot.position();
    return true;
  }

  /** The ground covered since the last tick, so a teleport or a chase never counts. */
  void count() {
    Vec3 now = robot.position();
    if (last != null) {
      robot.workArea().work().patrol().walked(now.subtract(last).horizontalDistance());
    }
    last = now;
  }

  /** On the start of a goal or after a teleport, the count goes on from where it stands. */
  void reset() {
    last = null;
  }

  private void look(PatrolStops.Gaze gaze) {
    float turn =
        switch (gaze) {
          case LEFT -> LOOK_ANGLE;
          case RIGHT -> -LOOK_ANGLE;
          case AHEAD -> 0;
        };
    Vec3 look = Vec3.directionFromRotation(0, facing + turn).add(robot.getEyePosition());
    robot.getLookControl().setLookAt(look.x, look.y, look.z, LOOK_SPEED, LOOK_SPEED);
  }
}
