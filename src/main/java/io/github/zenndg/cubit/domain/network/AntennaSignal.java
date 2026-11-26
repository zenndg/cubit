package io.github.zenndg.cubit.domain.network;

/**
 * The signal of an antenna with its Network Server and how long it has been without it. Without
 * signal for longer than the loss time, it stops, also with energy of its own, and it starts again
 * by itself when the signal is back (answers 2323, 2552 and 2553).
 */
public record AntennaSignal(int ticksWithout) {
  public static final AntennaSignal LINKED = new AntennaSignal(0);
  private static final int TICKS_PER_MINUTE = 1200;
  private static final String NEGATIVE = "The ticks without signal cannot be negative: ";

  public AntennaSignal {
    if (ticksWithout < 0) {
      throw new IllegalArgumentException(NEGATIVE + ticksWithout);
    }
  }

  /** One more tick, with or without the signal now. It never grows past the loss time. */
  public AntennaSignal next(boolean signalNow, int lossTicks) {
    return signalNow ? LINKED : new AntennaSignal(Math.min(ticksWithout + 1, lossTicks));
  }

  public State state(int lossTicks) {
    if (ticksWithout == 0) {
      return State.LINKED;
    }
    return ticksWithout >= lossTicks ? State.STOPPED : State.NO_SIGNAL;
  }

  /** The whole minutes left until it stops, rounded up, so the screen never shows zero early. */
  public int minutesLeft(int lossTicks) {
    int left = Math.max(0, lossTicks - ticksWithout);
    return (left + TICKS_PER_MINUTE - 1) / TICKS_PER_MINUTE;
  }

  /** Linked, without signal and still working, or stopped. */
  public enum State {
    LINKED,
    NO_SIGNAL,
    STOPPED;

    public boolean works() {
      return this != STOPPED;
    }
  }
}
