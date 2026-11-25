package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.item.PowerBankItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * The Power Banks in the input slot and in the storage of a block of the mod (answers 919 to 959).
 */
final class PowerBankTransfer {
  private PowerBankTransfer() {}

  /** The bank in the input slot empties into the stock like the cables, within the same input. */
  static void discharge(ItemStack stack, UnaryOperator<Energy> receive) {
    Battery bank = PowerBankItem.batteryOf(stack);
    Energy accepted = receive.apply(bank.charge());
    if (!accepted.isZero()) {
      PowerBankItem.store(stack, bank.consume(accepted).battery());
    }
  }

  /**
   * At the rate of the tier of the block (answer 1376). Entities tick before block entities, so a
   * docked robot already took its share this tick, and the bank only charges once it is full
   * (answer 952).
   */
  private static EnergyStock charge(
      EnergyStock stock, ItemStack stack, Energy rate, Runnable changed) {
    if (!PowerBankItem.is(stack)) {
      return stock;
    }
    Battery before = PowerBankItem.batteryOf(stack);
    EnergyStock.Transfer transfer = stock.charge(before, rate);
    if (transfer.robot().equals(before)) {
      return stock;
    }
    PowerBankItem.store(stack, transfer.robot());
    changed.run();
    return transfer.stock();
  }

  /** One bank of a storage at a time, the first that is not full (answer 949). */
  static EnergyStock chargeFirst(
      EnergyStock stock, Container items, Energy rate, Runnable changed) {
    OptionalInt index = DockStorageRules.bankToCharge(banks(items));
    return index.isPresent()
        ? charge(stock, items.getItem(index.getAsInt()), rate, changed)
        : stock;
  }

  private static List<Optional<Battery>> banks(Container items) {
    List<Optional<Battery>> banks = new ArrayList<>();
    for (int index = 0; index < items.getContainerSize(); index++) {
      ItemStack stack = items.getItem(index);
      banks.add(
          PowerBankItem.is(stack) ? Optional.of(PowerBankItem.batteryOf(stack)) : Optional.empty());
    }
    return banks;
  }
}
