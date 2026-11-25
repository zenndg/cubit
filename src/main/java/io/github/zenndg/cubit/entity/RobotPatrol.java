package io.github.zenndg.cubit.entity;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolProgress;
import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import io.github.zenndg.cubit.domain.behavior.PatrolSpread;
import io.github.zenndg.cubit.domain.behavior.PatrolStops;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.robot.PatrolRoute;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.domain.robot.RouteMarking;
import io.github.zenndg.cubit.domain.robot.RoutePoint;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * The route of the Galactic Patroller and where it is in its round. The points live only in the
 * save of the entity, under PatrolRoute: they stay without the card and are lost when the Cubit is
 * collected, like its area (answers 1561, 1579, 1581, 1582 and 1602). Only the points inside its
 * area and in its dimension count (answer 1599).
 */
public final class RobotPatrol {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String KEY = "PatrolRoute";
  private static final String DIMENSION_KEY = "Dimension";
  private static final String X_KEY = "X";
  private static final String Y_KEY = "Y";
  private static final String Z_KEY = "Z";
  private static final String INVALID_POINT = "Robot {} has an invalid point of patrol: {}";

  private final RobotEntity robot;
  private PatrolRoute route = PatrolRoute.EMPTY;
  private PatrolProgress progress = PatrolProgress.START;
  private PatrolStops stops = PatrolStops.START;
  private long progressSince;
  private int edgeCorner;
  private long edgeSince;

  RobotPatrol(RobotEntity robot) {
    this.robot = robot;
  }

  public PatrolRoute route() {
    return route;
  }

  void setRoute(PatrolRoute next) {
    route = next;
    walkTo(progress.within(walkable().size()));
  }

  /** How many points the tier keeps, from the config (answer 1579). */
  public int limit() {
    return robot.spec().jobs().patroller().routePoints();
  }

  /**
   * A click of the owner with the Mark app: a marked point comes out, and the top of a solid block
   * inside the area goes in at the end (answers 1579, 1599 and 1611). Nothing for anyone else.
   */
  public Optional<RouteMarking.Result> click(Player player, BlockPos pos) {
    if (!Ownership.owns(robot.ownerId(), player.getUUID())) {
      return Optional.empty();
    }
    Level level = player.level();
    RoutePoint point = pointAt(level, pos);
    boolean inside =
        robot.workArea().area().filter(area -> area.isIn(point.dimension())).stream()
            .anyMatch(area -> area.contains(point.x(), point.y(), point.z()));
    boolean solid =
        level.isLoaded(pos) && level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP);
    RouteMarking.Click click =
        new RouteMarking.Click(robot.workArea().area().isPresent(), inside, solid, point);
    RouteMarking.Marked marked = RouteMarking.click(route, click, limit());
    setRoute(marked.route());
    return Optional.of(marked.result());
  }

  /** Shift and a click of the owner: the clicked point, or else the last one marked. */
  public boolean unmark(Player player, Optional<BlockPos> clicked) {
    if (!Ownership.owns(robot.ownerId(), player.getUUID())) {
      return false;
    }
    Optional<PatrolRoute> next =
        RouteMarking.unmark(route, clicked.map(pos -> pointAt(player.level(), pos)));
    next.ifPresent(this::setRoute);
    return next.isPresent();
  }

  private static RoutePoint pointAt(Level level, BlockPos pos) {
    return new RoutePoint(RobotUnload.dimensionOf(level), pos.getX(), pos.getY(), pos.getZ());
  }

  /** The card of the Patroller is in the slot. */
  public boolean hasCard() {
    return robot.workArea().work().job().filter(RobotJob.PATROLLER::equals).isPresent();
  }

  /** The points it walks now: in its dimension and inside its area. */
  PatrolRoute walkable() {
    Optional<WorkArea> area = robot.workArea().here();
    if (area.isEmpty()) {
      return PatrolRoute.EMPTY;
    }
    PatrolRoute here = route.in(RobotUnload.dimensionOf(robot.level()));
    return new PatrolRoute(
        here.points().stream()
            .filter(point -> area.get().contains(point.x(), point.y(), point.z()))
            .toList());
  }

  /** Walking the points: the card, the area and at least one point inside it. */
  public boolean active() {
    return hasCard() && robot.workArea().working() && !walkable().isEmpty();
  }

  /** The point it walks to, skipping the ones in a chunk that is not loaded (rule 22). */
  Optional<BlockPos> currentPoint() {
    PatrolRoute walkable = walkable();
    walkTo(progress.within(walkable.size()));
    for (int tried = 0; tried < walkable.size(); tried++) {
      Optional<BlockPos> point =
          walkable.at(progress.index()).map(found -> new BlockPos(found.x(), found.y(), found.z()));
      if (point.filter(robot.level()::isLoaded).isPresent()) {
        return point;
      }
      walkTo(progress.next(walkable.size()));
    }
    return Optional.empty();
  }

  boolean pausing() {
    return stops.pausing();
  }

  PatrolStops.Gaze gaze() {
    return stops.gaze();
  }

  /** It reached its point: a stop there, and then the next point (answer 1615). */
  void arrived() {
    stopHere();
    walkTo(progress.next(walkable().size()));
    spread();
  }

  /** Each change of the point it walks to is timed, to know who came first (answer 1708). */
  private void walkTo(PatrolProgress next) {
    if (next.index() != progress.index()) {
      progressSince = robot.level().getGameTime();
    }
    progress = next;
  }

  /**
   * When a mate on the same route heads to the same point first, it goes to the point farthest from
   * its mates, at the start of the round and at each point (answers 1646 and 1708).
   */
  void spread() {
    PatrolRoute route = walkable();
    List<PatrolSpread.Walker> mates =
        PatrolTeams.mates(robot).stream()
            .map(mate -> mate.workArea().work().patrol())
            .filter(mate -> mate.active() && mate.walkable().equals(route))
            .map(RobotPatrol::walker)
            .toList();
    walkTo(new PatrolProgress(PatrolSpread.place(route.size(), walker(), mates)));
  }

  private PatrolSpread.Walker walker() {
    return new PatrolSpread.Walker(progress.index(), progressSince, robot.getId());
  }

  /** After a fight, it goes on from the point nearest to where it is (answer 1625). */
  void backToNearest() {
    PatrolRoute route = walkable();
    walkTo(new PatrolProgress(route.nearestTo(robot.getX(), robot.getY() - 1, robot.getZ())));
  }

  /** Walking the edges of its area, without points of a route (answer 1616). */
  boolean onEdges() {
    return hasCard() && robot.workArea().working() && walkable().isEmpty();
  }

  /**
   * The corner of the edges it walks to, spread from the mates on the same edges as on a route
   * (answers 1616 and 1708).
   */
  int spreadCorner(int corner) {
    Optional<WorkArea> area = robot.workArea().here();
    if (area.isEmpty()) {
      return corner;
    }
    int corners = area.get().cornerCount();
    edgeTo(Math.floorMod(corner, corners));
    List<PatrolSpread.Walker> mates =
        PatrolTeams.mates(robot).stream()
            .filter(mate -> mate.workArea().here().equals(area))
            .map(mate -> mate.workArea().work().patrol())
            .filter(RobotPatrol::onEdges)
            .map(RobotPatrol::edgeWalker)
            .toList();
    edgeTo(PatrolSpread.place(corners, edgeWalker(), mates));
    return edgeCorner;
  }

  private void edgeTo(int corner) {
    if (corner != edgeCorner) {
      edgeSince = robot.level().getGameTime();
    }
    edgeCorner = corner;
  }

  private PatrolSpread.Walker edgeWalker() {
    return new PatrolSpread.Walker(edgeCorner, edgeSince, robot.getId());
  }

  /** A stop where it is, at a corner of the edges, drawn with the next leg (answers 1616, 1628). */
  void stopHere() {
    stops = stops.stop(drawStopTicks(), drawLeg());
  }

  /** The blocks walked count toward the end of the leg (answers 1616, 1617 and 1628). */
  void walked(double blocks) {
    if (!stops.hasLeg()) {
      stops = stops.withLeg(drawLeg());
    }
    stops = stops.walked(blocks);
    if (stops.due()) {
      stopHere();
    }
  }

  /** The draw stays out of the domain, which keeps it within the limits (answer 1628). */
  private int drawStopTicks() {
    PatrolSettings.Round round = round();
    return round.stopTicks(
        robot.getRandom().nextIntBetweenInclusive(round.stopMinTicks(), round.stopMaxTicks()));
  }

  private int drawLeg() {
    PatrolSettings.Round round = round();
    return round.stopDistance(
        robot
            .getRandom()
            .nextIntBetweenInclusive(round.stopMinDistance(), round.stopMaxDistance()));
  }

  void waitOut() {
    stops = stops.tick();
  }

  /** The share of the normal speed of the round; a chase keeps the normal one (answer 1614). */
  double speed() {
    return round().speed();
  }

  private static PatrolSettings.Round round() {
    return CubitMod.CONFIG.patrol().settings().round();
  }

  void save(CompoundTag tag) {
    if (route.isEmpty()) {
      return;
    }
    ListTag saved = new ListTag();
    for (RoutePoint point : route.points()) {
      CompoundTag entry = new CompoundTag();
      entry.putString(DIMENSION_KEY, point.dimension());
      entry.putInt(X_KEY, point.x());
      entry.putInt(Y_KEY, point.y());
      entry.putInt(Z_KEY, point.z());
      saved.add(entry);
    }
    tag.put(KEY, saved);
  }

  /** A point without its dimension is left out and goes to the log (rule 17). */
  void load(CompoundTag tag) {
    List<RoutePoint> points = new ArrayList<>();
    for (Tag saved : tag.getList(KEY, Tag.TAG_COMPOUND)) {
      CompoundTag entry = (CompoundTag) saved;
      if (entry.getString(DIMENSION_KEY).isEmpty()) {
        LOGGER.warn(INVALID_POINT, robot.getUUID(), entry);
        continue;
      }
      points.add(
          new RoutePoint(
              entry.getString(DIMENSION_KEY),
              entry.getInt(X_KEY),
              entry.getInt(Y_KEY),
              entry.getInt(Z_KEY)));
    }
    route = new PatrolRoute(points);
    progress = PatrolProgress.START;
    stops = PatrolStops.START;
  }
}
