package io.github.zenndg.cubit.domain.xp;

/**
 * The XP going from the tank of a docked Cubit to the one of the Dock, in whole points. What a tick
 * could move short of a point stays for the next one, so the average is the one of the tier and
 * waiting never saves up for a burst (answer 1835).
 */
public record XpFlow(int carried) {
  public static final XpFlow START = new XpFlow(0);

  /** The next flow and both tanks. */
  public record Step(XpFlow flow, XpTank from, XpTank to) {}

  public Step advance(XpTank from, XpTank to, int perTick, XpRate rate) {
    int budget = (int) Math.min((long) carried + perTick, Integer.MAX_VALUE);
    int wanted = Math.min(rate.pointsIn(budget), to.room(rate));
    XpTank.Moved drained = from.drain(wanted, rate);
    XpTank.Moved filled = to.fill(drained.points(), rate);
    return new Step(new XpFlow(budget % rate.fluidPerPoint()), drained.tank(), filled.tank());
  }
}
