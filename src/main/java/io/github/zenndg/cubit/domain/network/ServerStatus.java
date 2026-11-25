package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/** What the top of the screen of a Network Server says of its energy (answer 2397). */
public enum ServerStatus {
  ON,
  ON_UPS,
  NO_ENERGY;

  /** With the chunk switched off the reserve has nothing to hold, so the UPS is off (detail 23). */
  public static ServerStatus of(
      Energy charge, Energy capacity, UpsReserve ups, boolean chunkLoading) {
    if (charge.isZero()) {
      return NO_ENERGY;
    }
    boolean reserve = chunkLoading && !ups.isNone() && charge.amount() <= ups.of(capacity).amount();
    return reserve ? ON_UPS : ON;
  }
}
