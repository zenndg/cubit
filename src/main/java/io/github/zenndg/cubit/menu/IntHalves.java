package io.github.zenndg.cubit.menu;

import net.minecraft.world.inventory.ContainerData;

/** An int of a screen in two values, because each value travels in 16 bits. */
final class IntHalves {
  private static final int BITS = 16;
  private static final int MASK = 0xFFFF;

  private IntHalves() {}

  static void put(ContainerData values, int index, int value) {
    values.set(index, value & MASK);
    values.set(index + 1, value >>> BITS);
  }

  /** The halves may arrive sign-extended, so only their 16 bits count. */
  static int join(ContainerData values, int index) {
    return (values.get(index + 1) << BITS) | (values.get(index) & MASK);
  }

  /** Never negative, so a value before the first ones arrive, or a bad one, is safe. */
  static int joinPositive(ContainerData values, int index) {
    return Math.max(0, join(values, index));
  }
}
