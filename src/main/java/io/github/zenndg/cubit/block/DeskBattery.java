package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskStock;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.item.EnergySlotItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * The battery of a desk: its energy, what cables and redstone still bring in this tick and the
 * redstone of its slot, which becomes energy whole (answers 1425 and 1447). Paying and receiving
 * never create nor lose energy (rule 13).
 */
final class DeskBattery {
  private static final int SLOT = 0;

  private final Runnable changed;
  private DeskStock stock;
  private Energy inputLeft = Energy.ZERO;

  DeskBattery(Energy capacity, Runnable changed) {
    this.changed = changed;
    stock = DeskStock.of(capacity, Energy.ZERO);
  }

  Energy charge() {
    return stock.charge();
  }

  Energy capacity() {
    return stock.capacity();
  }

  /** At the start of a tick, the input of the tick comes back. */
  void newTick(Energy inputPerTick) {
    inputLeft = inputPerTick;
  }

  void useSlot(Container slot) {
    ItemStack stack = slot.getItem(SLOT);
    Energy itemEnergy = EnergySlotItems.energyOf(stack);
    if (stock.canTake(itemEnergy)) {
      stack.shrink(1);
      stock = stock.take(itemEnergy);
      changed.run();
    }
  }

  boolean canPay(Energy price) {
    return stock.canPay(price);
  }

  void pay(Energy price) {
    stock = stock.pay(price);
    changed.run();
  }

  /** A new tier keeps the energy, up to the new battery. */
  void resize(Energy capacity) {
    stock = stock.withCapacity(capacity);
  }

  /** The energy of a save or of an item, up to the battery. */
  void load(Energy capacity, Energy saved) {
    stock = DeskStock.of(capacity, saved);
  }

  Energy receive(Energy offered, boolean simulate) {
    DeskStock.Received received = stock.receive(offered, inputLeft);
    if (!simulate) {
      stock = received.stock();
      inputLeft = inputLeft.minus(received.accepted());
      changed.run();
    }
    return received.accepted();
  }
}
