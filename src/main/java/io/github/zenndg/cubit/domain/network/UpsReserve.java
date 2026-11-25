package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * The part of a battery kept only for the chunk, from 0% to 50% in steps of 5, with how long it
 * lasts (answers 2209, 2212, 2219 and 2221).
 */
public record UpsReserve(int percent) {
  public static final int MAX = 50;
  public static final int STEP = 5;
  public static final UpsReserve NONE = new UpsReserve(0);
  private static final int WHOLE = 100;
  private static final String INVALID = "A reserve goes from 0 to 50, in steps of 5: ";

  public UpsReserve {
    if (percent < 0 || percent > MAX || percent % STEP != 0) {
      throw new IllegalArgumentException(INVALID + percent);
    }
  }

  /** A saved or edited number goes to the nearest step below it, inside the range (rule 17). */
  public static UpsReserve of(int percent) {
    int kept = Math.min(Math.max(percent, 0), MAX);
    return new UpsReserve(kept - kept % STEP);
  }

  public UpsReserve more() {
    return of(percent + STEP);
  }

  public UpsReserve less() {
    return of(percent - STEP);
  }

  public boolean isNone() {
    return percent == 0;
  }

  public Energy of(Energy capacity) {
    return new Energy(
        capacity.amount() / WHOLE * percent + capacity.amount() % WHOLE * percent / WHOLE);
  }

  /** What may be spent on anything but the chunk. */
  public Energy spare(Energy charge, Energy capacity) {
    Energy kept = of(capacity);
    return charge.amount() > kept.amount() ? charge.minus(kept) : Energy.ZERO;
  }

  /** How many ticks the whole reserve pays the chunk for; a free chunk is never paid. */
  public long holdTicks(Energy capacity, Energy cost) {
    return cost.isZero() ? 0 : of(capacity).amount() / cost.amount();
  }
}
