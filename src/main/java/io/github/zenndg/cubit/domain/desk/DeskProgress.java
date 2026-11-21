package io.github.zenndg.cubit.domain.desk;

/**
 * The ticks of the craft in progress. A pause keeps them, and a new recipe starts from zero
 * (answers 1443 and 1448).
 */
public record DeskProgress(int ticks) {
  public static final DeskProgress NONE = new DeskProgress(0);
  private static final int PERCENT = 100;
  private static final String NEGATIVE = "Progress cannot be negative: ";

  public DeskProgress {
    if (ticks < 0) {
      throw new IllegalArgumentException(NEGATIVE + ticks);
    }
  }

  public DeskProgress advance() {
    return new DeskProgress(ticks + 1);
  }

  /** A faster tier finishes a craft that already passed its time. */
  public boolean isReady(int craftTicks) {
    return ticks >= craftTicks;
  }

  /**
   * The client moves the progress on by itself between two updates from the server. Ticks that went
   * back, from a clock that changed, move nothing.
   */
  public DeskProgress plus(int elapsed) {
    return new DeskProgress(ticks + Math.max(0, elapsed));
  }

  /** The share done, from zero to one, for the hologram. */
  public float fraction(int craftTicks) {
    return Math.min(1f, (float) ticks / craftTicks);
  }

  public int percent(int craftTicks) {
    return Math.min(PERCENT, ticks * PERCENT / craftTicks);
  }
}
