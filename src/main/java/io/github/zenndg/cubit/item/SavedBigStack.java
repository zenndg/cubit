package io.github.zenndg.cubit.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.cubit.domain.network.BigSlot;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * A slot of the Network Server in a save: the item, with one, and how many apart, because the game
 * only saves stacks from 1 to 99. A contract with the saves (answers 2368 and 2410).
 */
public record SavedBigStack(int slot, ItemStack item, int amount) {
  private static final String SLOT_FIELD = "slot";
  private static final String ITEM_FIELD = "item";
  private static final String AMOUNT_FIELD = "amount";
  private static final String SLOT_KEY = "Slot";
  private static final String AMOUNT_KEY = "Amount";

  public static final Codec<SavedBigStack> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  Codec.INT.fieldOf(SLOT_FIELD).forGetter(SavedBigStack::slot),
                  ItemStack.SINGLE_ITEM_CODEC.fieldOf(ITEM_FIELD).forGetter(SavedBigStack::item),
                  Codec.INT.fieldOf(AMOUNT_FIELD).forGetter(SavedBigStack::amount))
              .apply(instance, SavedBigStack::new));

  /** A broken amount never becomes a flood of items in the world (rule 17). */
  public SavedBigStack {
    item = item.copyWithCount(1);
    amount = Math.min(Math.max(amount, 0), BigSlot.MAX_LIMIT);
  }

  public static SavedBigStack of(int slot, ItemStack stack) {
    return new SavedBigStack(slot, stack, stack.getCount());
  }

  public static SavedBigStack load(CompoundTag tag, HolderLookup.Provider registries) {
    ItemStack item = ItemStack.parse(registries, tag).orElse(ItemStack.EMPTY);
    return new SavedBigStack(tag.getInt(SLOT_KEY), item, tag.getInt(AMOUNT_KEY));
  }

  public CompoundTag save(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    tag.putInt(SLOT_KEY, slot);
    tag.putInt(AMOUNT_KEY, amount);
    return (CompoundTag) item.save(registries, tag);
  }

  public ItemStack stack() {
    return item.copyWithCount(amount);
  }

  /** A stack has no equality of its own, and a component needs one. */
  @Override
  public boolean equals(Object other) {
    return other instanceof SavedBigStack saved
        && slot == saved.slot
        && amount == saved.amount
        && ItemStack.isSameItemSameComponents(item, saved.item);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slot, amount, ItemStack.hashItemAndComponents(item));
  }
}
