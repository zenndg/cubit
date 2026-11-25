package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

/**
 * The route of a Cubit goes through a shut door only when its owner marked it and the Cubit can pay
 * to open it, also for iron, which the mobs of the game never open (answers 1364 to 1368). The
 * other doors stay as the game sees them, shut for the Cubit.
 */
final class RobotNodeEvaluator extends WalkNodeEvaluator {
  private static final Set<PathType> SHUT =
      EnumSet.of(
          PathType.DOOR_WOOD_CLOSED, PathType.DOOR_IRON_CLOSED, PathType.FENCE, PathType.TRAPDOOR);
  private static final Set<PathType> TWO_HIGH =
      EnumSet.of(PathType.DOOR_WOOD_CLOSED, PathType.DOOR_IRON_CLOSED);

  /** Enough for the way around inside to win over the cut through the outside. */
  private static final float OUTSIDE_COST = 8;

  private Set<Long> passable = Set.of();
  private Optional<WorkArea> area = Optional.empty();

  @Override
  public void prepare(PathNavigationRegion region, Mob mob) {
    super.prepare(region, mob);
    if (mob instanceof RobotEntity robot) {
      passable = DoorPassage.passable(robot);
      WorkArea.Place here = AreaWalk.placeOf(robot.position());
      area =
          ((RobotNavigation) robot.getNavigation()).kept().filter(found -> found.holds(here, here));
    }
  }

  /** A way to a place out of the area costs the same everywhere. */
  @Override
  public Target getTarget(double x, double y, double z) {
    WorkArea.Place goal = new WorkArea.Place(x, z);
    area = area.filter(found -> found.holds(goal, goal));
    return super.getTarget(x, y, z);
  }

  /** From a place of the area to another, the blocks outside are the last choice (2365). */
  @Override
  protected Node findAcceptedNode(
      int x,
      int y,
      int z,
      int verticalDeltaLimit,
      double nodeFloorLevel,
      Direction direction,
      PathType pathType) {
    Node node =
        super.findAcceptedNode(x, y, z, verticalDeltaLimit, nodeFloorLevel, direction, pathType);
    if (node != null && node.costMalus >= 0 && outside(node)) {
      node.costMalus = Math.max(node.costMalus, OUTSIDE_COST);
    }
    return node;
  }

  /** A diagonal past a block outside would graze it, so it goes around straight. */
  @Override
  protected boolean isDiagonalValid(Node root, Node xNode, Node zNode) {
    boolean grazes = area.isPresent() && !outside(root) && (grazes(xNode) || grazes(zNode));
    return !grazes && super.isDiagonalValid(root, xNode, zNode);
  }

  private boolean grazes(Node node) {
    return node != null && outside(node);
  }

  private boolean outside(Node node) {
    return area.isPresent() && !area.get().shape().has(node.x, node.z);
  }

  @Override
  public void done() {
    super.done();
    passable = Set.of();
    area = Optional.empty();
  }

  @Override
  public PathType getPathType(PathfindingContext context, int x, int y, int z) {
    PathType type = super.getPathType(context, x, y, z);
    if (passable.isEmpty() || !SHUT.contains(type)) {
      return type;
    }
    boolean marked =
        passable.contains(BlockPos.asLong(x, y, z))
            || (TWO_HIGH.contains(type) && passable.contains(BlockPos.asLong(x, y - 1, z)));
    return marked ? PathType.WALKABLE_DOOR : type;
  }
}
