package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.dock.StoredKind;
import io.github.zenndg.cubit.item.EnergySlotItems;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/** The click with an item on a block that stores, as a dock or a server (answer 959). */
final class StorageClick {
  private StorageClick() {}

  /**
   * The click keeps only the Toolbox, the Power Bank and the Damaged Cubit, and redstone goes to
   * the input slot (959). Other items of the mod go in by the screen or a hopper (answer 1382).
   */
  static boolean put(ItemStack hand, IItemHandler open, SimpleContainer input) {
    StoredKind kind = StoredKinds.of(hand);
    if (kind == StoredKind.MOD_ITEM) {
      return false;
    }
    return DockStorageRules.accepts(kind) ? store(hand, open) : EnergySlotItems.insert(input, hand);
  }

  /** Joining the stacks and then the free slots of the tier. False when nothing fits. */
  private static boolean store(ItemStack hand, IItemHandler open) {
    if (!DockStorageRules.accepts(StoredKinds.of(hand))) {
      return false;
    }
    int before = hand.getCount();
    ItemStack rest = ItemHandlerHelper.insertItemStacked(open, hand.copy(), false);
    hand.setCount(rest.getCount());
    return rest.getCount() < before;
  }
}
