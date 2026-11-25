package io.github.zenndg.cubit.domain.robot;

import java.util.ArrayList;
import java.util.List;

/**
 * The points of an area in the order they were marked, joined by straight lines. It knows if one
 * more line is straight and stays clear of the lines before it, except for where they join (answers
 * 2243 and 2354). Lines side by side are fine.
 */
public record AreaPath(List<AreaColumn> points) {
  public static final AreaPath EMPTY = new AreaPath(List.of());

  public AreaPath {
    points = List.copyOf(points);
  }

  public AreaPath with(AreaColumn point) {
    List<AreaColumn> next = new ArrayList<>(points);
    next.add(point);
    return new AreaPath(next);
  }

  /** The line from the last point would be straight. */
  public boolean straightTo(AreaColumn point) {
    return AreaSide.straight(points.getLast(), point);
  }

  /** The line from the last point would pass over a block of the drawing. */
  public boolean crossesTo(AreaColumn point) {
    AreaSide line = new AreaSide(points.getLast(), point);
    AreaSide ahead = new AreaSide(line.second(), point);
    return points.getLast().equals(point) || meetsAny(ahead);
  }

  /** The line from the last point back to the first would pass over a block of the drawing. */
  public boolean crossesToClose() {
    AreaSide line = new AreaSide(points.getLast(), points.getFirst());
    AreaColumn ahead = line.second();
    if (ahead.equals(points.getFirst())) {
      return false;
    }
    AreaColumn behind = new AreaSide(points.getFirst(), points.getLast()).second();
    return meetsAny(new AreaSide(ahead, behind));
  }

  /** Straight and clear from its first point back to it, so it makes a closed shape. */
  public boolean closes() {
    return clear()
        && points.size() > 2
        && straightTo(points.getFirst())
        && !points.getLast().equals(points.getFirst())
        && !crossesToClose();
  }

  private boolean clear() {
    AreaPath walked = EMPTY;
    for (AreaColumn point : points) {
      boolean first = walked.points.isEmpty();
      if (!first && (!walked.straightTo(point) || walked.crossesTo(point))) {
        return false;
      }
      walked = walked.with(point);
    }
    return true;
  }

  /** The lines between the points, without the one that closes the shape. */
  public List<AreaSide> sides() {
    List<AreaSide> sides = new ArrayList<>();
    for (int index = 1; index < points.size(); index++) {
      sides.add(new AreaSide(points.get(index - 1), points.get(index)));
    }
    return sides;
  }

  private boolean meetsAny(AreaSide line) {
    if (points.size() == 1) {
      return line.holds(points.getFirst());
    }
    return sides().stream().anyMatch(line::meets);
  }
}
