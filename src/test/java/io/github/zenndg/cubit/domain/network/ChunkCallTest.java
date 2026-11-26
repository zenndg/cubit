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
  private static final ChunkCall.Reach SHARED = new ChunkCall.Reach(true, true);
  private static final ChunkCall.Reach STOPPED = new ChunkCall.Reach(false, false);

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

  /** An antenna in the chunk its Server holds pays nothing for it again (answer 2264). */
  @Test
  void anAntennaInTheChunkOfItsServerSharesIt() {
    assertEquals(
        ChunkState.SHARED, new ChunkCall(true, true, false, PLAYER_FULL, SHARED).decide(DRAINED));
    assertEquals(
        ChunkState.SWITCHED_OFF,
        new ChunkCall(true, false, true, FREE, SHARED).decide(ChunkCharge.CHARGED));
    assertFalse(ChunkState.SHARED.holds());
    assertFalse(ChunkState.SHARED.waits());
  }

  /** Stopped without signal, it waits in the line and holds nothing (answer 2553). */
  @Test
  void aStoppedAntennaWaitsForTheSignal() {
    assertEquals(
        ChunkState.NO_SIGNAL, new ChunkCall(true, true, true, FREE, STOPPED).decide(DRAINED));
    assertEquals(
        ChunkState.OWNER_OFFLINE,
        new ChunkCall(true, true, false, FREE, STOPPED).decide(ChunkCharge.CHARGED));
    assertTrue(ChunkState.NO_SIGNAL.waits());
    assertTrue(ChunkState.OWNER_OFFLINE.waits());
    assertFalse(ChunkState.NO_SIGNAL.holds());
    assertFalse(ChunkState.NO_ENERGY.waits());
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
