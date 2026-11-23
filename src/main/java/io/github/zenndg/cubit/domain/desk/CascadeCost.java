package io.github.zenndg.cubit.domain.desk;

import io.github.zenndg.cubit.domain.energy.Energy;

/** The energy and the time of a plan, shown before the start and never asked for (answer 2013). */
public record CascadeCost(int crafts, int craftTicks, Energy craftEnergy) {
  private static final String NEGATIVE = "A cost cannot be negative";
  private static final int TICKS_PER_SECOND = 20;

  public CascadeCost {
    if (crafts < 0 || craftTicks < 0) {
      throw new IllegalArgumentException(NEGATIVE);
    }
  }

  public Energy energy() {
    return new Energy(Math.multiplyExact(craftEnergy.amount(), crafts));
  }

  /** Rounded up, so a plan never takes longer than it says. */
  public long seconds() {
    return Math.ceilDiv((long) crafts * craftTicks, TICKS_PER_SECOND);
  }
}
