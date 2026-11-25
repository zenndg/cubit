package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.item.SavedBigStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.slf4j.Logger;

/**
 * The storage of a Network Server, for the items of the mod, with the Power Banks its battery
 * charges (answers 2263, 2267 and 2373). Each slot is saved as the item and its amount, apart.
 */
final class ServerStorage {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String ITEMS_KEY = "Items";
  private static final String REFUSED = "The server at {} cannot store {} of {}, so they drop.";

  private final IntSupplier tierSlots;
  private final ServerSlots slots;
  private final ServerItemHandler handler;
  private final List<ItemStack> refused = new ArrayList<>();

  ServerStorage(Runnable changed, IntSupplier tierSlots) {
    this.tierSlots = tierSlots;
    slots = new ServerSlots(changed, tierSlots, () -> CubitMod.CONFIG.network().slot());
    handler = new ServerItemHandler(slots);
  }

  ServerSlots container() {
    return slots;
  }

  IItemHandlerModifiable handler() {
    return handler;
  }

  /** How many slots have something, for the column of the front. */
  int used() {
    int used = 0;
    for (int index = 0; index < slots.getContainerSize(); index++) {
      used += slots.getItem(index).isEmpty() ? 0 : 1;
    }
    return used;
  }

  boolean insertByClick(ItemStack hand, SimpleContainer input) {
    return StorageClick.put(hand, new RangedWrapper(handler, 0, tierSlots.getAsInt()), input);
  }

  EnergyStock charge(EnergyStock stock, Energy rate, Runnable changed) {
    return PowerBankTransfer.chargeFirst(stock, slots, rate, changed);
  }

  void save(CompoundTag tag, HolderLookup.Provider registries) {
    ListTag list = new ListTag();
    contents().forEach(saved -> list.add(saved.save(registries)));
    tag.put(ITEMS_KEY, list);
  }

  void load(CompoundTag tag, HolderLookup.Provider registries, BlockPos pos) {
    ListTag list = tag.getList(ITEMS_KEY, Tag.TAG_COMPOUND);
    List<SavedBigStack> saved = new ArrayList<>();
    for (int entry = 0; entry < list.size(); entry++) {
      saved.add(SavedBigStack.load(list.getCompound(entry), registries));
    }
    restore(saved, pos);
  }

  List<SavedBigStack> contents() {
    List<SavedBigStack> saved = new ArrayList<>();
    for (int index = 0; index < slots.getContainerSize(); index++) {
      ItemStack stack = slots.getItem(index);
      if (!stack.isEmpty()) {
        saved.add(SavedBigStack.of(index, stack));
      }
    }
    return List.copyOf(saved);
  }

  void restore(List<SavedBigStack> saved, BlockPos pos) {
    slots.clearContent();
    refused.clear();
    saved.forEach(entry -> keep(entry, pos));
  }

  /** What the slot does not keep, from an edited save or config, drops beside it (rule 17). */
  private void keep(SavedBigStack saved, BlockPos pos) {
    ItemStack rest = saved.stack();
    if (rest.isEmpty()) {
      return;
    }
    boolean exists = saved.slot() >= 0 && saved.slot() < slots.getContainerSize();
    if (exists) {
      rest = handler.insertItem(saved.slot(), rest, false);
    }
    if (!rest.isEmpty()) {
      LOGGER.warn(REFUSED, pos, rest.getCount(), rest.getItem());
      refused.add(rest);
    }
  }

  /** The level is not there while loading, so the refused items drop on the next tick. */
  void dropRefused(Level level, BlockPos pos) {
    for (ItemStack stack : refused) {
      while (!stack.isEmpty()) {
        ItemStack normal = stack.split(stack.getMaxStackSize());
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), normal);
      }
    }
    refused.clear();
  }
}
