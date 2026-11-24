package io.github.zenndg.cubit.domain.xp;

/**
 * An immutable tank of Cubit XP Fluid, in millibuckets. Only whole points move, and each operation
 * tells how many, so none is created or lost (rule 13). A capacity the config made smaller keeps
 * the fluid over it, and takes no more (answer 1937).
 */
public record XpTank(int fluid, int capacity) {
  private static final String INVALID = "Invalid XP tank: ";
  private static final String OF = " of ";

  /** The tank after a move, and how many points moved. */
  public record Moved(XpTank tank, int points) {}

  public XpTank {
    if (fluid < 0 || capacity < 0) {
      throw new IllegalArgumentException(INVALID + fluid + OF + capacity);
    }
  }

  public int points(XpRate rate) {
    return rate.pointsIn(fluid);
  }

  /** How many more points fit. */
  public int room(XpRate rate) {
    return rate.pointsIn(freeFluid());
  }

  /** Without a whole point left to give. */
  public boolean isEmpty(XpRate rate) {
    return points(rate) == 0;
  }

  /** One more point does not fit (detail 3 of the concept). */
  public boolean isFull(XpRate rate) {
    return room(rate) == 0;
  }

  /** The free room of a pipe, which moves any amount (answer 2102). */
  public int freeFluid() {
    return Math.max(0, capacity - fluid);
  }

  /** What a pipe put in, or took out when negative. */
  public XpTank plusFluid(int amount) {
    return new XpTank(fluid + amount, capacity);
  }

  public Moved fill(int points, XpRate rate) {
    int moved = Math.clamp(points, 0, room(rate));
    return new Moved(new XpTank(fluid + rate.fluidOf(moved), capacity), moved);
  }

  public Moved drain(int points, XpRate rate) {
    int moved = Math.clamp(points, 0, points(rate));
    return new Moved(new XpTank(fluid - rate.fluidOf(moved), capacity), moved);
  }

  /** The same fluid with the capacity of another tier or of a changed config (answer 1838). */
  public XpTank withCapacity(int next) {
    return new XpTank(fluid, next);
  }
}
