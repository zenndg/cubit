package io.github.zenndg.cubit.domain.network;

/**
 * Which of the six colors the wall around the chunk of the player takes: one drawn at random on
 * each new chunk, and never the one of the chunk the player came from, so the change always shows
 * (answers 2613 and 2614). The caller gives the random number, as the domain never reads chance.
 */
public final class WallColors {
  public static final int COUNT = 6;
  public static final int NONE = -1;

  private WallColors() {}

  /**
   * The next color, from 0 to 5.
   *
   * @param previous the color of the last chunk, or {@link #NONE} when the wall just went on
   * @param drawn any whole number drawn at random
   */
  public static int next(int previous, int drawn) {
    if (previous < 0 || previous >= COUNT) {
      return Math.floorMod(drawn, COUNT);
    }
    int other = Math.floorMod(drawn, COUNT - 1);
    return other >= previous ? other + 1 : other;
  }
}
