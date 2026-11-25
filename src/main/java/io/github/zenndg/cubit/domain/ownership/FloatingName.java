package io.github.zenndg.cubit.domain.ownership;

/**
 * The name over a block of the player, as a Dock or a Network Server: half a block above its top,
 * over its middle, for its owner with the Tablet in a hand, up to 32 blocks away, and never with a
 * screen open or the interface hidden (answers 2469 and 2512 to 2517).
 */
public record FloatingName(double x, double y, double z) {
  public static final int REACH = 32;
  public static final double ABOVE_TOP = 0.5;
  public static final int LOOK_INTERVAL_TICKS = 10;
  private static final double MIDDLE = 0.5;
  private static final String NO_HEIGHT = "A block with a name is at least one block tall: ";

  /** The name over a block by its lower corner and how many blocks tall it is. */
  public static FloatingName over(int x, int y, int z, int blocksTall) {
    if (blocksTall < 1) {
      throw new IllegalArgumentException(NO_HEIGHT + blocksTall);
    }
    return new FloatingName(x + MIDDLE, y + blocksTall + ABOVE_TOP, z + MIDDLE);
  }

  /** Whether the client looks for the blocks around now, and not on every frame (rule 21). */
  public static boolean looksFor(long tick) {
    return tick % LOOK_INTERVAL_TICKS == 0;
  }

  public boolean seenBy(Viewer viewer) {
    return viewer.owner()
        && viewer.tablet()
        && !viewer.screenOpen()
        && !viewer.interfaceHidden()
        && distanceTo(viewer) <= REACH;
  }

  private double distanceTo(Viewer viewer) {
    double dx = viewer.x() - x;
    double dy = viewer.y() - y;
    double dz = viewer.z() - z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /** Who looks: whether they own the block, hold the Tablet, have a screen open or hid the HUD. */
  public record Viewer(
      boolean owner,
      boolean tablet,
      boolean screenOpen,
      boolean interfaceHidden,
      double x,
      double y,
      double z) {}
}
