package io.github.zenndg.cubit.domain.network;

/**
 * The two angles of the open panel of the Solar Relay, in degrees: how far it lies back and how far
 * it leans to its side, to follow the sun without leaving the block. Facing north or south it leans
 * forward and turns to its side, and facing east or west it lies almost flat and goes a little past
 * it and back. By the moon it lies flat (answers 2384 and 2385).
 */
public record PanelAim(double tilt, double roll) {
  private static final double FOLLOWS = 11;
  private static final double FLAT = 90;
  private static final double NOON_TILT = 55;
  private static final double LOW_SUN_TILT = 32;
  private static final long DAY = 24_000;
  private static final long DUSK = 12_000;

  /** Where the front of the block looks. */
  public enum Side {
    NORTH,
    SOUTH,
    EAST,
    WEST
  }

  public static PanelAim of(Side side, long dayTime) {
    if (Math.floorMod(dayTime, DAY) > DUSK) {
      return new PanelAim(FLAT, 0);
    }
    double sun = SunTrack.at(dayTime);
    return switch (side) {
      case NORTH -> new PanelAim(leaning(sun), -FOLLOWS * sun);
      case SOUTH -> new PanelAim(leaning(sun), FOLLOWS * sun);
      case WEST -> new PanelAim(FLAT - FOLLOWS * sun, 0);
      case EAST -> new PanelAim(FLAT + FOLLOWS * sun, 0);
    };
  }

  private static double leaning(double sun) {
    return NOON_TILT - (NOON_TILT - LOW_SUN_TILT) * Math.abs(sun);
  }
}
