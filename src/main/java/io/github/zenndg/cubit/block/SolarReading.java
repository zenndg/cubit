package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SunState;

/**
 * What the panel of a Dock or of a Solar Relay gives now, what it gives at most at the moment,
 * whether its tier gives at night, what the sky does, whether it pulses on the client and where the
 * panel is (answers 1391, 1392, 1396, 1865 and 1949).
 */
public record SolarReading(
    Energy given,
    Energy max,
    boolean givesAtNight,
    SunState state,
    boolean sunlit,
    PanelState panel) {}
