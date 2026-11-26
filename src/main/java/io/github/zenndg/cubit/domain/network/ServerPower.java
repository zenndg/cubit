package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/** What the battery did in a tick, for the startup and the power loss (Delivery 3, detail 12). */
public enum ServerPower {
  SAME,
  STARTS,
  FALLS;

  public static ServerPower between(Energy before, Energy after) {
    if (before.isZero() == after.isZero()) {
      return SAME;
    }
    return before.isZero() ? STARTS : FALLS;
  }
}
