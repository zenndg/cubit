package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyHalves;
import net.minecraft.world.inventory.ContainerData;

/**
 * The numbers of the screen of a block. The server reads them once per tick, and the menu copies
 * them to the client. Energy takes two values, because each one travels in 16 bits.
 */
abstract class ScreenValues implements ContainerData {
  private static final int TRUE_VALUE = 1;

  private final int[] values;

  ScreenValues(int count) {
    values = new int[count];
  }

  /** Reads the block once, so the data slots do not read it each. */
  abstract void refresh();

  void put(int index, int value) {
    values[index] = value;
  }

  void put(int index, boolean value) {
    values[index] = value ? TRUE_VALUE : 0;
  }

  void put(int index, Energy energy) {
    values[index] = EnergyHalves.low(energy);
    values[index + 1] = EnergyHalves.high(energy);
  }

  /** On either side: the client gets the halves back from its own copy. */
  static Energy energy(ContainerData data, int index) {
    return EnergyHalves.join(data.get(index), data.get(index + 1));
  }

  static boolean isTrue(ContainerData data, int index) {
    return data.get(index) == TRUE_VALUE;
  }

  @Override
  public int get(int index) {
    return values[index];
  }

  /** The server never takes values from the client. */
  @Override
  public void set(int index, int value) {}

  @Override
  public int getCount() {
    return values.length;
  }
}
