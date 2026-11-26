package io.github.zenndg.cubit.domain.network;

/**
 * How many antennas, Docks and chests the tier of a Cubit links in the Network app, with more
 * chests for an Organizer (answers 2226, 2232, 2233, 2235 and 2602). A Base Cubit has no antenna.
 */
public record LinkLimits(int antennas, int docks, int chests, int organizerChests) {
  private static final String NEGATIVE = "Link limits cannot be negative: %d, %d, %d, %d";

  public LinkLimits {
    if (antennas < 0 || docks < 0 || chests < 0 || organizerChests < 0) {
      throw new IllegalArgumentException(
          NEGATIVE.formatted(antennas, docks, chests, organizerChests));
    }
  }

  /** The chests of a Cubit with the card of the Organizer, or of any other. */
  public int chestsFor(boolean organizer) {
    return organizer ? organizerChests : chests;
  }
}
