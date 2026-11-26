package io.github.zenndg.cubit.domain.energy;

/** What a solar panel is doing, and why it stops, for the Solar tab (answer 1396). */
public enum SunState {
  GENERATING,
  NIGHT,
  RAIN,
  COVERED,
  NO_SUN,
  /** A Dock from before the third block, with the space above it taken (answer 1897). */
  NO_ROOM,
  /** A clear night, at a tier that generates at night (answers 1943 and 1946). */
  MOONLIGHT;

  public boolean generates() {
    return this == GENERATING || this == MOONLIGHT;
  }
}
