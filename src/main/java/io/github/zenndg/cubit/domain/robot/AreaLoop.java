package io.github.zenndg.cubit.domain.robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The corners of a closed path in one form, whatever the point it started from and the way it went
 * around: the points halfway along a side are out, so two Cubits with the same shape have the same
 * corners, and the round stops only where the edge turns.
 */
final class AreaLoop {
  private static final Comparator<AreaColumn> FIRST =
      Comparator.comparingInt(AreaColumn::x).thenComparingInt(AreaColumn::z);

  private AreaLoop() {}

  static List<AreaColumn> cornersOf(AreaPath path) {
    List<AreaColumn> corners = turns(path.points());
    if (turning(corners) < 0) {
      Collections.reverse(corners);
    }
    Collections.rotate(corners, -corners.indexOf(Collections.min(corners, FIRST)));
    return List.copyOf(corners);
  }

  private static List<AreaColumn> turns(List<AreaColumn> points) {
    List<AreaColumn> corners = new ArrayList<>();
    int size = points.size();
    for (int index = 0; index < size; index++) {
      AreaColumn before = points.get(Math.floorMod(index - 1, size));
      AreaColumn point = points.get(index);
      AreaColumn after = points.get((index + 1) % size);
      boolean alongX = before.z() == point.z() && point.z() == after.z();
      boolean alongZ = before.x() == point.x() && point.x() == after.x();
      if (!alongX && !alongZ) {
        corners.add(point);
      }
    }
    return corners;
  }

  /** Twice the area inside the corners, negative when they go around the other way. */
  private static long turning(List<AreaColumn> corners) {
    long sum = 0;
    for (int index = 0; index < corners.size(); index++) {
      AreaColumn one = corners.get(index);
      AreaColumn next = corners.get((index + 1) % corners.size());
      sum += (long) one.x() * next.z() - (long) next.x() * one.z();
    }
    return sum;
  }
}
