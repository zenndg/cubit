package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class ChunkChargeTest {
  private static final Energy COST = new Energy(25);
  private static final Energy BELOW_COST = new Energy(24);
  private static final Energy RESUME = new Energy(30_000);
  private static final Energy BELOW_RESUME = new Energy(29_999);
  private static final ChunkCall.Price PRICE = new ChunkCall.Price(COST, RESUME);
  private static final ChunkCall.Price FREE = new ChunkCall.Price(Energy.ZERO, Energy.ZERO);

  @Test
  void drainsWithoutTheCostOfATick() {
    assertFalse(ChunkCharge.CHARGED.next(COST, PRICE).drained());
    assertTrue(ChunkCharge.CHARGED.next(BELOW_COST, PRICE).drained());
  }

  @Test
  void staysDrainedUntilTheChargeThatTakesTheChunkBack() {
    ChunkCharge drained = new ChunkCharge(true);
    assertTrue(drained.next(BELOW_RESUME, PRICE).drained());
    assertFalse(drained.next(RESUME, PRICE).drained());
  }

  @Test
  void aFreeChunkNeverDrains() {
    assertFalse(ChunkCharge.CHARGED.next(Energy.ZERO, FREE).drained());
    assertFalse(new ChunkCharge(true).next(Energy.ZERO, FREE).drained());
  }
}
