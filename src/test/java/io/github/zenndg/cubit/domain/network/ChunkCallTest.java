package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChunkCallTest {
  private static final ChunkCharge DRAINED = new ChunkCharge(true);
  private static final ChunkCall.Room FREE = new ChunkCall.Room(true, true);
  private static final ChunkCall.Room PLAYER_FULL = new ChunkCall.Room(false, false);
  private static final ChunkCall.Room WORLD_FULL = new ChunkCall.Room(true, false);

  @Test
  void holdsWhileItPays() {
    ChunkCall call = new ChunkCall(true, true, true, FREE);
    assertEquals(ChunkState.HOLDING, call.decide(ChunkCharge.CHARGED));
    assertTrue(ChunkState.HOLDING.holds());
    assertFalse(ChunkState.NO_ENERGY.holds());
  }

  @Test
  void releasesWithoutEnergy() {
    ChunkCall call = new ChunkCall(true, true, true, FREE);
    assertEquals(ChunkState.NO_ENERGY, call.decide(DRAINED));
  }

  @Test
  void theConfigComesBeforeTheSwitchAndTheOwner() {
    assertEquals(
        ChunkState.LOCKED, new ChunkCall(false, false, false, PLAYER_FULL).decide(DRAINED));
    assertEquals(
        ChunkState.SWITCHED_OFF, new ChunkCall(true, false, false, PLAYER_FULL).decide(DRAINED));
    assertEquals(
        ChunkState.OWNER_OFFLINE, new ChunkCall(true, true, false, PLAYER_FULL).decide(DRAINED));
  }

  @Test
  void theLimitsComeLast() {
    assertEquals(
        ChunkState.NO_ENERGY, new ChunkCall(true, true, true, PLAYER_FULL).decide(DRAINED));
    assertEquals(
        ChunkState.PLAYER_LIMIT,
        new ChunkCall(true, true, true, PLAYER_FULL).decide(ChunkCharge.CHARGED));
    assertEquals(
        ChunkState.WORLD_LIMIT,
        new ChunkCall(true, true, true, WORLD_FULL).decide(ChunkCharge.CHARGED));
  }
}
