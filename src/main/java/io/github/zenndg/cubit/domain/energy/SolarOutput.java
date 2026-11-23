package io.github.zenndg.cubit.domain.energy;

/** What a solar panel gives per tick, by day and by night. The numbers come from the config. */
public record SolarOutput(Energy dayPerTick, Energy nightPerTick) {
  public Energy at(boolean day) {
    return day ? dayPerTick : nightPerTick;
  }

  public boolean givesAtNight() {
    return !nightPerTick.isZero();
  }

  /** What it gives at most at the moment, or by day when it gives nothing at night (1949). */
  public Energy most(boolean day) {
    return at(day || !givesAtNight());
  }
}
