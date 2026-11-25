package io.github.zenndg.cubit.domain.network;

/**
 * How many items a slot of the Network Server keeps and gives: up to its limit of an item that
 * stacks, one of an item that does not, and never more than a normal stack at a time (answers 2263
 * and 2368).
 */
public record BigSlot(int limit) {
  private static final String INVALID = "A slot keeps at least one item: ";
  private static final int ONE = 1;

  public BigSlot {
    if (limit < ONE) {
      throw new IllegalArgumentException(INVALID + limit);
    }
  }

  public int keeps(int stackSize) {
    return stackSize <= ONE ? ONE : Math.max(limit, stackSize);
  }

  /** How many of an offer fit beside what is already there. */
  public int takes(int stored, int offered, int stackSize) {
    return Math.min(Math.max(keeps(stackSize) - stored, 0), offered);
  }

  public int gives(int stored, int wanted, int stackSize) {
    return Math.min(Math.min(stored, wanted), stackSize);
  }
}
