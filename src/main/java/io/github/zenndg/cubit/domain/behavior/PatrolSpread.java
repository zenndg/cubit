package io.github.zenndg.cubit.domain.behavior;

import java.util.List;

/**
 * The round of several Patrollers on the same route, or the same edges: each one walks to a
 * different point of the loop. When two head to the same point, the one that came later goes to the
 * point farthest from its mates, and the first keeps its own (answers 1646, 1708 and 1719).
 */
public final class PatrolSpread {
  /**
   * A Patroller on the loop: the point it walks to, the tick it started to and its id, which breaks
   * a tie so one of two equal ones moves.
   */
  public record Walker(int index, long since, int id) {
    boolean cameBefore(Walker other) {
      return since < other.since || since == other.since && id < other.id;
    }
  }

  private PatrolSpread() {}

  /** The point it walks to, among the points of the loop. */
  public static int place(int size, Walker self, List<Walker> mates) {
    if (size < 2) {
      return self.index();
    }
    int mine = Math.floorMod(self.index(), size);
    boolean crowded =
        mates.stream()
            .anyMatch(mate -> Math.floorMod(mate.index(), size) == mine && mate.cameBefore(self));
    return crowded ? farthest(size, mates) : self.index();
  }

  /** The point whose nearest mate, along the loop, is the farthest; the first one on a tie. */
  private static int farthest(int size, List<Walker> mates) {
    int best = 0;
    int bestGap = -1;
    for (int point = 0; point < size; point++) {
      int gap = nearestMate(size, point, mates);
      if (gap > bestGap) {
        best = point;
        bestGap = gap;
      }
    }
    return best;
  }

  private static int nearestMate(int size, int point, List<Walker> mates) {
    int nearest = size;
    for (Walker mate : mates) {
      int apart = Math.abs(point - Math.floorMod(mate.index(), size));
      nearest = Math.min(nearest, Math.min(apart, size - apart));
    }
    return nearest;
  }
}
