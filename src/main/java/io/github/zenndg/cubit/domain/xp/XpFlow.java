package io.github.zenndg.cubit.domain.xp;

/**
 * The pace of the XP at the tier of a Dock, in whole points: from the tank of a docked Cubit, and
 * into energy (answers 1835 and 1939). What a tick could move short of a point stays for the next
 * one, so the average is the one of the tier and waiting never saves up for a burst.
 */
public record XpFlow(int carried) {
  public static final XpFlow START = new XpFlow(0);

  /** The next flow, and how many points may move in this tick. */
  public record Budget(XpFlow flow, int points) {}

  /** The next flow, the tank after it and how many points left it. */
  public record Step(XpFlow flow, XpTank from, int points) {}

  public Budget tick(int perTick, XpRate rate) {
    int budget = (int) Math.min((long) carried + perTick, Integer.MAX_VALUE);
    return new Budget(new XpFlow(budget % rate.fluidPerPoint()), rate.pointsIn(budget));
  }

  public Step drain(XpTank from, int perTick, XpRate rate) {
    Budget budget = tick(perTick, rate);
    XpTank.Moved drained = from.drain(budget.points(), rate);
    return new Step(budget.flow(), drained.tank(), drained.points());
  }
}
