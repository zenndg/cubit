package io.github.zenndg.cubit.domain.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The points of the patrol, in the order they were marked, walked in a loop. A point is never
 * marked twice, a full route refuses a new one, and removing a point keeps the order of the others
 * (answers 1561, 1579, 1582 and 1602).
 */
public record PatrolRoute(List<RoutePoint> points) {
  public static final PatrolRoute EMPTY = new PatrolRoute(List.of());

  public PatrolRoute {
    points = List.copyOf(points);
  }

  public boolean isEmpty() {
    return points.isEmpty();
  }

  public int size() {
    return points.size();
  }

  public boolean contains(RoutePoint point) {
    return points.contains(point);
  }

  /** The same route when the point is already in it or the tier keeps no more points. */
  public PatrolRoute add(RoutePoint point, int maxPoints) {
    if (contains(point) || points.size() >= maxPoints) {
      return this;
    }
    List<RoutePoint> next = new ArrayList<>(points);
    next.add(point);
    return new PatrolRoute(next);
  }

  public PatrolRoute remove(RoutePoint point) {
    List<RoutePoint> next = new ArrayList<>(points);
    next.remove(point);
    return new PatrolRoute(next);
  }

  /** Shift and a click anywhere else takes the last point out, as in the other marks. */
  public PatrolRoute removeLast() {
    return isEmpty() ? this : new PatrolRoute(points.subList(0, points.size() - 1));
  }

  public Optional<RoutePoint> at(int index) {
    return index >= 0 && index < points.size() ? Optional.of(points.get(index)) : Optional.empty();
  }

  /** The points in one dimension, the only ones a Cubit there walks to. */
  public PatrolRoute in(String dimension) {
    return new PatrolRoute(points.stream().filter(point -> point.in(dimension)).toList());
  }

  /**
   * The point nearest to a place, where it goes back to after a fight, going on with the order from
   * it (answer 1625). The first point on an empty route.
   */
  public int nearestTo(double x, double y, double z) {
    int nearest = 0;
    double best = Double.MAX_VALUE;
    for (int index = 0; index < points.size(); index++) {
      RoutePoint point = points.get(index);
      double dx = point.x() - x;
      double dy = point.y() - y;
      double dz = point.z() - z;
      double distance = dx * dx + dy * dy + dz * dz;
      if (distance < best) {
        best = distance;
        nearest = index;
      }
    }
    return nearest;
  }

  /** The point after this one, back to the first after the last. */
  public int nextIndex(int index) {
    return isEmpty() ? 0 : (index + 1) % points.size();
  }
}
