package io.github.zenndg.cubit.domain.robot;

/**
 * A straight line of blocks between two columns, along X or Z only (answer 2243). Its ends are part
 * of it.
 */
public record AreaSide(AreaColumn from, AreaColumn to) {
  private static final String CROOKED_MESSAGE = "A side of an area is not straight: ";

  public AreaSide {
    if (!straight(from, to)) {
      throw new IllegalArgumentException(CROOKED_MESSAGE + from + to);
    }
  }

  public static boolean straight(AreaColumn from, AreaColumn to) {
    return from.x() == to.x() || from.z() == to.z();
  }

  public int minX() {
    return Math.min(from.x(), to.x());
  }

  public int maxX() {
    return Math.max(from.x(), to.x());
  }

  public int minZ() {
    return Math.min(from.z(), to.z());
  }

  public int maxZ() {
    return Math.max(from.z(), to.z());
  }

  public boolean holds(AreaColumn column) {
    return column.x() >= minX()
        && column.x() <= maxX()
        && column.z() >= minZ()
        && column.z() <= maxZ();
  }

  /** The two lines share a block. */
  public boolean meets(AreaSide other) {
    return apart(other) == 0;
  }

  /** The two lines share a block or have blocks side by side, corners included. */
  public boolean touches(AreaSide other) {
    return apart(other) <= 1;
  }

  /** The line without its first block, or nothing when it is a single one. */
  AreaColumn second() {
    return new AreaColumn(
        from.x() + Integer.signum(to.x() - from.x()), from.z() + Integer.signum(to.z() - from.z()));
  }

  /** How far a place on the ground is from the blocks of the line, with 0 on them. */
  public double distance(double x, double z) {
    double dx = Math.max(0, Math.max(minX() - x, x - (maxX() + 1)));
    double dz = Math.max(0, Math.max(minZ() - z, z - (maxZ() + 1)));
    return Math.hypot(dx, dz);
  }

  private int apart(AreaSide other) {
    int dx = Math.max(other.minX() - maxX(), minX() - other.maxX());
    int dz = Math.max(other.minZ() - maxZ(), minZ() - other.maxZ());
    return Math.max(0, Math.max(dx, dz));
  }
}
