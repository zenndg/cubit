package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What the signal of an antenna costs by its distance to the Network Server: nothing up to the free
 * blocks, and then the cost for each whole step beyond them, in a straight line on the plane,
 * without the height (answers 2290 and 2310). With the defaults, nothing up to 599 blocks, 1 FE/t
 * at 600 and 2 at 1,100.
 */
public record SignalDistance(int freeBlocks, int stepBlocks, Energy costPerStep) {
  private static final String BAD_FREE = "The free blocks cannot be negative: ";
  private static final String BAD_STEP = "A step has at least one block: ";

  public SignalDistance {
    if (freeBlocks < 0) {
      throw new IllegalArgumentException(BAD_FREE + freeBlocks);
    }
    if (stepBlocks < 1) {
      throw new IllegalArgumentException(BAD_STEP + stepBlocks);
    }
  }

  /** The blocks on the plane between two points, rounded down. */
  public static long blocks(int fromX, int fromZ, int toX, int toZ) {
    long dx = (long) toX - fromX;
    long dz = (long) toZ - fromZ;
    return (long) Math.floor(Math.sqrt((double) dx * dx + (double) dz * dz));
  }

  public Energy cost(long blocks) {
    long steps = Math.max(0, blocks - freeBlocks) / stepBlocks;
    return new Energy(Math.multiplyExact(steps, costPerStep.amount()));
  }
}
