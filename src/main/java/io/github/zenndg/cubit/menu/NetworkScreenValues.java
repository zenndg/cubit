package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.NetworkBlockEntity;

/** The numbers every screen of the Network shows: the battery and what enters it. */
abstract class NetworkScreenValues extends ScreenValues {
  private final NetworkBlockEntity block;

  NetworkScreenValues(int count, NetworkBlockEntity block) {
    super(count);
    this.block = block;
  }

  @Override
  void refresh() {
    put(NetworkMenu.ENERGY, block.energy());
    put(NetworkMenu.CAPACITY, block.capacity());
    put(NetworkMenu.INPUT, block.inputShown());
  }
}
