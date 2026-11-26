package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.desk.DeskOrder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

/**
 * The Disassemble tab of a desk: its own slot and the units asked, counted as an order, so the
 * comparator reads what is left as in an order (answers 2527 and 2532). The progress is the one of
 * the craft of the desk, because only one work runs at a time (answer 2528).
 */
public final class DeskDisassembly {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String KEY = "Disassembly";
  private static final String BAD_SAVE = "The desk at {} has an invalid disassembly: {}";
  private static final String UNSAVED = "The disassembly of a desk could not be saved: {}";
  private static final int SLOT = 0;

  private final SimpleContainer slot;
  private DeskOrder units = DeskOrder.NONE;

  DeskDisassembly(Runnable changed) {
    slot =
        new SimpleContainer(1) {
          @Override
          public void setChanged() {
            changed.run();
          }
        };
  }

  public SimpleContainer slot() {
    return slot;
  }

  public ItemStack item() {
    return slot.getItem(SLOT);
  }

  public boolean isRunning() {
    return units.isActive();
  }

  public DeskOrder units() {
    return units;
  }

  void start(int count) {
    units = new DeskOrder(count, 0);
  }

  void cancel() {
    units = DeskOrder.NONE;
  }

  /** One unit is done: the items of it leave the slot, and an empty slot ends the work. */
  void unitDone(int yield) {
    slot.removeItem(SLOT, yield);
    units = item().isEmpty() ? DeskOrder.NONE : units.crafted();
  }

  Optional<SavedDisassembly> saved() {
    if (item().isEmpty() && !isRunning()) {
      return Optional.empty();
    }
    return Optional.of(new SavedDisassembly(item().copy(), units.total(), units.done()));
  }

  void use(Optional<SavedDisassembly> saved) {
    slot.setItem(SLOT, saved.map(SavedDisassembly::item).orElse(ItemStack.EMPTY));
    units = saved.map(SavedDisassembly::units).orElse(DeskOrder.NONE);
  }

  void save(CompoundTag tag, HolderLookup.Provider registries) {
    saved()
        .ifPresent(found ->
            SavedDisassembly.CODEC
                .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), found)
                .ifSuccess(encoded -> tag.put(KEY, encoded))
                .ifError(error -> LOGGER.error(UNSAVED, error.message())));
  }

  void load(CompoundTag tag, HolderLookup.Provider registries, BlockPos pos) {
    if (!tag.contains(KEY)) {
      use(Optional.empty());
      return;
    }
    use(
        SavedDisassembly.CODEC
            .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(KEY))
            .resultOrPartial(error -> LOGGER.warn(BAD_SAVE, pos, error)));
  }
}
