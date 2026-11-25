package io.github.zenndg.cubit.domain.robot;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

/**
 * The closed shape of an area on the ground, drawn in right angles (answers 2240 and 2243). Its
 * columns are read once, when it is made, so asking if a block is inside costs nothing later (rule
 * 21). The blocks of the points and of the lines are part of it (answer 2311).
 */
public final class AreaShape {
  /** The largest side a shape can have, so a broken save never takes the memory (rule 17). */
  public static final int MAX_SIDE = 256;

  /** The most points a shape can have, for the same reason. */
  public static final int MAX_POINTS = 256;

  private static final double HALF = 0.5;

  private final List<AreaColumn> corners;
  private final List<AreaSide> sides;
  private final AreaColumn min;
  private final AreaColumn max;
  private final BitSet inside;
  private final int[] columns;
  private final WorkArea.Place heart;
  private final AreaWays ways;

  private AreaShape(List<AreaColumn> corners, AreaColumn min, AreaColumn max) {
    this.corners = corners;
    this.sides = sidesOf(corners);
    this.min = min;
    this.max = max;
    this.inside = read();
    this.columns = inside.stream().toArray();
    this.heart = findHeart();
    this.ways = new AreaWays(this);
  }

  /** The shape of a path that closes, or nothing when it does not. */
  public static Optional<AreaShape> of(AreaPath path) {
    if (path.points().isEmpty() || path.points().size() > MAX_POINTS) {
      return Optional.empty();
    }
    AreaColumn min = path.points().stream().reduce(AreaShape::lower).orElseThrow();
    AreaColumn max = path.points().stream().reduce(AreaShape::higher).orElseThrow();
    boolean small = (long) max.x() - min.x() < MAX_SIDE && (long) max.z() - min.z() < MAX_SIDE;
    return small && path.closes()
        ? Optional.of(new AreaShape(AreaLoop.cornersOf(path), min, max))
        : Optional.empty();
  }

  /** Where the edge turns, in order around the shape. */
  public List<AreaColumn> corners() {
    return corners;
  }

  public List<AreaSide> sides() {
    return sides;
  }

  public AreaColumn min() {
    return min;
  }

  public AreaColumn max() {
    return max;
  }

  public int width() {
    return max.x() - min.x() + 1;
  }

  public int depth() {
    return max.z() - min.z() + 1;
  }

  public boolean has(int x, int z) {
    return x >= min.x()
        && x <= max.x()
        && z >= min.z()
        && z <= max.z()
        && inside.get(indexOf(x, z));
  }

  public int columnCount() {
    return columns.length;
  }

  public AreaColumn column(int index) {
    return columnAt(columns[index]);
  }

  /** The middle of the shape, or the place inside it nearest to the middle. */
  public WorkArea.Place heart() {
    return heart;
  }

  /** Where to go first from a place inside to another, without leaving the shape (2365). */
  public WorkArea.Place next(WorkArea.Place from, WorkArea.Place to) {
    return ways.between(from, to).next();
  }

  /** How long the way from a place inside to another is, without leaving the shape (2365). */
  public double way(WorkArea.Place from, WorkArea.Place to) {
    return ways.between(from, to).length();
  }

  /** How far a place on the ground is from the shape, with 0 inside (answer 1636). */
  public double distance(double x, double z) {
    if (has((int) Math.floor(x), (int) Math.floor(z))) {
      return 0;
    }
    return sides.stream().mapToDouble(side -> side.distance(x, z)).min().orElseThrow();
  }

  /** The corner nearest to a place on the ground (answer 1625). */
  public int nearestCorner(double x, double z) {
    int nearest = 0;
    double best = Double.MAX_VALUE;
    for (int index = 0; index < corners.size(); index++) {
      double distance = Math.hypot(corners.get(index).x() - x, corners.get(index).z() - z);
      if (distance < best) {
        best = distance;
        nearest = index;
      }
    }
    return nearest;
  }

  /** The two shapes share a block or have blocks side by side (answer 1707). */
  public boolean touches(AreaShape other) {
    AreaColumn mine = corners.getFirst();
    AreaColumn theirs = other.corners.getFirst();
    return has(theirs.x(), theirs.z())
        || other.has(mine.x(), mine.z())
        || sides.stream().anyMatch(side -> other.sides.stream().anyMatch(side::touches));
  }

  private static List<AreaSide> sidesOf(List<AreaColumn> corners) {
    List<AreaSide> sides = new ArrayList<>();
    for (int index = 0; index < corners.size(); index++) {
      sides.add(new AreaSide(corners.get(index), corners.get((index + 1) % corners.size())));
    }
    return List.copyOf(sides);
  }

  private BitSet read() {
    BitSet found = new BitSet(width() * depth());
    for (int index = 0; index < width() * depth(); index++) {
      AreaColumn column = columnAt(index);
      found.set(index, onEdge(column) || withinEdges(column));
    }
    return found;
  }

  private boolean onEdge(AreaColumn column) {
    return sides.stream().anyMatch(side -> side.holds(column));
  }

  /** A line from the column to the east crosses the edges an odd number of times. */
  private boolean withinEdges(AreaColumn column) {
    long crossed =
        sides.stream()
            .filter(side -> side.minX() > column.x())
            .filter(side -> side.minZ() <= column.z() && column.z() < side.maxZ())
            .count();
    return crossed % 2 == 1;
  }

  private WorkArea.Place findHeart() {
    double x = (min.x() + max.x() + 1) * HALF;
    double z = (min.z() + max.z() + 1) * HALF;
    if (has((int) Math.floor(x), (int) Math.floor(z))) {
      return new WorkArea.Place(x, z);
    }
    WorkArea.Place nearest = AreaWays.middleOf(column(0));
    for (int index = 1; index < columnCount(); index++) {
      WorkArea.Place place = AreaWays.middleOf(column(index));
      if (place.distance(x, z) < nearest.distance(x, z)) {
        nearest = place;
      }
    }
    return nearest;
  }

  private int indexOf(int x, int z) {
    return (z - min.z()) * width() + x - min.x();
  }

  private AreaColumn columnAt(int index) {
    return new AreaColumn(min.x() + index % width(), min.z() + index / width());
  }

  private static AreaColumn lower(AreaColumn one, AreaColumn other) {
    return new AreaColumn(Math.min(one.x(), other.x()), Math.min(one.z(), other.z()));
  }

  private static AreaColumn higher(AreaColumn one, AreaColumn other) {
    return new AreaColumn(Math.max(one.x(), other.x()), Math.max(one.z(), other.z()));
  }

  /** Two shapes are the same by their corners, which come in one form. */
  @Override
  public boolean equals(Object other) {
    return other instanceof AreaShape shape && corners.equals(shape.corners);
  }

  @Override
  public int hashCode() {
    return corners.hashCode();
  }
}
