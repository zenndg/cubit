package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.dock.DockSunState;
import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What the panel of a Dock gives now, what it gives at most at the tier, what the sky does, whether
 * it pulses on the client and where the panel is (answers 1391, 1392, 1396 and 1865).
 */
public record DockSolarReading(
    Energy given, Energy max, DockSunState state, boolean sunlit, DockPanelState panel) {}
