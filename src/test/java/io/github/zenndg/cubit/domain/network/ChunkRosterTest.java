package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChunkRosterTest {
  private static final UUID ANNA = new UUID(0, 1);
  private static final UUID BEN = new UUID(0, 2);
  private static final String FIRST = "first";
  private static final String SECOND = "second";
  private static final String THIRD = "third";
  private static final ChunkLimits ONE_EACH = new ChunkLimits(1, 0);
  private static final ChunkLimits ONE_IN_THE_WORLD = new ChunkLimits(0, 1);
  private static final ChunkLimits TWO_EACH = new ChunkLimits(2, 0);
  private final ChunkRoster<String> roster = new ChunkRoster<>();

  @Test
  void countsWhatAPlayerHolds() {
    assertTrue(roster.hold(FIRST, ANNA));
    assertFalse(roster.hold(FIRST, ANNA));
    roster.hold(SECOND, BEN);
    assertEquals(1, roster.heldBy(ANNA));
    assertTrue(roster.holds(FIRST));
    assertFalse(roster.holds(THIRD));
  }

  /** Who came first stays, also when the limit goes down (Delivery 3, detail 14). */
  @Test
  void whoCameFirstStaysInsideTheLimitOfThePlayer() {
    roster.hold(FIRST, ANNA);
    roster.hold(SECOND, ANNA);
    assertTrue(roster.room(FIRST, ANNA, ONE_EACH).ofPlayer());
    assertFalse(roster.room(SECOND, ANNA, ONE_EACH).ofPlayer());
    assertFalse(roster.room(THIRD, ANNA, TWO_EACH).ofPlayer());
    assertTrue(roster.room(THIRD, BEN, ONE_EACH).ofPlayer());
  }

  @Test
  void whoCameFirstStaysInsideTheLimitOfTheWorld() {
    roster.hold(FIRST, ANNA);
    roster.hold(SECOND, BEN);
    assertTrue(roster.room(FIRST, ANNA, ONE_IN_THE_WORLD).ofWorld());
    assertFalse(roster.room(SECOND, BEN, ONE_IN_THE_WORLD).ofWorld());
    assertFalse(roster.room(THIRD, ANNA, ONE_IN_THE_WORLD).ofWorld());
  }

  /** With the owner away nothing is held, and the place in line is kept (answer 2210). */
  @Test
  void aWaitingOneHoldsNothingAndComesBack() {
    roster.hold(FIRST, ANNA);
    assertTrue(roster.await(FIRST));
    assertFalse(roster.await(FIRST));
    assertFalse(roster.holds(FIRST));
    assertEquals(0, roster.heldBy(ANNA));
    assertEquals(List.of(FIRST), roster.waitingOf(ANNA));
    assertTrue(roster.waitingOf(BEN).isEmpty());
    assertTrue(roster.room(SECOND, ANNA, ONE_EACH).ofPlayer());
    assertTrue(roster.hold(FIRST, ANNA));
    assertTrue(roster.waitingOf(ANNA).isEmpty());
  }

  @Test
  void aReleasedOneLeavesTheList() {
    roster.hold(FIRST, ANNA);
    assertTrue(roster.release(FIRST));
    assertFalse(roster.release(FIRST));
    assertFalse(roster.await(FIRST));
    assertTrue(roster.entries().isEmpty());
  }

  /** A save brings the entries back as they were, in order. */
  @Test
  void theEntriesComeBackInOrder() {
    roster.hold(FIRST, ANNA);
    roster.hold(SECOND, BEN);
    roster.await(FIRST);
    ChunkRoster<String> loaded = new ChunkRoster<>();
    roster.entries().forEach(loaded::restore);
    assertEquals(roster.entries(), loaded.entries());
    assertEquals(
        List.of(
            new ChunkRoster.Entry<>(FIRST, ANNA, true),
            new ChunkRoster.Entry<>(SECOND, BEN, false)),
        loaded.entries());
  }
}
