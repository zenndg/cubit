package io.github.zenndg.cubit.domain.robot;

import io.github.zenndg.cubit.domain.tier.AreaSpec;

/**
 * The area of a Cubit: a shape drawn on the ground, with whole blocks from its lowest point to its
 * highest, in one dimension (answers 2240 and 2244).
 *
 * @param low the height of the lowest marked block
 * @param high the height of the highest marked block
 */
public record WorkArea(AreaShape shape, int low, int high, String dimension) {
  private static final String INVERTED_MESSAGE = "The heights of an area are inverted";
  private static final String NO_DIMENSION_MESSAGE = "An area needs a dimension";

  /** A place on the ground, whatever the height. */
  public record Place(double x, double z) {
    public double distance(double otherX, double otherZ) {
      return Math.hypot(otherX - x, otherZ - z);
    }
  }

  public WorkArea {
    if (low > high) {
      throw new IllegalArgumentException(INVERTED_MESSAGE);
    }
    if (dimension.isBlank()) {
      throw new IllegalArgumentException(NO_DIMENSION_MESSAGE);
    }
  }

  /** The box around the area. */
  public AreaBounds bounds() {
    return new AreaBounds(
        new AreaCorner(shape.min().x(), low, shape.min().z()),
        new AreaCorner(shape.max().x(), high, shape.max().z()));
  }

  public int height() {
    return high - low + 1;
  }

  /** Within the base and the height of the tier (answers 342, 1050 and 2350). */
  public boolean fits(AreaSpec spec) {
    return spec.unlocked()
        && shape.width() <= spec.sizeBlocks()
        && shape.depth() <= spec.sizeBlocks()
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
        && low <= other.high + 1
        && other.low <= high + 1
        && shape.touches(other.shape);
  }

  /** Which way is out of the area from a place on the ground (answer 1626). */
  public AreaOutward outwardFrom(double x, double z) {
    return AreaOutward.from(shape, x, z);
  }

  /** Both places are in columns of the area, whatever the height. */
  public boolean holds(Place from, Place to) {
    return has(from) && has(to);
  }

  private boolean has(Place place) {
    return shape.has((int) Math.floor(place.x()), (int) Math.floor(place.z()));
  }

  /** Where to go first from a place to another, around an inner corner when needed (2365). */
  public Place next(Place from, Place to) {
    return holds(from, to) ? shape.next(from, to) : to;
  }

  /** How long the way to a place is, so going around a corner counts as progress (2365). */
  public double way(Place from, Place to) {
    return holds(from, to) ? shape.way(from, to) : from.distance(to.x(), to.z());
  }

  /** The corner of the area nearest to a place on the ground (answer 1625). */
  public int nearestCorner(double x, double z) {
    return shape.nearestCorner(x, z);
  }

  /** How far a point is from the base of the area, on the ground, with 0 inside (answer 1636). */
  public double distanceToBase(double x, double z) {
    return shape.distance(x, z);
  }

  /**
   * A point inside the area, with one block over its top, where the items lie and the Cubit stands
   * on the marked blocks: points on the ground make a flat area that still holds them.
   */
  public boolean contains(double x, double y, double z) {
    return y >= low && y < top() && shape.has((int) Math.floor(x), (int) Math.floor(z));
  }

  /** Where the area ends upwards: the block over its top, which also holds the items. */
  public int top() {
    return high + 2;
  }

  /** Where the Cubit heads to when it is outside: the middle of the area, always inside it. */
  public Place heart() {
    return shape.heart();
  }

  /** How many columns of blocks the area has, numbered from zero for the scans. */
  public int columnCount() {
    return shape.columnCount();
  }

  public AreaColumn column(int index) {
    return shape.column(index);
  }

  /** The center of the box around it within a radius of a point (answers 1058 and 2351). */
  public boolean centerWithin(double x, double z, int radius) {
    return bounds().center().distance(x, z) <= radius;
  }

  public int cornerCount() {
    return shape.corners().size();
  }

  /**
   * The corners of the area, in order around it, for the round along the edges (answer 1062). The
   * height of the round comes from where the Cubit walks, inside the volume. Any index goes around.
   */
  public AreaColumn corner(int index) {
    return shape.corners().get(Math.floorMod(index, cornerCount()));
  }
}
