package io.github.zenndg.cubit.domain.robot;

/**
 * The numbers of the drawing of an area: how few and how many points it takes, and how far the
 * player marks a block in sight (answers 2287 and 2311).
 */
public record AreaDrawingSpec(int minPoints, int maxPoints, int reachBlocks) {
  /** The fewest points that close a shape in right angles (answer 2287). */
  public static final int FEWEST_POINTS = 4;

  private static final String INVALID = "Invalid numbers of the drawing of an area: ";
  private static final String SEPARATOR = ", ";

  public AreaDrawingSpec {
    if (minPoints < FEWEST_POINTS || maxPoints < minPoints || reachBlocks < 1) {
      throw new IllegalArgumentException(
          INVALID + minPoints + SEPARATOR + maxPoints + SEPARATOR + reachBlocks);
    }
  }
}
