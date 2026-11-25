package io.github.zenndg.cubit.domain.robot;

import java.util.UUID;

/**
 * The first of the two clicks that erase an area: the second one, on the same Cubit and soon
 * enough, confirms it (answer 2299).
 *
 * @param tick the game time of the first click
 */
public record AreaErase(UUID robot, long tick) {
  /** Five seconds. */
  public static final int CONFIRM_TICKS = 100;

  public boolean confirmedBy(UUID clicked, long now) {
    return robot.equals(clicked) && now >= tick && now - tick <= CONFIRM_TICKS;
  }
}
