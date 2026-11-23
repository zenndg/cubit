package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.dock.DockSunState;
import io.github.zenndg.cubit.domain.energy.Energy;
import net.minecraft.world.inventory.ContainerData;

/** The numbers of the Solar tab of the dock screen (answers 1391, 1396, 1865 and 1949). */
public record DockSolarView(ContainerData values) {
  /** What the panel gives now, zero without the sun or the moon (answers 1391 and 1855). */
  public Energy now() {
    return DockScreenValues.energy(values, DockScreenValues.SOLAR);
  }

  /** What the panel gives at most at the moment, at the tier of the dock (1396 and 1949). */
  public Energy most() {
    return DockScreenValues.energy(values, DockScreenValues.SOLAR_MAX);
  }

  /** Whether the tier of the dock gives at night, for the hint of the tab (answer 1949). */
  public boolean givesAtNight() {
    return DockScreenValues.isTrue(values, DockScreenValues.SOLAR_NIGHT);
  }

  public DockSunState sun() {
    return ScreenEnums.of(DockSunState.values(), values.get(DockScreenValues.SUN_STATE));
  }

  /** Where the panel is: in the cabinet, on its way or on the tower (answer 1865). */
  public DockPanelState panel() {
    return ScreenEnums.of(DockPanelState.values(), values.get(DockScreenValues.PANEL));
  }
}
