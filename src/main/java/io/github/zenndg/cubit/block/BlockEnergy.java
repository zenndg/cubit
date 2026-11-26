package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.energy.InputMeter;
import io.github.zenndg.cubit.item.EnergySlotItems;
import io.github.zenndg.cubit.item.PowerBankItem;
import java.util.function.Supplier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * The energy of a block of the mod: its stock, the input per tick of the cables, the slot that
 * feeds it and what entered, for the screen.
 */
final class BlockEnergy implements EnergyInput {
  private static final int SLOT = 0;
  private static final int ONE_ITEM = 1;

  private final Supplier<Energy> inputPerTick;
  private final Container slot;
  private final Runnable changed;
  private final InputMeter meter = new InputMeter();
  private EnergyStock stock;
  private Energy inputLeft;
  private Energy entered = Energy.ZERO;

  /** The numbers of the start come apart, because the block is still being built. */
  BlockEnergy(Start start, Supplier<Energy> inputPerTick, Container slot, Runnable changed) {
    this.inputPerTick = inputPerTick;
    this.slot = slot;
    this.changed = changed;
    stock = EnergyStock.of(start.capacity(), Energy.ZERO);
    inputLeft = start.inputPerTick();
  }

  EnergyStock stock() {
    return stock;
  }

  void set(EnergyStock next) {
    stock = next;
  }

  /** What entered by cables and redstone, per tick, on average over the last second. */
  Energy inputShown() {
    return meter.perTick();
  }

  /** What entered so far in this tick. */
  Energy enteredNow() {
    return entered;
  }

  /** Once per tick, before anything else: the input opens again, and the slot feeds the stock. */
  void tick() {
    inputLeft = inputPerTick.get();
    meter.record(entered);
    entered = Energy.ZERO;
    useSlot();
  }

  /**
   * Redstone becomes stock whole. A Power Bank empties into the stock like the cables, within the
   * same input per tick, and stays in the slot when empty (answer 920).
   */
  private void useSlot() {
    ItemStack stack = slot.getItem(SLOT);
    if (PowerBankItem.is(stack)) {
      PowerBankTransfer.discharge(stack, offered -> receive(offered, false));
      return;
    }
    Energy itemEnergy = EnergySlotItems.energyOf(stack);
    if (stock.canTake(itemEnergy)) {
      stack.shrink(ONE_ITEM);
      stock = stock.take(itemEnergy);
      entered = entered.plus(itemEnergy);
      changed.run();
    }
  }

  @Override
  public Energy receive(Energy offered, boolean simulate) {
    EnergyStock.Received received = stock.receive(offered, inputLeft);
    if (!simulate) {
      stock = received.stock();
      inputLeft = inputLeft.minus(received.accepted());
      entered = entered.plus(received.accepted());
      changed.run();
    }
    return received.accepted();
  }

  @Override
  public Energy energy() {
    return stock.energy().charge();
  }

  @Override
  public long capacityAmount() {
    return stock.energy().capacity().amount();
  }

  /** The capacity and the input of a block just made. */
  record Start(Energy capacity, Energy inputPerTick) {}
}
