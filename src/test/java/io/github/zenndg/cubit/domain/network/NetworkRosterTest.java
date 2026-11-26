package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NetworkRosterTest {
  private static final UUID ANNA = new UUID(0, 1);
  private static final UUID SERVER = new UUID(1, 1);
  private static final UUID OTHER_SERVER = new UUID(1, 2);
  private static final String HOME = "home";
  private static final String FARM = "farm";
  private static final String MINE = "mine";
  private static final String TOWER = "tower";
  private static final int TWO_OPEN = 2;
  private static final NetworkRoster.Server<String> BASE_SERVER =
      new NetworkRoster.Server<>(SERVER, ANNA, HOME, RobotTier.BASE);
  private final NetworkRoster<String> roster = new NetworkRoster<>();

  @Test
  void findsAServerByItsIdAndFollowsItsTier() {
    assertTrue(roster.putServer(BASE_SERVER));
    assertFalse(roster.putServer(BASE_SERVER));
    NetworkRoster.Server<String> upgraded =
        new NetworkRoster.Server<>(SERVER, ANNA, HOME, RobotTier.ELITE);
    assertTrue(roster.putServer(upgraded));
    assertEquals(Optional.of(upgraded), roster.server(SERVER));
    assertEquals(Optional.empty(), roster.server(OTHER_SERVER));
    assertTrue(roster.removeServer(SERVER));
    assertFalse(roster.removeServer(SERVER));
    assertEquals(List.of(), List.copyOf(roster.servers()));
  }

  /** The channels belong to one Server, and another Server has its own (answer 2269). */
  @Test
  void anAntennaTakesAChannelOfItsServer() {
    assertTrue(roster.putAntenna(antenna(FARM, SERVER, AntennaChannel.BLACK)));
    assertFalse(roster.putAntenna(antenna(FARM, SERVER, AntennaChannel.BLACK)));
    roster.putAntenna(antenna(MINE, OTHER_SERVER, AntennaChannel.BLUE));
    assertEquals(Set.of(AntennaChannel.BLACK), roster.taken(SERVER));
    assertEquals(Set.of(AntennaChannel.BLUE), roster.taken(OTHER_SERVER));
    assertTrue(roster.removeAntenna(FARM));
    assertFalse(roster.removeAntenna(FARM));
    assertEquals(Set.of(), roster.taken(SERVER));
    assertEquals(1, roster.antennas().size());
  }

  /** Placed again, it goes back to its last channel while free, or to the first free (2547). */
  @Test
  void aPlacedAntennaGetsItsLastChannelOrTheFirstFree() {
    roster.putAntenna(antenna(FARM, SERVER, AntennaChannel.BLACK));
    Optional<AntennaChannel> last = Optional.of(AntennaChannel.BLACK);
    assertEquals(Optional.of(AntennaChannel.BLUE), roster.channelFor(SERVER, last, TWO_OPEN));
    assertEquals(
        Optional.of(AntennaChannel.BLUE),
        roster.channelFor(SERVER, Optional.of(AntennaChannel.BLUE), TWO_OPEN));
    roster.putAntenna(antenna(MINE, SERVER, AntennaChannel.BLUE));
    assertEquals(Optional.empty(), roster.channelFor(SERVER, Optional.empty(), TWO_OPEN));
  }

  /** The antenna in the same place is taken out first, so it may keep its own channel. */
  @Test
  void anAntennaMovedToAnotherServerLeavesTheOldOne() {
    roster.putAntenna(antenna(TOWER, SERVER, AntennaChannel.BLACK));
    roster.putAntenna(antenna(TOWER, OTHER_SERVER, AntennaChannel.BLACK));
    assertEquals(Set.of(), roster.taken(SERVER));
    assertEquals(Set.of(AntennaChannel.BLACK), roster.taken(OTHER_SERVER));
  }

  @Test
  void refusesAnEntryWithoutItsParts() {
    assertThrows(
        NullPointerException.class,
        () -> new NetworkRoster.Server<>(SERVER, ANNA, null, RobotTier.BASE));
    assertThrows(
        NullPointerException.class, () -> new NetworkRoster.Antenna<>(FARM, ANNA, SERVER, null));
  }

  private static NetworkRoster.Antenna<String> antenna(
      String place, UUID server, AntennaChannel channel) {
    return new NetworkRoster.Antenna<>(place, ANNA, server, channel);
  }
}
