package io.github.zenndg.cubit.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * The items of a plan, out of the reach of players and automation (answer 2007). It has no fixed
 * size, because many results do not stack (answer 2028).
 */
final class CascadeStock {
  private static final int SAVED_TOGETHER = 128;

  private final List<ItemStack> stacks = new ArrayList<>();

  void add(ItemStack added) {
    ItemStack left = added.copy();
    for (ItemStack present : stacks) {
      if (ItemStack.isSameItemSameComponents(present, left)) {
        int moved = Math.min(present.getMaxStackSize() - present.getCount(), left.getCount());
        present.grow(moved);
        left.shrink(moved);
      }
    }
    if (!left.isEmpty()) {
      stacks.add(left);
    }
  }

  boolean isEmpty() {
    return stacks.isEmpty();
  }

  /** The same stacks as slots: what leaves them leaves the stock. */
  Container view() {
    return new SimpleContainer(stacks.toArray(ItemStack[]::new));
  }

  int size() {
    return stacks.size();
  }

  ItemStack first() {
    return stacks.getFirst();
  }

  /** After taking from the stacks, the empty ones leave the list. */
  void prune() {
    stacks.removeIf(ItemStack::isEmpty);
  }

  void moveTo(CascadeStock other) {
    stacks.forEach(other::add);
    stacks.clear();
  }

  /** In parts, because the saved contents of the game keep few stacks each. */
  List<ItemContainerContents> saved() {
    List<ItemContainerContents> parts = new ArrayList<>();
    for (int from = 0; from < stacks.size(); from += SAVED_TOGETHER) {
      int to = Math.min(from + SAVED_TOGETHER, stacks.size());
      parts.add(ItemContainerContents.fromItems(stacks.subList(from, to)));
    }
    return parts;
  }

  void load(List<ItemContainerContents> parts) {
    stacks.clear();
    for (ItemContainerContents part : parts) {
      NonNullList<ItemStack> loaded = NonNullList.withSize(part.getSlots(), ItemStack.EMPTY);
      part.copyInto(loaded);
      loaded.stream().filter(stack -> !stack.isEmpty()).forEach(stacks::add);
    }
  }
}
