package io.github.zenndg.cubit.domain.robot;

import java.util.UUID;

/**
 * The first of the two clicks that erase an area: the second one, on the same Cubit and soon
 * enough, confirms it (answer 2299).
 *
 * @param tick the game time of the first click
 */
public record AreaErase(UUID robot, long tick) {
  /** Five seconds, as every second click of the Tablet. */
  public static final int CONFIRM_TICKS = SecondClick.CONFIRM_TICKS;

  public boolean confirmedBy(UUID clicked, long now) {
    return new SecondClick<>(robot, tick).confirmedBy(clicked, now);
  }
}
