package io.github.zenndg.cubit.domain.robot;

import io.github.zenndg.cubit.domain.tier.AreaSpec;

/**
 * The area of a Cubit: a box of whole blocks between two corners, in one dimension (answers 342 and
 * 1051). The base is the side the limits of the tier count, and the height comes from the corners.
 */
public record WorkArea(AreaCorner min, AreaCorner max, String dimension) {
  /** The corners of the base, which the round walks in a loop. */
  private static final int CORNERS = 4;

  /** How near both edges a place counts as at a corner, for the way out (answer 1626). */
  private static final double CORNER_REACH = 2;

  private static final double HALF = 0.5;
  private static final String INVERTED_MESSAGE = "The corners of an area are inverted";
  private static final String NO_DIMENSION_MESSAGE = "An area needs a dimension";

  public WorkArea {
    if (min.x() > max.x() || min.y() > max.y() || min.z() > max.z()) {
      throw new IllegalArgumentException(INVERTED_MESSAGE);
    }
    if (dimension.isBlank()) {
      throw new IllegalArgumentException(NO_DIMENSION_MESSAGE);
    }
  }

  /** The two clicked corners, in any order. */
  public static WorkArea between(AreaCorner first, AreaCorner second, String dimension) {
    return new WorkArea(
        new AreaCorner(
            Math.min(first.x(), second.x()),
            Math.min(first.y(), second.y()),
            Math.min(first.z(), second.z())),
        new AreaCorner(
            Math.max(first.x(), second.x()),
            Math.max(first.y(), second.y()),
            Math.max(first.z(), second.z())),
        dimension);
  }

  public int width() {
    return max.x() - min.x() + 1;
  }

  public int height() {
    return max.y() - min.y() + 1;
  }

  public int depth() {
    return max.z() - min.z() + 1;
  }

  /** Within the base and the height of the tier (answers 342 and 1050). */
  public boolean fits(AreaSpec spec) {
    return spec.unlocked()
        && width() <= spec.sizeBlocks()
        && depth() <= spec.sizeBlocks()
        && height() <= spec.heightBlocks();
  }

  public boolean isIn(String level) {
    return dimension.equals(level);
  }

  /**
   * Another area in the same dimension that overlaps this one or touches it, so a base guarded by
   * several areas side by side counts as one (answer 1707).
   */
  public boolean touches(WorkArea other) {
    return dimension.equals(other.dimension)
        && min.x() <= other.max.x() + 1
        && other.min.x() <= max.x() + 1
        && min.y() <= other.max.y() + 1
        && other.min.y() <= max.y() + 1
        && min.z() <= other.max.z() + 1
        && other.min.z() <= max.z() + 1;
  }

  /** Which way is out of the area, on each axis of the ground: -1, 0 or 1. */
  public record Outward(int x, int z) {}

  /**
   * Which way is out of the area from a place on the ground: across the nearest edge, or the
   * diagonal near a corner, where the Patroller faces at a stop of its edges (answer 1626).
   */
  public Outward outwardFrom(double x, double z) {
    double west = x - min.x();
    double east = max.x() + 1 - x;
    double north = z - min.z();
    double south = max.z() + 1 - z;
    int sideX = west <= east ? -1 : 1;
    int sideZ = north <= south ? -1 : 1;
    double nearX = Math.min(west, east);
    double nearZ = Math.min(north, south);
    if (nearX <= CORNER_REACH && nearZ <= CORNER_REACH) {
      return new Outward(sideX, sideZ);
    }
    return nearX <= nearZ ? new Outward(sideX, 0) : new Outward(0, sideZ);
  }

  /** The corner of the base nearest to a place on the ground (answer 1625). */
  public int nearestCorner(double x, double z) {
    int nearest = 0;
    double best = Double.MAX_VALUE;
    for (int index = 0; index < CORNERS; index++) {
      AreaCorner corner = corner(index);
      double distance = Math.hypot(corner.x() - x, corner.z() - z);
      if (distance < best) {
        best = distance;
        nearest = index;
      }
    }
    return nearest;
  }

  /** How far a point is from the base of the area, on the ground, with 0 inside (answer 1636). */
  public double distanceToBase(double x, double z) {
    double dx = Math.max(0, Math.max(min.x() - x, x - (max.x() + 1)));
    double dz = Math.max(0, Math.max(min.z() - z, z - (max.z() + 1)));
    return Math.sqrt(dx * dx + dz * dz);
  }

  /**
   * A point inside the area, with one block over its top, where the items lie and the Cubit stands
   * on the marked blocks: two corners on the ground make a flat area that still holds them.
   */
  public boolean contains(double x, double y, double z) {
    return x >= min.x()
        && x < max.x() + 1
        && y >= min.y()
        && y < top()
        && z >= min.z()
        && z < max.z() + 1;
  }

  /** Where the area ends upwards: the block over its top, which also holds the items. */
  public int top() {
    return max.y() + 2;
  }

  /** A place on the ground, whatever the height. */
  public record Place(double x, double z) {}

  /** Where the Cubit heads to when it is outside: the middle of the area. */
  public Place heart() {
    return new Place((min.x() + max.x() + 1) * HALF, (min.z() + max.z() + 1) * HALF);
  }

  /** How many columns of blocks the area has, numbered from zero for the scans. */
  public int columnCount() {
    return width() * depth();
  }

  public AreaColumn column(int index) {
    return new AreaColumn(min.x() + index % width(), min.z() + index / width());
  }

  /** The center of the base within a radius of a point, as the reach of a tier (answer 1058). */
  public boolean centerWithin(double x, double z, int radius) {
    double dx = heart().x() - x;
    double dz = heart().z() - z;
    return dx * dx + dz * dz <= (double) radius * radius;
  }

  public int cornerCount() {
    return CORNERS;
  }

  /**
   * The corners of the base, in order around it, for the round along the edges (answer 1062). The
   * height of the round comes from where the Cubit walks, inside the volume. Any index goes around.
   */
  public AreaCorner corner(int index) {
    return switch (Math.floorMod(index, CORNERS)) {
      case 0 -> new AreaCorner(min.x(), min.y(), min.z());
      case 1 -> new AreaCorner(max.x(), min.y(), min.z());
      case 2 -> new AreaCorner(max.x(), min.y(), max.z());
      default -> new AreaCorner(min.x(), min.y(), max.z());
    };
  }
}
