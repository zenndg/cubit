package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.Linger;
import io.github.zenndg.cubit.domain.behavior.WingHop;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.PatrollerSpec;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The wings of the Galactic Patroller on the move: it walks the path of the game, and when that
 * path does not reach or is far longer than the straight line, it hops with a short flight over
 * what is in the way or across a gap, to a safe landing it checked first, paying the energy of a
 * jump (answers 1647, 1648, 1650, 1651, 1654 and 1738). Without a landing or a charge, it walks.
 */
final class RobotWings {
  private static final double FORWARD_SPEED = 0.35;
  private static final double RISE_SPEED = 0.42;
  private static final double LANDED_BLOCKS = 1;
  private static final int MIN_LANDING = 2;
  private static final int MAX_FLIGHT_TICKS = 60;
  private static final double HALF = 0.5;
  private static final float QUARTER_TURN = 90f;
  private static final double AIRBORNE_SPEED = 0.15;

  private final RobotEntity robot;
  private Vec3 landing;
  private double apex;
  private int flightTicks;
  private Linger open = Linger.OFF;

  RobotWings(RobotEntity robot) {
    this.robot = robot;
  }

  /** In a short flight, which the goals leave alone until it lands. */
  boolean flying() {
    return landing != null;
  }

  /**
   * Walks to a place, or hops when the way on foot does not reach or is too long. Out of a chase,
   * it never lands out of its area (answer 1650).
   */
  /**
   * In use in the air, the wings stay open and big a while after the landing or the end of the
   * flight (answers 1553, 1806 and 1807). Without the card, they close at once (answer 1799).
   */
  void updateOpen(boolean armed, long now) {
    if (!armed) {
      open = Linger.OFF;
      return;
    }
    open = open.tick(flying() || inTheAir(), now, CubitMod.CONFIG.patrol().wingsLingerTicks());
  }

  boolean open(long now) {
    return open.on(now);
  }

  /** Off the ground and moving up or down, so a step on a block does not open the wings. */
  private boolean inTheAir() {
    return !robot.onGround() && Math.abs(robot.getDeltaMovement().y) > AIRBORNE_SPEED;
  }

  void moveTo(Vec3 target, double speed, boolean chasing) {
    if (flying()) {
      return;
    }
    robot.getNavigation().moveTo(target.x, target.y, target.z, speed);
    RobotPatrolCombat combat = robot.workArea().work().patrolCombat();
    if (!combat.armed() || !robot.onGround()) {
      return;
    }
    Path path = robot.getNavigation().getPath();
    boolean reaches = path != null && path.canReach();
    double straight = robot.position().distanceTo(target);
    double factor = CubitMod.CONFIG.patrol().wingDetourFactor();
    if (WingHop.worthHopping(reaches, length(path), straight, factor)) {
      landingToward(target, chasing).ifPresent(this::takeOff);
    }
  }

  /** On every tick of the server: the flight goes on until it lands. */
  void tick() {
    if (!flying()) {
      return;
    }
    Vec3 toLanding = landing.subtract(robot.position()).multiply(1, 0, 1);
    double distance = toLanding.horizontalDistance();
    boolean landed = robot.onGround() && flightTicks > 1 && distance < LANDED_BLOCKS;
    if (landed
        || ++flightTicks > MAX_FLIGHT_TICKS
        || !robot.workArea().work().patrolCombat().armed()) {
      landing = null;
      return;
    }
    WingHop.Motion motion = WingHop.motion(robot.getY(), apex, distance, FORWARD_SPEED, RISE_SPEED);
    Vec3 way = distance > 0 ? toLanding.normalize() : Vec3.ZERO;
    double up = motion.rising() ? motion.up() : robot.getDeltaMovement().y;
    robot.setDeltaMovement(way.x * motion.forward(), up, way.z * motion.forward());
    robot.hasImpulse = true;
    robot.resetFallDistance();
    if (distance > 0) {
      float yaw = (float) Math.toDegrees(Math.atan2(way.z, way.x)) - QUARTER_TURN;
      robot.setYRot(yaw);
      robot.setYBodyRot(yaw);
    }
  }

  private void takeOff(Vec3 spot) {
    PatrollerSpec spec = robot.workArea().work().patrolCombat().spec();
    robot.setBattery(spec.payForJump(robot.battery()));
    robot.playSound(CubitSounds.PATROL_JUMP.get());
    robot.getNavigation().stop();
    landing = spot;
    flightTicks = 0;
  }

  /** The walked length of the path of the game, from where it stands. */
  private double length(Path path) {
    if (path == null) {
      return Double.MAX_VALUE;
    }
    double total = 0;
    Vec3 from = robot.position();
    for (int index = path.getNextNodeIndex(); index < path.getNodeCount(); index++) {
      Node node = path.getNode(index);
      Vec3 to = new Vec3(node.x + HALF, node.y, node.z + HALF);
      total += from.distanceTo(to);
      from = to;
    }
    return total;
  }

  /**
   * The nearest safe landing toward the place, past something in the way, within the gap of the
   * tier, with the flight within its jump and a free way (answers 1647, 1648 and 1738).
   */
  private Optional<Vec3> landingToward(Vec3 target, boolean chasing) {
    PatrollerSpec spec = robot.workArea().work().patrolCombat().spec();
    if (!spec.canJump(robot.battery())) {
      return Optional.empty();
    }
    Vec3 way = target.subtract(robot.position()).multiply(1, 0, 1).normalize();
    WingFlight flight = new WingFlight(robot, way, spec.jump().height());
    for (int reach = MIN_LANDING; reach <= spec.jump().gap() + 1; reach++) {
      Optional<Vec3> found = flight.landingAt(reach).filter(spot -> allowed(spot, chasing));
      if (found.isPresent()) {
        apex = flight.apex();
        return found;
      }
    }
    return Optional.empty();
  }

  /** Out of a chase, a landing out of its area is not allowed (answer 1650). */
  private boolean allowed(Vec3 spot, boolean chasing) {
    Optional<WorkArea> area = robot.workArea().here();
    return chasing || area.isEmpty() || area.get().contains(spot.x, spot.y, spot.z);
  }

  /** The checks of the world for one flight: the landing, what is in the way and a free way. */
  private static final class WingFlight {
    private final RobotEntity robot;
    private final Vec3 way;
    private final int jumpHeight;
    private double apex;

    WingFlight(RobotEntity robot, Vec3 way, int jumpHeight) {
      this.robot = robot;
      this.way = way;
      this.jumpHeight = jumpHeight;
    }

    double apex() {
      return apex;
    }

    Optional<Vec3> landingAt(int reach) {
      Optional<BlockPos> stand = standAt(column(reach));
      if (stand.isEmpty() || !somethingInTheWay(reach)) {
        return Optional.empty();
      }
      Vec3 spot = Vec3.atBottomCenterOf(stand.get());
      apex = WingHop.apex(robot.getY(), spot.y, highestOnTheWay(reach));
      if (!WingHop.withinJump(robot.getY(), apex, jumpHeight) || !freeWay(reach, spot)) {
        return Optional.empty();
      }
      return Optional.of(spot);
    }

    private BlockPos column(int reach) {
      Vec3 at = robot.position().add(way.scale(reach));
      return BlockPos.containing(at.x, robot.getY(), at.z);
    }

    /** The highest place to stand in the column, from the jump above down to the glide below. */
    private Optional<BlockPos> standAt(BlockPos column) {
      int drop = CubitMod.CONFIG.patrol().glideDropBlocks();
      for (int up = jumpHeight; up >= -drop; up--) {
        BlockPos at = column.above(up);
        if (FollowOwnerGoal.canStandAt(robot, at)) {
          return Optional.of(at);
        }
      }
      return Optional.empty();
    }

    /** A wall, a fence or a gap between, which the path on foot could not take. */
    private boolean somethingInTheWay(int reach) {
      for (int step = 1; step < reach; step++) {
        BlockPos at = column(step);
        boolean walk =
            FollowOwnerGoal.canStandAt(robot, at)
                || FollowOwnerGoal.canStandAt(robot, at.above())
                || FollowOwnerGoal.canStandAt(robot, at.below());
        if (!walk) {
          return true;
        }
      }
      return false;
    }

    private double highestOnTheWay(int reach) {
      Level level = robot.level();
      double highest = robot.getY();
      for (int step = 1; step < reach; step++) {
        BlockPos column = column(step);
        for (int up = 0; up <= jumpHeight + 1; up++) {
          BlockPos at = column.above(up);
          if (!level.getBlockState(at).getCollisionShape(level, at).isEmpty()) {
            highest = Math.max(highest, at.getY() + 1);
          }
        }
      }
      return highest;
    }

    /** Room to rise where it stands, to cross at the top and to come down on the landing. */
    private boolean freeWay(int reach, Vec3 spot) {
      AABB body = robot.getBoundingBox();
      Level level = robot.level();
      if (!level.noCollision(robot, body.expandTowards(0, apex - robot.getY(), 0))) {
        return false;
      }
      for (int step = 1; step <= reach; step++) {
        Vec3 at = robot.position().add(way.scale(step));
        if (!level.isLoaded(BlockPos.containing(at))
            || !level.noCollision(
                robot, body.move(at.x - robot.getX(), apex - robot.getY(), at.z - robot.getZ()))) {
          return false;
        }
      }
      AABB down = body.move(spot.x - robot.getX(), spot.y - robot.getY(), spot.z - robot.getZ());
      return level.noCollision(robot, down.expandTowards(0, apex - spot.y, 0));
    }
  }
}
