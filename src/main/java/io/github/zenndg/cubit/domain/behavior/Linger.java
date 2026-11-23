package io.github.zenndg.cubit.domain.behavior;

/**
 * A part of the Patroller that stays on for a while after it was last wanted, without going off and
 * on again if it is wanted back in time: the lit blade and the open wings (answers 1805 and 1807).
 * The caller gives the time.
 */
public record Linger(long until) {
  public static final Linger OFF = new Linger(Long.MIN_VALUE);
  private static final String NEGATIVE = "Linger ticks cannot be negative: ";

  public Linger tick(boolean wanted, long now, int ticks) {
    if (ticks < 0) {
      throw new IllegalArgumentException(NEGATIVE + ticks);
    }
    return wanted ? new Linger(now + ticks) : this;
  }

  public boolean on(long now) {
    return now <= until;
  }
}
