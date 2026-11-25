package io.github.zenndg.cubit.domain.robot;

/**
 * Which way is out of a shape from a place on the ground: across the nearest edge, or the diagonal
 * near a corner, also the inner one of an L, where the Patroller faces at a stop of its edges
 * (answers 1626 and 1724).
 *
 * @param x on the X axis: -1, 0 or 1
 * @param z on the Z axis: -1, 0 or 1
 */
public record AreaOutward(int x, int z) {
  /** How near both edges a place counts as at a corner, for the way out (answer 1626). */
  private static final int CORNER_REACH = 2;

  private static final int[] WAYS = {-1, 1};

  static AreaOutward from(AreaShape shape, double x, double z) {
    int blockX = (int) Math.floor(x);
    int blockZ = (int) Math.floor(z);
    if (!shape.has(blockX, blockZ)) {
      return awayFrom(shape, x, z);
    }
    double west = x - edge(shape, blockX, blockZ, new AreaOutward(-1, 0));
    double east = edge(shape, blockX, blockZ, new AreaOutward(1, 0)) + 1 - x;
    double north = z - edge(shape, blockX, blockZ, new AreaOutward(0, -1));
    double south = edge(shape, blockX, blockZ, new AreaOutward(0, 1)) + 1 - z;
    int sideX = west <= east ? -1 : 1;
    int sideZ = north <= south ? -1 : 1;
    double nearX = Math.min(west, east);
    double nearZ = Math.min(north, south);
    if (nearX <= CORNER_REACH && nearZ <= CORNER_REACH) {
      return new AreaOutward(sideX, sideZ);
    }
    if (nearX > CORNER_REACH && nearZ > CORNER_REACH) {
      AreaOutward notch = notch(shape, blockX, blockZ);
      if (notch != null) {
        return notch;
      }
    }
    return nearX <= nearZ ? new AreaOutward(sideX, 0) : new AreaOutward(0, sideZ);
  }

  /** The last block inside, going straight one way. */
  private static int edge(AreaShape shape, int x, int z, AreaOutward way) {
    int atX = x;
    int atZ = z;
    while (shape.has(atX + way.x, atZ + way.z)) {
      atX += way.x;
      atZ += way.z;
    }
    return way.x != 0 ? atX : atZ;
  }

  /** The inner corner of an L: only the diagonal leads out, or null when there is none near. */
  private static AreaOutward notch(AreaShape shape, int x, int z) {
    for (int wayX : WAYS) {
      for (int wayZ : WAYS) {
        if (!shape.has(x + wayX * CORNER_REACH, z + wayZ * CORNER_REACH)) {
          return new AreaOutward(wayX, wayZ);
        }
      }
    }
    return null;
  }

  /** Already outside, the way goes on from the heart of the shape. */
  private static AreaOutward awayFrom(AreaShape shape, double x, double z) {
    double dx = x - shape.heart().x();
    double dz = z - shape.heart().z();
    return Math.abs(dx) >= Math.abs(dz)
        ? new AreaOutward(dx < 0 ? -1 : 1, 0)
        : new AreaOutward(0, dz < 0 ? -1 : 1);
  }
}
