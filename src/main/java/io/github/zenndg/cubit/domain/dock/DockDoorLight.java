package io.github.zenndg.cubit.domain.dock;

/**
 * The small light on the edge of each door of the cabinet: red with the panel stored, green with it
 * deployed, and yellow, blinking, while the arms move it (answers 1853 and 1863).
 */
public enum DockDoorLight {
  RED(false),
  YELLOW(true),
  GREEN(false);

  /** One blink, on and off, of the yellow light. Only visual, so not in the config. */
  public static final long BLINK_TICKS = 10;

  private final boolean blinks;

  DockDoorLight(boolean blinks) {
    this.blinks = blinks;
  }

  public static DockDoorLight of(DockPanelState panel) {
    return switch (panel) {
      case STORED -> RED;
      case DEPLOYED -> GREEN;
      case DEPLOYING, RETRACTING -> YELLOW;
    };
  }

  /** Whether the light is on at this tick: a blinking light spends half of each blink off. */
  public boolean isLit(long ticks) {
    return !blinks || Math.floorMod(ticks, BLINK_TICKS) < BLINK_TICKS / 2;
  }
}
