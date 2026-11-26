package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.network.NetworkLinks.Click;
import io.github.zenndg.cubit.domain.network.NetworkLinks.Step;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NetworkLinksTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final int MIDDLE = 8;
  private static final int Y = 64;
  private static final int ELITE_ANTENNAS = 4;
  private static final int ELITE_MARKS = 4;
  private static final int ONE_MARK = 1;
  private static final UUID SERVER = UUID.randomUUID();
  private static final UUID OTHER_SERVER = UUID.randomUUID();
  private static final Optional<UUID> NO_SERVER = Optional.empty();
  private static final Optional<UUID> LINKED = Optional.of(SERVER);

  private static LinkSpot inChunk(int chunkX, int chunkZ) {
    return new LinkSpot(
        OVERWORLD, chunkX * LinkChunk.SIDE + MIDDLE, Y, chunkZ * LinkChunk.SIDE + MIDDLE);
  }

  private static LinkSpot cornerOf(int chunkX, int chunkZ) {
    return new LinkSpot(OVERWORLD, chunkX * LinkChunk.SIDE, Y, chunkZ * LinkChunk.SIDE);
  }

  private static NetworkLinks linked(LinkSpot... antennas) {
    return new NetworkLinks(List.of(antennas), List.of(), List.of());
  }

  @Test
  void theFirstAntennaLinksAndTheNextOnesOnlyBesideIt() {
    Click first = NetworkLinks.NONE.antenna(inChunk(0, 0), SERVER, NO_SERVER, ELITE_ANTENNAS);
    assertEquals(Step.LINKED, first.step());
    Click side = first.links().antenna(inChunk(1, 0), SERVER, LINKED, ELITE_ANTENNAS);
    assertEquals(Step.LINKED, side.step());
    assertEquals(List.of(inChunk(0, 0), inChunk(1, 0)), side.links().antennas());
  }

  @Test
  void aCornerOrAGapIsNotBeside() {
    NetworkLinks links = linked(inChunk(0, 0));
    assertEquals(
        Step.NOT_BESIDE, links.antenna(inChunk(1, 1), SERVER, LINKED, ELITE_ANTENNAS).step());
    assertEquals(
        Step.NOT_BESIDE, links.antenna(inChunk(2, 0), SERVER, LINKED, ELITE_ANTENNAS).step());
  }

  @Test
  void allTheAntennasOfACubitAreOfOneServer() {
    Click click =
        linked(inChunk(0, 0)).antenna(inChunk(1, 0), OTHER_SERVER, LINKED, ELITE_ANTENNAS);
    assertEquals(Step.OTHER_SERVER, click.step());
    assertEquals(linked(inChunk(0, 0)), click.links());
  }

  @Test
  void theTierLimitsTheAntennasAndABaseCubitHasNone() {
    assertEquals(Step.FULL, NetworkLinks.NONE.antenna(inChunk(0, 0), SERVER, NO_SERVER, 0).step());
    assertEquals(
        Step.FULL, linked(inChunk(0, 0)).antenna(inChunk(1, 0), SERVER, LINKED, ONE_MARK).step());
  }

  @Test
  void aLinkedAntennaAsksToConfirmUnlessItWouldSplitTheOthers() {
    NetworkLinks row = linked(inChunk(0, 0), inChunk(1, 0), inChunk(2, 0));
    assertEquals(Step.CONFIRM, row.antenna(inChunk(0, 0), SERVER, LINKED, ELITE_ANTENNAS).step());
    assertEquals(Step.SPLITS, row.antenna(inChunk(1, 0), SERVER, LINKED, ELITE_ANTENNAS).step());
    NetworkLinks square = linked(inChunk(0, 0), inChunk(1, 0), inChunk(1, 1), inChunk(0, 1));
    assertEquals(
        Step.CONFIRM, square.antenna(inChunk(1, 0), SERVER, LINKED, ELITE_ANTENNAS).step());
  }

  @Test
  void aConfirmedAntennaLeavesWithTheDocksAndChestsOfItsChunk() {
    NetworkLinks links =
        new NetworkLinks(
            List.of(inChunk(0, 0), inChunk(1, 0)),
            List.of(cornerOf(0, 0), cornerOf(1, 0)),
            List.of(inChunk(0, 0), inChunk(1, 0)));
    NetworkLinks left = links.without(inChunk(1, 0));
    assertEquals(
        new NetworkLinks(List.of(inChunk(0, 0)), List.of(cornerOf(0, 0)), List.of(inChunk(0, 0))),
        left);
  }

  @Test
  void docksAndChestsAreMarkedOnlyInsideTheChunksOfTheAntennas() {
    NetworkLinks links = linked(inChunk(0, 0));
    assertEquals(Step.OUTSIDE, links.dock(inChunk(1, 0), ELITE_MARKS).step());
    assertEquals(Step.OUTSIDE, links.chest(inChunk(1, 0), ELITE_MARKS).step());
    assertEquals(Step.OUTSIDE, NetworkLinks.NONE.dock(inChunk(0, 0), ELITE_MARKS).step());
    Click dock = links.dock(cornerOf(0, 0), ELITE_MARKS);
    Click chest = dock.links().chest(inChunk(0, 0), ELITE_MARKS);
    assertEquals(Step.LINKED, dock.step());
    assertEquals(Step.LINKED, chest.step());
    assertEquals(List.of(cornerOf(0, 0)), chest.links().docks());
    assertEquals(List.of(inChunk(0, 0)), chest.links().chests());
  }

  @Test
  void theSecondClickUnmarksAndTheTierLimitsTheMarks() {
    NetworkLinks links = linked(inChunk(0, 0)).dock(cornerOf(0, 0), ONE_MARK).links();
    assertEquals(Step.FULL, links.dock(inChunk(0, 0), ONE_MARK).step());
    Click unmarked = links.dock(cornerOf(0, 0), ONE_MARK);
    assertEquals(Step.UNLINKED, unmarked.step());
    assertEquals(linked(inChunk(0, 0)), unmarked.links());
    NetworkLinks chest = linked(inChunk(0, 0)).chest(inChunk(0, 0), ONE_MARK).links();
    assertEquals(Step.FULL, chest.chest(cornerOf(0, 0), ONE_MARK).step());
    assertEquals(Step.UNLINKED, chest.chest(inChunk(0, 0), ONE_MARK).step());
  }

  @Test
  void aLowerLimitKeepsTheFirstChests() {
    NetworkLinks links =
        new NetworkLinks(List.of(inChunk(0, 0)), List.of(), List.of(inChunk(0, 0), cornerOf(0, 0)));
    assertEquals(List.of(inChunk(0, 0)), links.chestsFitting(ONE_MARK).chests());
    assertEquals(links, links.chestsFitting(ELITE_MARKS));
  }

  @Test
  void itKnowsItsChunksAndWhatTheyCover() {
    NetworkLinks links = linked(inChunk(0, 0), inChunk(0, 1));
    assertEquals(Set.of(LinkChunk.of(OVERWORLD, 0, 0), inChunk(0, 1).chunk()), links.chunks());
    assertTrue(links.covers(cornerOf(0, 1)));
    assertFalse(links.covers(cornerOf(1, 1)));
    assertTrue(NetworkLinks.NONE.isEmpty());
    assertFalse(links.isEmpty());
  }

  @Test
  void aRepeatedSavedSpotCountsOnce() {
    NetworkLinks links =
        new NetworkLinks(
            List.of(inChunk(0, 0), inChunk(0, 0)),
            List.of(cornerOf(0, 0), cornerOf(0, 0)),
            List.of(inChunk(0, 0), inChunk(0, 0)));
    assertEquals(1, links.antennas().size());
    assertEquals(1, links.docks().size());
    assertEquals(1, links.chests().size());
  }
}
