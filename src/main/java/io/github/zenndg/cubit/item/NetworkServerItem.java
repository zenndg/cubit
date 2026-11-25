package io.github.zenndg.cubit.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * The item of the Network Server, with all it keeps. With items or energy it never goes into
 * another container, and empty it does (Delivery 3, detail 16).
 */
public final class NetworkServerItem extends NetworkItem {
  public NetworkServerItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public boolean canFitInsideContainerItems(ItemStack stack) {
    return !SavedNetwork.of(stack).holdsAnything();
  }
}
