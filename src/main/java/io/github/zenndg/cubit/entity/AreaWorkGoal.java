package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * The Cubit in its area, a volume between two corners: it walks there, or appears inside when
 * stuck, and then goes round the edges, from corner to corner, at the height it walks (answers 342,
 * 1058, 1059 and 1062). The Galactic Patroller walks them at the pace of a lookout, stopping at
 * each corner and every so many blocks to look around, out of the area (answers 1614, 1616 and
 * 1626). Fetching an item comes first, by its priority.
 */
final class AreaWorkGoal extends Goal {
  /** The robot speed is already in its movement attribute, so the route does not change it. */
  private static final double ROUTE_SPEED_MODIFIER = 1;

  private static final double ARRIVED_BLOCKS = 1.5;
  private static final double PROGRESS_BLOCKS = 0.5;
  private static final double HALF = 0.5;
  private static final float QUARTER_TURN = 90f;

  private final RobotEntity robot;
  private final AreaReturn back;
  private final PatrolPause pause;
  private int corner;
  private int repathCountdown;
  private double nearest;
  private int stuckTicks;

  AreaWorkGoal(RobotEntity robot) {
    this.robot = robot;
    this.back = new AreaReturn(robot);
    this.pause = new PatrolPause(robot, this::outside);
    setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    RobotWork work = robot.workArea().work();
    return robot.workArea().working()
        && !work.fishing().active()
        && !work.farming().active()
        && !work.organizing().active()
        && !work.cooking().active()
        && !work.patrol().active()
        && !work.escort().active();
  }

  @Override
  public boolean canContinueToUse() {
    return canUse();
  }

  /** The stuck time counts ticks, so this goal runs on all of them. */
  @Override
  public boolean requiresUpdateEveryTick() {
    return true;
  }

  @Override
  public void start() {
    repathCountdown = 0;
    pause.reset();
    resetProgress();
    if (patroller()) {
      robot
          .workArea()
          .here()
          .ifPresent(area -> corner = area.nearestCorner(robot.getX(), robot.getZ()));
    }
    spreadCorner();
  }

  @Override
  public void stop() {
    robot.getNavigation().stop();
  }

  @Override
  public void tick() {
    WorkArea area = robot.workArea().here().orElse(null);
    if (robot.workArea().work().patrolCombat().wings().flying()) {
      return;
    }
    if (area == null || (patroller() && pause.hold())) {
      return;
    }
    boolean inside = area.contains(robot.getX(), robot.getY(), robot.getZ());
    Vec3 target = inside ? spotOf(area, area.corner(corner)) : centerOf(area);
    double distance = robot.position().subtract(target).horizontalDistance();
    if (inside && distance < ARRIVED_BLOCKS) {
      reachedCorner();
      return;
    }
    if (stuck(distance)) {
      unstick(area, inside);
      return;
    }
    walkTo(target);
  }

  /** The Patroller stops at each corner to look around (answer 1616). */
  private void reachedCorner() {
    if (patroller()) {
      robot.workArea().work().patrol().stopHere();
    }
    nextCorner();
  }

  private void walkTo(Vec3 target) {
    if (patroller()) {
      pause.count();
    }
    if (--repathCountdown <= 0) {
      repathCountdown = CubitMod.CONFIG.followRepathTicks();
      robot.workArea().work().patrolCombat().wings().moveTo(target, speed(), false);
    }
  }

  /** The Patrollers on the same edges each walk to a different corner (answers 1646 and 1708). */
  private void spreadCorner() {
    if (patroller()) {
      corner = robot.workArea().work().patrol().spreadCorner(corner);
    }
  }

  /** On its edges, the Patroller looks out of the area, where a threat comes from (1626). */
  private Optional<Float> outside() {
    return robot
        .workArea()
        .here()
        .map(area -> area.outwardFrom(robot.getX(), robot.getZ()))
        .map(out -> (float) Math.toDegrees(Math.atan2(out.z(), out.x())) - QUARTER_TURN);
  }

  private boolean patroller() {
    return robot.workArea().work().patrol().hasCard();
  }

  /** The Patroller walks its edges slower; every other card keeps the speed of the robot. */
  private double speed() {
    return patroller() ? robot.workArea().work().patrol().speed() : ROUTE_SPEED_MODIFIER;
  }

  /** A corner it cannot reach is skipped, and outside the area it appears inside (answer 1058). */
  private void unstick(WorkArea area, boolean inside) {
    if (inside) {
      nextCorner();
    } else {
      back.appearInside(area);
      pause.reset();
      resetProgress();
    }
  }

  private void nextCorner() {
    corner++;
    spreadCorner();
    repathCountdown = 0;
    resetProgress();
  }

  private boolean stuck(double distance) {
    if (distance < nearest - PROGRESS_BLOCKS) {
      nearest = distance;
      stuckTicks = 0;
      return false;
    }
    return ++stuckTicks >= CubitMod.CONFIG.dock().stuckTicks();
  }

  private void resetProgress() {
    nearest = Double.MAX_VALUE;
    stuckTicks = 0;
  }

  /**
   * The corner of the base at the height the Cubit is, within the area: the area is a volume, so
   * the round follows its edges on the level the Cubit walks, not under the ground.
   */
  private Vec3 spotOf(WorkArea area, AreaCorner corner) {
    return new Vec3(corner.x() + HALF, back.levelIn(area), corner.z() + HALF);
  }

  private Vec3 centerOf(WorkArea area) {
    return new Vec3(area.centerX(), back.levelIn(area), area.centerZ());
  }
}
