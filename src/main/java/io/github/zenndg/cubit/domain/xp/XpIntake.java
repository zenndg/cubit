package io.github.zenndg.cubit.domain.xp;

/**
 * What a Cubit does with the XP it gets, by its mode: the one of its work, straight to the tank,
 * and the orbs of the ground (answers 1819, 1822 and 1827). The XP a player sends never comes here,
 * so no mode deletes it (answer 2080).
 */
public final class XpIntake {
  private XpIntake() {}

  /** The tank, and the points that fall as an orb beside the Cubit. */
  public record Work(XpTank tank, int spilled) {}

  /** The tank, and the points that leave the orb. */
  public record Orb(XpTank tank, int taken) {}

  public static Work ofWork(XpTank tank, XpSettings settings, int points, XpRate rate) {
    if (settings.mode() == XpMode.IGNORE) {
      return new Work(tank, 0);
    }
    XpTank.Moved filled = tank.fill(points, rate);
    int rest = Math.max(0, points - filled.points());
    return new Work(filled.tank(), settings.deletesWhatDoesNotFit() ? 0 : rest);
  }

  /** A bigger orb gives only the part that fits (answer 2065). */
  public static Orb ofOrb(XpTank tank, XpSettings settings, int points, XpRate rate) {
    if (settings.mode() == XpMode.IGNORE) {
      return new Orb(tank, points);
    }
    XpTank.Moved filled = tank.fill(points, rate);
    return new Orb(filled.tank(), settings.deletesWhatDoesNotFit() ? points : filled.points());
  }

  /** With nothing to do with an orb, the Cubit does not even look for one. */
  public static boolean collects(XpTank tank, XpSettings settings, XpRate rate) {
    return settings.mode() == XpMode.IGNORE
        || settings.deletesWhatDoesNotFit()
        || !tank.isFull(rate);
  }
}
