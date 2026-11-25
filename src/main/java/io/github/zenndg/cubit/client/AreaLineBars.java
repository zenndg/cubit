package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.robot.AreaCorner;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Where the bars of the line of an area go: 3 pixels of a block wide, on top of the middle of the
 * marked blocks, with a cube of 6 pixels on each point (answers 2358 and 2359).
 */
final class AreaLineBars {
  private static final double PIXEL = 1.0 / 16;
  private static final double BAR_HALF = 1.5 * PIXEL;
  private static final double POST_HALF = PIXEL;
  private static final double CUBE_HALF = 3 * PIXEL;
  private static final double AIM_HEIGHT = 0.25 * PIXEL;
  private static final double HALF = 0.5;

  private AreaLineBars() {}

  /** The line of an open drawing, at the height of each point, rising to the next one. */
  static List<AABB> path(List<AreaCorner> points) {
    List<AABB> bars = new ArrayList<>();
    for (int index = 1; index < points.size(); index++) {
      bars.addAll(step(points.get(index - 1), points.get(index)));
    }
    return bars;
  }

  /** One line of the drawing. */
  static List<AABB> step(AreaCorner from, AreaCorner to) {
    Vec3 start = middleOf(from);
    Vec3 end = middleOf(to);
    Vec3 under = new Vec3(end.x, start.y, end.z);
    List<AABB> bars = new ArrayList<>();
    bars.add(bar(start, under, BAR_HALF));
    if (from.y() != to.y()) {
      bars.add(bar(under, end, BAR_HALF));
    }
    return bars;
  }

  /** The contour at the lowest blocks and at the highest, with posts between them. */
  static List<AABB> volume(List<AreaCorner> corners, int low, int high) {
    List<AABB> bars = new ArrayList<>(contour(corners, low));
    if (low == high) {
      return bars;
    }
    bars.addAll(contour(corners, high));
    for (AreaCorner corner : corners) {
      bars.add(bar(middleOf(at(corner, low)), middleOf(at(corner, high)), POST_HALF));
    }
    return bars;
  }

  static List<AABB> cubes(List<AreaCorner> points) {
    return points.stream()
        .map(point -> AABB.ofSize(middleOf(point), 2 * CUBE_HALF, 2 * CUBE_HALF, 2 * CUBE_HALF))
        .toList();
  }

  /** A thin plate on the top of the block in the aim. */
  static AABB plate(AreaCorner aimed) {
    return new AABB(
        aimed.x(),
        aimed.y() + 1,
        aimed.z(),
        aimed.x() + 1,
        aimed.y() + 1 + AIM_HEIGHT,
        aimed.z() + 1);
  }

  private static List<AABB> contour(List<AreaCorner> corners, int level) {
    List<AABB> bars = new ArrayList<>();
    for (int index = 0; index < corners.size(); index++) {
      AreaCorner next = corners.get((index + 1) % corners.size());
      bars.add(bar(middleOf(at(corners.get(index), level)), middleOf(at(next, level)), BAR_HALF));
    }
    return bars;
  }

  private static AreaCorner at(AreaCorner corner, int level) {
    return new AreaCorner(corner.x(), level, corner.z());
  }

  private static AABB bar(Vec3 from, Vec3 to, double half) {
    return new AABB(from, to).inflate(half);
  }

  private static Vec3 middleOf(AreaCorner point) {
    return new Vec3(point.x() + HALF, point.y() + 1 + BAR_HALF, point.z() + HALF);
  }
}
