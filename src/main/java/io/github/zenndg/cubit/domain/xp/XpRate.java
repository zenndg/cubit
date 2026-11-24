package io.github.zenndg.cubit.domain.xp;

/** How much Cubit XP Fluid, in millibuckets, a point of the game is worth (answer 1817). */
public record XpRate(int fluidPerPoint) {
  private static final String INVALID = "A point needs some fluid: ";

  public XpRate {
    if (fluidPerPoint < 1) {
      throw new IllegalArgumentException(INVALID + fluidPerPoint);
    }
  }

  public int fluidOf(int points) {
    return (int) Math.min((long) points * fluidPerPoint, Integer.MAX_VALUE);
  }

  public int pointsIn(int fluid) {
    return fluid / fluidPerPoint;
  }
}
