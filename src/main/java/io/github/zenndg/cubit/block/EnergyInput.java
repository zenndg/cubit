package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;

/** A block that takes energy in and never gives any out, as the cables of other mods see it. */
interface EnergyInput {
  Energy receive(Energy offered, boolean simulate);

  Energy energy();

  long capacityAmount();
}
