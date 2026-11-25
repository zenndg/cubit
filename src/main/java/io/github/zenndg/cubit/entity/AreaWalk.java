package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.Optional;
import net.minecraft.world.phys.Vec3;

/**
 * How the area bends the way of the Cubit that works in it: going around the inner corner of an L
 * is a way and counts as progress, instead of cutting through the outside (answer 2365). Only the
 * work in the area walks like this: the Dock, the chests, a chase and an escape stay straight.
 */
final class AreaWalk {
  private AreaWalk() {}

  /** How much of the way to a place is left. */
  static double left(RobotEntity robot, Vec3 target) {
    Optional<WorkArea> area = workingIn(robot);
    if (area.isEmpty()) {
      return robot.position().distanceTo(target);
    }
    double ground = area.get().way(placeOf(robot.position()), placeOf(target));
    return Math.hypot(ground, target.y - robot.getY());
  }

  /** Where to go first to get to a place. */
  static Vec3 next(RobotEntity robot, Vec3 target) {
    Optional<WorkArea> area = workingIn(robot);
    if (area.isEmpty()) {
      return target;
    }
    WorkArea.Place goal = placeOf(target);
    WorkArea.Place next = area.get().next(placeOf(robot.position()), goal);
    return next.equals(goal) ? target : new Vec3(next.x(), robot.getY(), next.z());
  }

  /** Walks to a place by the way inside the area. False when the game finds no route. */
  static boolean to(RobotEntity robot, Vec3 target, double speed) {
    return ((RobotNavigation) robot.getNavigation())
        .moveInside(next(robot, target), speed, workingIn(robot));
  }

  static Optional<WorkArea> workingIn(RobotEntity robot) {
    return robot.workArea().working() ? robot.workArea().here() : Optional.empty();
  }

  static WorkArea.Place placeOf(Vec3 position) {
    return new WorkArea.Place(position.x, position.z);
  }
}
