package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.item.EnergySlotItems;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/** The slot of a block that turns redstone and Power Banks into stock. */
final class EnergySlot extends SimpleContainer {
  private static final int SIZE = 1;
  private final Runnable changed;

  EnergySlot(Runnable changed) {
    super(SIZE);
    this.changed = changed;
  }

  @Override
  public boolean canPlaceItem(int index, ItemStack stack) {
    return EnergySlotItems.accepts(stack);
  }

  @Override
  public void setChanged() {
    changed.run();
  }
}
