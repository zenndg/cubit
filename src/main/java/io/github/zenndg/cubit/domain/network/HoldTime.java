package io.github.zenndg.cubit.domain.network;

/** How long a reserve holds a chunk: minutes up to two hours, and hours and minutes from there. */
public record HoldTime(long hours, long minutes) {
  private static final long TICKS_PER_MINUTE = 1_200;
  private static final long MINUTES_PER_HOUR = 60;
  private static final long ONLY_MINUTES = 120;

  public static HoldTime of(long ticks) {
    long total = ticks / TICKS_PER_MINUTE;
    return total < ONLY_MINUTES
        ? new HoldTime(0, total)
        : new HoldTime(total / MINUTES_PER_HOUR, total % MINUTES_PER_HOUR);
  }
}
