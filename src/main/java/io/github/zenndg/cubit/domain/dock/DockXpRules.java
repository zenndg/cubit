package io.github.zenndg.cubit.domain.dock;

import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.xp.XpConversion;
import io.github.zenndg.cubit.domain.xp.XpTank;

/**
 * What a Dock does with XP, by its mode: the one a Cubit gives, and the stored one (answers 1831,
 * 1935 and 2104). The XP a player sends and the fluid of a pipe go straight to the tank and never
 * come here, so no mode deletes them (answers 1936 and 2072).
 */
public final class DockXpRules {
  private DockXpRules() {}

  /** The tank and the stock after it, and the points that became energy or were deleted. */
  public record Received(XpTank tank, EnergyStock stock, int converted, int deleted, boolean full) {
    /** More came than fit, which the owner is told of (answer 2103). */
    public boolean overflowed() {
      return full && converted + deleted > 0;
    }
  }

  /** The tank and the stock after it, and the points that became energy. */
  public record Converted(XpTank tank, EnergyStock stock, int points) {}

  /** The Dock always takes all of it: what fits nowhere is deleted (answers 1831 and 2061). */
  public static Received receive(
      DockXpMode mode, XpTank tank, EnergyStock stock, int points, XpConversion conversion) {
    if (mode == DockXpMode.DELETE_ALL) {
      return new Received(tank, stock, 0, points, false);
    }
    XpTank.Moved filled = tank.fill(points, conversion.rate());
    int rest = points - filled.points();
    int converted = mode.converts() ? Math.min(rest, conversion.pointsFitting(stock.room())) : 0;
    return new Received(
        filled.tank(),
        stock.take(conversion.energyOf(converted)),
        converted,
        rest - converted,
        filled.tank().isFull(conversion.rate()));
  }

  /** Only Convert all spends what is stored, and waits while the battery is full (1935, 2101). */
  public static Converted convert(
      DockXpMode mode, XpTank tank, EnergyStock stock, int budget, XpConversion conversion) {
    if (mode != DockXpMode.CONVERT_ALL) {
      return new Converted(tank, stock, 0);
    }
    int wanted = Math.min(budget, conversion.pointsFitting(stock.room()));
    XpTank.Moved drained = tank.drain(wanted, conversion.rate());
    return new Converted(
        drained.tank(), stock.take(conversion.energyOf(drained.points())), drained.points());
  }
}
