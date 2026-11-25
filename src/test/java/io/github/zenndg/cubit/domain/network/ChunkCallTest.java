package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class ChunkCallTest {
  private static final Energy COST = new Energy(25);
  private static final Energy RESUME = new Energy(30_000);
  private static final Energy BELOW_RESUME = new Energy(29_999);
  private static final Energy BELOW_COST = new Energy(24);
  private static final ChunkCall.Price PRICE = new ChunkCall.Price(COST, RESUME);
  private static final ChunkCall.Room FREE = new ChunkCall.Room(true, true);
  private static final ChunkCall.Room PLAYER_FULL = new ChunkCall.Room(false, false);
  private static final ChunkCall.Room WORLD_FULL = new ChunkCall.Room(true, false);

  private static ChunkState decide(ChunkCall call, ChunkState before, Energy charge) {
    return call.decide(before, charge, PRICE);
  }

  @Test
  void holdsWhileItPays() {
    ChunkCall call = new ChunkCall(true, true, true, FREE);
    assertEquals(ChunkState.HOLDING, decide(call, ChunkState.HOLDING, COST));
    assertTrue(ChunkState.HOLDING.holds());
    assertFalse(ChunkState.NO_ENERGY.holds());
  }

  @Test
  void releasesWithoutTheCostOfATick() {
    ChunkCall call = new ChunkCall(true, true, true, FREE);
    assertEquals(ChunkState.NO_ENERGY, decide(call, ChunkState.HOLDING, BELOW_COST));
  }

  @Test
  void takesTheChunkBackOnlyWithEnoughEnergy() {
    ChunkCall call = new ChunkCall(true, true, true, FREE);
    assertEquals(ChunkState.NO_ENERGY, decide(call, ChunkState.NO_ENERGY, BELOW_RESUME));
    assertEquals(ChunkState.HOLDING, decide(call, ChunkState.NO_ENERGY, RESUME));
  }

  @Test
  void theConfigComesBeforeTheSwitchAndTheOwner() {
    assertEquals(
        ChunkState.LOCKED,
        decide(new ChunkCall(false, false, false, PLAYER_FULL), ChunkState.HOLDING, Energy.ZERO));
    assertEquals(
        ChunkState.SWITCHED_OFF,
        decide(new ChunkCall(true, false, false, PLAYER_FULL), ChunkState.HOLDING, Energy.ZERO));
    assertEquals(
        ChunkState.OWNER_OFFLINE,
        decide(new ChunkCall(true, true, false, PLAYER_FULL), ChunkState.HOLDING, Energy.ZERO));
  }

  @Test
  void theLimitsComeLast() {
    assertEquals(
        ChunkState.PLAYER_LIMIT,
        decide(new ChunkCall(true, true, true, PLAYER_FULL), ChunkState.HOLDING, RESUME));
    assertEquals(
        ChunkState.WORLD_LIMIT,
        decide(new ChunkCall(true, true, true, WORLD_FULL), ChunkState.HOLDING, RESUME));
  }
}
