package io.github.zenndg.cubit.domain.network;

/**
 * How many antennas, Docks and chests the tier of a Cubit links in the Network app (answers 2226,
 * 2232, 2233 and 2602). A Base Cubit has no antenna.
 */
public record LinkLimits(int antennas, int docks, int chests) {
  private static final String NEGATIVE = "Link limits cannot be negative: %d, %d, %d";

  public LinkLimits {
    if (antennas < 0 || docks < 0 || chests < 0) {
      throw new IllegalArgumentException(NEGATIVE.formatted(antennas, docks, chests));
    }
  }
}
