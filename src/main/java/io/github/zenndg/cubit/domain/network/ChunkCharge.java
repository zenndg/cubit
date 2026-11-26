package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * Whether a block ran out of energy for its chunk. It only takes the chunk back with enough to
 * last, so it does not flicker, and it remembers that also through a save (answers 2372 and 2410).
 */
public record ChunkCharge(boolean drained) {
  public static final ChunkCharge CHARGED = new ChunkCharge(false);

  public ChunkCharge next(Energy charge, ChunkCall.Price price) {
    Energy needed = drained ? price.resume() : price.perTick();
    return new ChunkCharge(charge.amount() < needed.amount());
  }
}
