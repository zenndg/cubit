package io.github.zenndg.cubit.domain.robot;

/**
 * The first of two clicks of the Tablet that confirm something that is lost: the second one, on the
 * same target and within five seconds, confirms it (answers 2299 and 2589).
 *
 * @param <T> what the click is on
 * @param tick the game time of the first click
 */
public record SecondClick<T>(T target, long tick) {
  /** Five seconds. */
  public static final int CONFIRM_TICKS = 100;

  public boolean confirmedBy(T clicked, long now) {
    return target.equals(clicked) && now >= tick && now - tick <= CONFIRM_TICKS;
  }
}
