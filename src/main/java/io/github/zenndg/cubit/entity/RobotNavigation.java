package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

/**
 * The navigation of a robot, where stopping really stops, and whose routes go through the doors its
 * owner marked (answer 1364).
 */
final class RobotNavigation extends GroundPathNavigation {
  private Optional<WorkArea> kept = Optional.empty();

  RobotNavigation(Mob mob, Level level) {
    super(mob, level);
  }

  @Override
  protected PathFinder createPathFinder(int maxVisitedNodes) {
    nodeEvaluator = new RobotNodeEvaluator();
    nodeEvaluator.setCanPassDoors(true);
    return new PathFinder(nodeEvaluator, maxVisitedNodes);
  }

  /** The area whose blocks the route of the moment prefers, if any (answer 2365). */
  Optional<WorkArea> kept() {
    return kept;
  }

  /** A route of the work in an area, which leaves it only with no other way (answer 2365). */
  boolean moveInside(Vec3 target, double speed, Optional<WorkArea> area) {
    kept = area;
    return super.moveTo(target.x, target.y, target.z, speed);
  }

  @Override
  public boolean moveTo(double x, double y, double z, double speed) {
    kept = Optional.empty();
    return super.moveTo(x, y, z, speed);
  }

  @Override
  public boolean moveTo(Entity target, double speed) {
    kept = Optional.empty();
    return super.moveTo(target, speed);
  }

  /**
   * The game keeps a route update delayed by nearby block changes through a stop, and that update
   * would give a stopped robot a new route to its old target.
   */
  @Override
  public void stop() {
    super.stop();
    kept = Optional.empty();
    hasDelayedRecomputation = false;
  }
}
