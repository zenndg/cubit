package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.network.FedDocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * The Docks an antenna feeds, all in its own chunk (answers 2588 and 2589), in its NBT as `Docks`
 * and in its item as `docks`. Placed again in another chunk, the antenna forgets them (2603).
 */
final class AntennaDocks {
  private static final String KEY = "Docks";
  private static final String POS_KEY = "Pos";

  private final Runnable changed;
  private List<BlockPos> docks = List.of();

  AntennaDocks(Runnable changed) {
    this.changed = changed;
  }

  boolean feeds(BlockPos dock) {
    return docks.contains(dock);
  }

  List<BlockPos> docks() {
    return docks;
  }

  /** True when it changed. */
  boolean feed(Level level, BlockPos dock) {
    return set(fed(level).with(LinkSpots.of(level, dock)));
  }

  boolean unfeed(Level level, BlockPos dock) {
    return set(fed(level).without(LinkSpots.of(level, dock)));
  }

  private FedDocks fed(Level level) {
    return new FedDocks(docks.stream().map(pos -> LinkSpots.of(level, pos)).toList());
  }

  private boolean set(FedDocks next) {
    List<BlockPos> kept = next.docks().stream().map(LinkSpots::pos).toList();
    if (kept.equals(docks)) {
      return false;
    }
    docks = kept;
    changed.run();
    return true;
  }

  List<Long> kept() {
    return docks.stream().map(BlockPos::asLong).toList();
  }

  /** Only the Docks of the chunk it stands in now come back. */
  void restore(List<Long> saved, BlockPos antenna) {
    ChunkPos chunk = new ChunkPos(antenna);
    docks =
        saved.stream()
            .map(BlockPos::of)
            .filter(dock -> new ChunkPos(dock).equals(chunk))
            .distinct()
            .toList();
  }

  void save(CompoundTag tag) {
    if (docks.isEmpty()) {
      return;
    }
    ListTag list = new ListTag();
    for (BlockPos dock : docks) {
      CompoundTag entry = new CompoundTag();
      entry.putLong(POS_KEY, dock.asLong());
      list.add(entry);
    }
    tag.put(KEY, list);
  }

  void load(CompoundTag tag) {
    List<BlockPos> loaded = new ArrayList<>();
    for (Tag entry : tag.getList(KEY, Tag.TAG_COMPOUND)) {
      CompoundTag saved = (CompoundTag) entry;
      if (saved.contains(POS_KEY, Tag.TAG_LONG)) {
        loaded.add(BlockPos.of(saved.getLong(POS_KEY)));
      }
    }
    docks = loaded.stream().distinct().toList();
  }
}
