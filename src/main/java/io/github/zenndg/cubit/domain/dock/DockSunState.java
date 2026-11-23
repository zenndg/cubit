package io.github.zenndg.cubit.domain.dock;

/** What the panel of a Dock is doing, and why it stops, for the Solar tab (answer 1396). */
public enum DockSunState {
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
