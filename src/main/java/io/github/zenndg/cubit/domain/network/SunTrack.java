package io.github.zenndg.cubit.domain.network;

/**
 * Where the panel of the Solar Relay points, from -1 at dawn, to the east, to 1 at dusk, to the
 * west, and 0 at noon and by the moon, when it lies flat (answers 2338 and 2385).
 */
public final class SunTrack {
  private static final long DAY = 24_000;
  private static final long DUSK = 12_000;
  private static final double NOON = 6_000;

  private SunTrack() {}

  public static double at(long dayTime) {
    long time = Math.floorMod(dayTime, DAY);
    return time > DUSK ? 0 : (time - NOON) / NOON;
  }
}
