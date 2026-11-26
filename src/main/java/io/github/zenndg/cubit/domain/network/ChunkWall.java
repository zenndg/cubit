package io.github.zenndg.cubit.domain.network;

/**
 * The wall around the chunk of a Network Server or of an antenna, which its owner sees while its
 * `View` key is on, with anything or nothing in hand (answer 2622): from the support block up to 16
 * blocks above it, around the 16 blocks of the chunk (answer 2448).
 */
public record ChunkWall(int minX, int minZ, int bottom, int top) {
  public static final int SIDE = 16;
  public static final int HEIGHT = 16;
  public static final int REACH = 128;
  private static final String NO_HEIGHT = "A wall goes up, from %d to %d";

  public ChunkWall {
    if (top <= bottom) {
      throw new IllegalArgumentException(NO_HEIGHT.formatted(bottom, top));
    }
  }

  /** The wall of a server by its block, which stands on the support block. */
  public static ChunkWall around(int x, int y, int z) {
    int support = y - 1;
    return new ChunkWall(
        Math.floorDiv(x, SIDE) * SIDE,
        Math.floorDiv(z, SIDE) * SIDE,
        support,
        support + 1 + HEIGHT);
  }

  public int maxX() {
    return minX + SIDE;
  }

  public int maxZ() {
    return minZ + SIDE;
  }

  /** Whether a viewer sees it: the owner, within reach of the wall (answer 2623). */
  public boolean seenBy(Viewer viewer) {
    return viewer.owner() && distanceTo(viewer) <= REACH;
  }

  /** To the nearest point of the wall, or zero within the chunk and the height. */
  private double distanceTo(Viewer viewer) {
    double x = outside(viewer.x(), minX, maxX());
    double y = outside(viewer.y(), bottom, top);
    double z = outside(viewer.z(), minZ, maxZ());
    return Math.sqrt(x * x + y * y + z * z);
  }

  private static double outside(double value, double low, double high) {
    return Math.max(0, Math.max(low - value, value - high));
  }

  /** Who looks: whether they own the block, and from where. */
  public record Viewer(boolean owner, double x, double y, double z) {}
}
