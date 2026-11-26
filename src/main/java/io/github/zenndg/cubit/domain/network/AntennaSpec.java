package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.Percentage;

/**
 * The numbers of the Signal Antenna, which has no tier (answer 2199): its battery, what it takes in
 * a tick from the network and from cables together (answer 2551), what it spends for being on, its
 * default UPS, how long it works without signal and the floor of its battery no Dock takes (answers
 * 2221, 2273, 2322, 2323, 2435 and 2599).
 */
public record AntennaSpec(
    Energy capacity,
    Energy inputPerTick,
    Energy standby,
    UpsReserve ups,
    int signalLossTicks,
    Percentage floor) {
  private static final String NO_CAPACITY = "An antenna needs a battery";
  private static final String NO_LOSS = "An antenna works at least one tick without signal: ";

  public AntennaSpec {
    if (capacity.isZero()) {
      throw new IllegalArgumentException(NO_CAPACITY);
    }
    if (signalLossTicks < 1) {
      throw new IllegalArgumentException(NO_LOSS + signalLossTicks);
    }
  }

  /** What it may give its Docks with this charge and the UPS its owner chose. */
  public Energy forDocks(Energy charge, UpsReserve chosen) {
    return DockFeed.spare(charge, capacity, chosen, floor);
  }

  /**
   * What it spends in a tick: for being on and for the distance, while it works, and for its chunk
   * while it holds it (answers 2321, 2435 and 2553). Switched off, nothing.
   */
  public Energy spending(
      boolean on, boolean works, boolean holdsChunk, Energy chunk, Energy distance) {
    if (!on || !works) {
      return Energy.ZERO;
    }
    return standby.plus(distance).plus(holdsChunk ? chunk : Energy.ZERO);
  }
}
