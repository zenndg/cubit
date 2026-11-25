package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import java.util.function.IntSupplier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * The slots of a Network Server, each with up to 128 items of the mod, and one of an item that does
 * not stack. Whatever takes items out gets at most a normal stack at a time, so no stack above it
 * ever reaches a hand or the ground (answers 2263 and 2368). The container always has the slots of
 * the Ultimate, and the tier opens the first ones.
 */
final class ServerSlots extends SimpleContainer {
  private final Runnable changed;
  private final IntSupplier tierSlots;

  ServerSlots(Runnable changed, IntSupplier tierSlots) {
    super(ServerSpec.MAX_SLOTS);
    this.changed = changed;
    this.tierSlots = tierSlots;
  }

  @Override
  public int getMaxStackSize() {
    return CubitMod.CONFIG.network().slot().limit();
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return CubitMod.CONFIG.network().slot().keeps(stack.getMaxStackSize());
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    return super.removeItem(index, Math.min(count, getItem(index).getMaxStackSize()));
  }

  @Override
  public boolean canPlaceItem(int index, ItemStack stack) {
    return DockStorageRules.opens(index, tierSlots.getAsInt())
        && DockStorageRules.accepts(StoredKinds.of(stack));
  }

  @Override
  public void setChanged() {
    changed.run();
  }
}
