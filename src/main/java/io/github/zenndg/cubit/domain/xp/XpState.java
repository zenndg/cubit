package io.github.zenndg.cubit.domain.xp;

/**
 * What a Cubit keeps of its XP, in the world and in the item: the fluid of the tank, the mode and
 * the switch of the excess. The capacity comes from the tier, so it is not here (answer 1838).
 */
public record XpState(int fluid, XpSettings settings) {
  /** An empty tank in the store, also for what was saved before the XP (answer 1829). */
  public static final XpState EMPTY = new XpState(0, XpSettings.INITIAL);

  private static final String NEGATIVE = "XP fluid cannot be negative: ";

  public XpState {
    if (fluid < 0) {
      throw new IllegalArgumentException(NEGATIVE + fluid);
    }
  }

  public XpTank tank(int capacity) {
    return new XpTank(fluid, capacity);
  }
}
