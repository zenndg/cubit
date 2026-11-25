package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.DockSolarReading;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SolarPanel;

/**
 * Whether the panel of a dock gives energy now, and under the moon, for its pulse and its screen
 * (answers 1950 and 1959). The server sends whether it gives, and the clock of the world is here.
 */
final class DockMoonlight {
  private DockMoonlight() {}

  static boolean gives(InductionDockBlockEntity dock) {
    DockSolarReading solar = dock.solar();
    return solar.sunlit() && solar.panel() == PanelState.DEPLOYED;
  }

  static boolean isNight(InductionDockBlockEntity dock) {
    return dock.getLevel() != null && !SolarPanel.isDay(dock.getLevel().getDayTime());
  }

  static boolean shines(InductionDockBlockEntity dock) {
    return gives(dock) && isNight(dock);
  }
}
