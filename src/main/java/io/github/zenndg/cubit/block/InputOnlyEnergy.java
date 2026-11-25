package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * How other mods see a block of the mod: it takes FE in, up to its input, and never gives any out.
 */
final class InputOnlyEnergy implements IEnergyStorage {
  private final EnergyInput block;

  InputOnlyEnergy(EnergyInput block) {
    this.block = block;
  }

  @Override
  public int receiveEnergy(int toReceive, boolean simulate) {
    return Math.toIntExact(block.receive(new Energy(Math.max(toReceive, 0)), simulate).amount());
  }

  @Override
  public int extractEnergy(int toExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getEnergyStored() {
    return Math.toIntExact(block.energy().amount());
  }

  @Override
  public int getMaxEnergyStored() {
    return Math.toIntExact(block.capacityAmount());
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return true;
  }
}
