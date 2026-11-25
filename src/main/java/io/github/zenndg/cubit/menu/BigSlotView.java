package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.ServerSlots;
import java.util.function.BooleanSupplier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A slot of the Network Server in its screen, with up to 128 items. Whatever takes items out gets
 * at most a normal stack, and no swap ever puts more than one in the hand (answer 2368).
 */
final class BigSlotView extends Slot {
  private final ServerSlots slots;
  private final BooleanSupplier shown;

  BigSlotView(ServerSlots slots, int index, int x, int y, BooleanSupplier shown) {
    super(slots, index, x, y);
    this.slots = slots;
    this.shown = shown;
  }

  /** The container refuses what it does not keep, and a pile too large for a hand never swaps. */
  @Override
  public boolean mayPlace(ItemStack stack) {
    boolean swaps = isLarge() && !ItemStack.isSameItemSameComponents(getItem(), stack);
    return !swaps && slots.canPlaceItem(getContainerSlot(), stack);
  }

  boolean isLarge() {
    return getItem().getCount() > getItem().getMaxStackSize();
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return slots.getMaxStackSize(stack);
  }

  @Override
  public boolean isActive() {
    return shown.getAsBoolean() && slots.isOpen(getContainerSlot());
  }
}
