package io.github.zenndg.cubit.domain.network;

/**
 * How many items a slot of the Network Server keeps and gives: up to its limit of an item that
 * stacks, one of an item that does not, and never more than a normal stack at a time (answers 2263
 * and 2368).
 */
public record BigSlot(int limit) {
  /** The screen shows up to three digits, and no save brings more than this. */
  public static final int MAX_LIMIT = 999;

  private static final String INVALID = "A slot keeps from 1 to 999 items: ";
  private static final int ONE = 1;

  public BigSlot {
    if (limit < ONE || limit > MAX_LIMIT) {
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
