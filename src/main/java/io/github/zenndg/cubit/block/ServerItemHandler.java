package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.network.BigSlot;
import io.github.zenndg.cubit.item.PowerBankItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * How the automation sees the slots of a Network Server: the items of the Dock go in by any face,
 * up to the limit of the slot, and leave as the rules of the Dock release them (answer 2263).
 */
final class ServerItemHandler implements IItemHandlerModifiable {
  private final ServerSlots slots;

  ServerItemHandler(ServerSlots slots) {
    this.slots = slots;
  }

  @Override
  public int getSlots() {
    return slots.getContainerSize();
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return slots.getItem(index);
  }

  @Override
  public void setStackInSlot(int index, ItemStack stack) {
    slots.setItem(index, stack);
  }

  @Override
  public int getSlotLimit(int index) {
    return rule().limit();
  }

  @Override
  public boolean isItemValid(int index, ItemStack stack) {
    return slots.canPlaceItem(index, stack);
  }

  @Override
  public ItemStack insertItem(int index, ItemStack stack, boolean simulate) {
    ItemStack stored = slots.getItem(index);
    boolean joins = stored.isEmpty() || ItemStack.isSameItemSameComponents(stored, stack);
    if (stack.isEmpty() || !joins || !slots.canPlaceItem(index, stack)) {
      return stack;
    }
    int taken = rule().takes(stored.getCount(), stack.getCount(), stack.getMaxStackSize());
    if (!simulate && taken > 0) {
      slots.setItem(index, stack.copyWithCount(stored.getCount() + taken));
    }
    return stack.copyWithCount(stack.getCount() - taken);
  }

  @Override
  public ItemStack extractItem(int index, int amount, boolean simulate) {
    ItemStack stored = slots.getItem(index);
    boolean full = PowerBankItem.is(stored) && PowerBankItem.batteryOf(stored).isFull();
    if (amount <= 0 || !DockStorageRules.releases(StoredKinds.of(stored), full)) {
      return ItemStack.EMPTY;
    }
    int given = rule().gives(stored.getCount(), amount, stored.getMaxStackSize());
    return simulate ? stored.copyWithCount(given) : slots.removeItem(index, given);
  }

  private static BigSlot rule() {
    return CubitMod.CONFIG.network().slot();
  }
}
