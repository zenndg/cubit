package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * Warns once when the battery goes down into the reserve, and again only after it left it. Without
 * a reserve it never warns (Delivery 3, detail 13).
 */
public record UpsWatch(boolean inside) {
  public static final UpsWatch OUTSIDE = new UpsWatch(false);

  public Step next(UpsReserve reserve, Energy charge, Energy capacity) {
    boolean now = !reserve.isNone() && charge.amount() <= reserve.of(capacity).amount();
    return new Step(new UpsWatch(now), now && !inside);
  }

  public record Step(UpsWatch watch, boolean warns) {}
}
