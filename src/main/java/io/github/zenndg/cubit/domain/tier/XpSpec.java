package io.github.zenndg.cubit.domain.tier;

/** The XP tank of a Cubit at a tier, in millibuckets (answer 1826). */
public record XpSpec(int tankCapacity) {
  /** No tank, as in a tier built without XP numbers, which only tests need. */
  public static final XpSpec NONE = new XpSpec(0);

  private static final String INVALID = "Invalid XP tank capacity: ";

  public XpSpec {
    if (tankCapacity < 0) {
      throw new IllegalArgumentException(INVALID + tankCapacity);
    }
  }
}
