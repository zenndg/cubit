package io.github.zenndg.cubit.domain.dock;

/**
 * What the panel of a Dock sees: whether it is day, whether the sky above it is open, whether it
 * rains there, whether the world has a sun at all, which the Nether and the End do not, whether the
 * Dock has its third block, where the panel goes, and whether its tier gives at night (answers
 * 1384, 1897 and 1943).
 */
public record DockSun(
    boolean day,
    boolean skyOpen,
    boolean raining,
    boolean naturalSky,
    boolean room,
    boolean givesAtNight) {
  /**
   * Why the panel stops, the most lasting reason first: a world without a sun, then a Dock without
   * room for the panel, then a block over the panel, then the night and then the rain, which only
   * the player can change or wait for (answers 1396 and 1897). At a tier that gives at night, the
   * night is no reason to stop (answer 1943).
   */
  public DockSunState state() {
    if (!naturalSky) {
      return DockSunState.NO_SUN;
    }
    if (!room) {
      return DockSunState.NO_ROOM;
    }
    if (!skyOpen) {
      return DockSunState.COVERED;
    }
    if (!day && !givesAtNight) {
      return DockSunState.NIGHT;
    }
    if (raining) {
      return DockSunState.RAIN;
    }
    return day ? DockSunState.GENERATING : DockSunState.MOONLIGHT;
  }
}
