package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.network.BigSlot;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.item.PowerBankItem;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * The slots of a Network Server, each with up to 128 items of the mod, and one of an item that does
 * not stack. Whatever takes items out gets at most a normal stack at a time, so no stack above it
 * ever reaches a hand or the ground (answers 2263 and 2368). The container always has the slots of
 * the Ultimate, and the tier opens the first ones.
 */
public final class ServerSlots extends SimpleContainer {
  private final Runnable changed;
  private final IntSupplier tierSlots;
  private final Supplier<BigSlot> rule;

  /** The client has the limit of the server, which its own config may not match. */
  public ServerSlots(Runnable changed, IntSupplier tierSlots, Supplier<BigSlot> rule) {
    super(ServerSpec.MAX_SLOTS);
    this.changed = changed;
    this.tierSlots = tierSlots;
    this.rule = rule;
  }

  BigSlot rule() {
    return rule.get();
  }

  public boolean isOpen(int index) {
    return DockStorageRules.opens(index, tierSlots.getAsInt());
  }

  public static boolean isBankToCharge(ItemStack stack) {
    return PowerBankItem.is(stack) && !PowerBankItem.batteryOf(stack).isFull();
  }

  @Override
  public int getMaxStackSize() {
    return rule.get().limit();
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return rule.get().keeps(stack.getMaxStackSize());
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    return super.removeItem(index, Math.min(count, getItem(index).getMaxStackSize()));
  }

  @Override
  public boolean canPlaceItem(int index, ItemStack stack) {
    return isOpen(index) && DockStorageRules.accepts(StoredKinds.of(stack));
  }

  @Override
  public void setChanged() {
    changed.run();
  }
}
