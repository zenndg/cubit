package io.github.zenndg.cubit.entity;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolProgress;
import io.github.zenndg.cubit.domain.robot.PatrolRoute;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.domain.robot.RoutePoint;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

  RobotPatrol(RobotEntity robot) {
    this.robot = robot;
  }

  public PatrolRoute route() {
    return route;
  }

  void setRoute(PatrolRoute next) {
    route = next;
    progress = progress.within(walkable().size());
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

  Optional<BlockPos> currentPoint() {
    PatrolRoute walkable = walkable();
    progress = progress.within(walkable.size());
    return walkable
        .at(progress.index())
        .map(point -> new BlockPos(point.x(), point.y(), point.z()));
  }

  boolean pausing() {
    return progress.pausing();
  }

  void arrived() {
    progress = progress.arrived(CubitMod.CONFIG.patrolSettings().pointPauseTicks());
  }

  /** One tick of the pause, or the next point once it ends. */
  void waitOrGoOn() {
    progress = progress.pausing() ? progress.tick() : progress.next(walkable().size());
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
  }
}
