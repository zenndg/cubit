package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import java.util.function.BooleanSupplier;

/**
 * What a Dock takes from the antenna of its chunk: within its input of 5,000 FE/t, shared with the
 * cables, after the sun, which stays out of the limit (detail 19 of the Network and detail 12 of
 * Delivery 5). The screen shows what entered in the last tick, and the line only while an antenna
 * feeds it (answer 2593).
 */
public final class DockNetworkInput {
  /** An antenna marks its Docks every tick, so two ticks without it mean none feeds it. */
  private static final int LINK_TICKS = 2;

  private final BlockEnergy energy;
  private final BooleanSupplier charging;
  private Energy entered = Energy.ZERO;
  private Energy shown = Energy.ZERO;
  private long linkedUntil = Long.MIN_VALUE;

  DockNetworkInput(BlockEnergy energy, BooleanSupplier charging) {
    this.energy = energy;
    this.charging = charging;
  }

  /** Whether a Cubit charges in it now, which puts it first with short energy (answer 2302). */
  public boolean chargesCubit() {
    return charging.getAsBoolean();
  }

  /** How much more it takes in this tick, from the network and the cables together. */
  public Energy room() {
    return energy.receive(new Energy(energy.capacityAmount()), true);
  }

  /** What it kept of a share, which the antenna loses exactly (rule 13). */
  public Energy take(Energy share) {
    Energy kept = energy.receiveFromNetwork(share);
    entered = entered.plus(kept);
    return kept;
  }

  /** An antenna feeds it in this tick, also with nothing to give. */
  public void linked(long now) {
    linkedUntil = now + LINK_TICKS;
  }

  public boolean isLinked(long now) {
    return now <= linkedUntil;
  }

  /** What entered from the network in the last tick. */
  public Energy shown() {
    return shown;
  }

  void tick() {
    shown = entered;
    entered = Energy.ZERO;
  }
}
