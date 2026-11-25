package io.github.zenndg.cubit.domain.behavior;

/** The numbers of calling a Cubit, from the config (answers 2460, 2464 and 2482). */
public record CallSpec(int cooldownSeconds, int arrivalBlocks, int timeoutSeconds) {
  public static final int MAX_COOLDOWN_SECONDS = 600;
  public static final int MAX_ARRIVAL_BLOCKS = 16;
  public static final int MAX_TIMEOUT_SECONDS = 60;
  public static final int TICKS_PER_SECOND = 20;
  private static final String INVALID = "Invalid call: %d, %d, %d";

  public CallSpec {
    if (cooldownSeconds < 0 || arrivalBlocks < 1 || timeoutSeconds < 1) {
      throw new IllegalArgumentException(
          INVALID.formatted(cooldownSeconds, arrivalBlocks, timeoutSeconds));
    }
  }

  public int timeoutTicks() {
    return timeoutSeconds * TICKS_PER_SECOND;
  }

  /** How many seconds until the next call, rounded up, and zero when it may come now. */
  public int secondsLeft(long lastCall, long now) {
    long left = lastCall + (long) cooldownSeconds * TICKS_PER_SECOND - now;
    // A clock that went back, as by a command, never holds the calls.
    if (left <= 0 || now < lastCall) {
      return 0;
    }
    return (int) ((left + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND);
  }
}
