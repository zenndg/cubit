package io.github.zenndg.cubit.domain.robot;

import java.util.ArrayList;
import java.util.List;

/**
 * The way between two places of a shape without leaving it: straight when it can, and around the
 * inner corners when it cannot, as in an L (answer 2365). The ways between the inner corners are
 * measured once, when the shape is made.
 */
final class AreaWays {
  private static final double HALF = 0.5;

  /** How far apart the blocks of a straight line are read. */
  private static final double STEP = 0.25;

  private final AreaShape shape;
  private final List<WorkArea.Place> turns = new ArrayList<>();
  private final double[][] lengths;

  AreaWays(AreaShape shape) {
    this.shape = shape;
    for (int index = 0; index < shape.corners().size(); index++) {
      if (inner(index)) {
        turns.add(middleOf(shape.corners().get(index)));
      }
    }
    this.lengths = measure();
  }

  /**
   * The way from a place to another, both inside, or straight when there is no other.
   *
   * @param next where to go first
   * @param length how long the whole way is
   */
  record Way(WorkArea.Place next, double length) {}

  Way between(WorkArea.Place from, WorkArea.Place to) {
    double straight = from.distance(to.x(), to.z());
    if (turns.isEmpty() || clear(from, to)) {
      return new Way(to, straight);
    }
    Way best = new Way(to, Double.MAX_VALUE);
    double[] ends = sights(to);
    for (int first = 0; first < turns.size(); first++) {
      WorkArea.Place turn = turns.get(first);
      double start = from.distance(turn.x(), turn.z());
      double rest = shortest(lengths[first], ends);
      if (start > 0 && start + rest < best.length() && clear(from, turn)) {
        best = new Way(turn, start + rest);
      }
    }
    return best.length() < Double.MAX_VALUE ? best : new Way(to, straight);
  }

  private static double shortest(double[] toEach, double[] ends) {
    double best = Double.MAX_VALUE;
    for (int last = 0; last < ends.length; last++) {
      best = Math.min(best, toEach[last] + ends[last]);
    }
    return best;
  }

  /** How far each inner corner is from a place, or infinity when it is out of sight. */
  private double[] sights(WorkArea.Place place) {
    return turns.stream()
        .mapToDouble(turn ->
            clear(turn, place) ? turn.distance(place.x(), place.z()) : Double.POSITIVE_INFINITY)
        .toArray();
  }

  private double[][] measure() {
    double[][] found = turns.stream().map(this::sights).toArray(double[][]::new);
    for (int through = 0; through < found.length; through++) {
      for (double[] from : found) {
        for (int to = 0; to < found.length; to++) {
          from[to] = Math.min(from[to], from[through] + found[through][to]);
        }
      }
    }
    return found;
  }

  /** A corner of the cut of an L: the corners turn one way around, and this one the other. */
  private boolean inner(int index) {
    List<AreaColumn> corners = shape.corners();
    AreaColumn before = corners.get(Math.floorMod(index - 1, corners.size()));
    AreaColumn corner = corners.get(index);
    AreaColumn after = corners.get((index + 1) % corners.size());
    long turn =
        (long) (corner.x() - before.x()) * (after.z() - corner.z())
            - (long) (corner.z() - before.z()) * (after.x() - corner.x());
    return turn < 0;
  }

  private boolean clear(WorkArea.Place from, WorkArea.Place to) {
    int steps = (int) Math.ceil(from.distance(to.x(), to.z()) / STEP);
    for (int step = 0; step <= steps; step++) {
      double share = steps == 0 ? 0 : (double) step / steps;
      int x = (int) Math.floor(from.x() + (to.x() - from.x()) * share);
      int z = (int) Math.floor(from.z() + (to.z() - from.z()) * share);
      if (!shape.has(x, z)) {
        return false;
      }
    }
    return true;
  }

  static WorkArea.Place middleOf(AreaColumn column) {
    return new WorkArea.Place(column.x() + HALF, column.z() + HALF);
  }
}
