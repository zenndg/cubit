package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;

/**
 * The numbers of a Network Server at a tier: the battery, the channels, what it takes per tick by
 * cables and redstone, the slots of the storage, in whole rows of the screen, and what its Solar
 * Relay gives by day and at night, which may be zero (answers 2220 to 2223, 2267, 2269 and 2326).
 */
public record ServerSpec(
    Energy capacity, int channels, Energy inputPerTick, int storageSlots, SolarOutput relay) {
  public static final int ROW = 9;
  public static final int MAX_SLOTS = 54;
  public static final int MAX_CHANNELS = 16;
  private static final String NO_CAPACITY = "A server needs a battery";
  private static final String NO_INPUT = "A server needs an input";
  private static final String BAD_SLOTS = "The slots of a server are whole rows, up to six: ";
  private static final String BAD_CHANNELS = "A server has from 1 to 16 channels: ";

  public ServerSpec {
    if (capacity.isZero()) {
      throw new IllegalArgumentException(NO_CAPACITY);
    }
    if (inputPerTick.isZero()) {
      throw new IllegalArgumentException(NO_INPUT);
    }
    if (storageSlots < ROW || storageSlots > MAX_SLOTS || storageSlots % ROW != 0) {
      throw new IllegalArgumentException(BAD_SLOTS + storageSlots);
    }
    if (channels < 1 || channels > MAX_CHANNELS) {
      throw new IllegalArgumentException(BAD_CHANNELS + channels);
    }
  }
}
