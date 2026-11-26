package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.network.ChunkRoster;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

/**
 * The Network in the data of the world, so it is known also with its chunks unloaded: the chunks it
 * holds and the ones that wait for their owner, the Servers and the antennas, with their channels,
 * and the blocks the operator released while unloaded. The names are a contract (answers 2334 and
 * 2413), and a broken entry is skipped (rule 17).
 */
public final class NetworkData extends SavedData {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DATA_NAME = "cubit_network";
  private static final String CHUNKS_KEY = "Chunks";
  private static final String WAITING_KEY = "Waiting";
  private static final String RELEASED_KEY = "Released";
  private static final String SKIPPED = "Skipping a broken chunk of a network: {}";

  private final ChunkRoster<GlobalPos> chunks = new ChunkRoster<>();
  private final NetworkRoster<GlobalPos> roster = new NetworkRoster<>();
  private final NetworkWake wake = new NetworkWake(this);
  private final Set<GlobalPos> released = new LinkedHashSet<>();

  public static NetworkData of(MinecraftServer server) {
    return server
        .overworld()
        .getDataStorage()
        .computeIfAbsent(new Factory<>(NetworkData::new, NetworkData::load), DATA_NAME);
  }

  /** Call {@link #setDirty()} after a change. */
  public ChunkRoster<GlobalPos> chunks() {
    return chunks;
  }

  /** The Servers and the antennas. Call {@link #setDirty()} after a change. */
  public NetworkRoster<GlobalPos> roster() {
    return roster;
  }

  NetworkWake wake() {
    return wake;
  }

  /**
   * A block released by the operator while unloaded switches its chunk off when it loads (answer
   * 2578). Call {@link #setDirty()} after a change.
   */
  void markReleased(GlobalPos place) {
    released.add(place);
  }

  /** True once, for a block that was released while unloaded. */
  boolean takeReleased(GlobalPos place) {
    return released.remove(place);
  }

  public static NetworkData load(CompoundTag tag, HolderLookup.Provider registries) {
    NetworkData data = new NetworkData();
    ListTag list = tag.getList(CHUNKS_KEY, Tag.TAG_COMPOUND);
    for (int index = 0; index < list.size(); index++) {
      data.restore(list.getCompound(index));
    }
    SavedRoster.load(tag, data.roster);
    ListTag marks = tag.getList(RELEASED_KEY, Tag.TAG_COMPOUND);
    for (int index = 0; index < marks.size(); index++) {
      SavedRoster.place(marks.getCompound(index)).ifPresent(data.released::add);
    }
    return data;
  }

  private void restore(CompoundTag entry) {
    Optional<GlobalPos> place = SavedRoster.place(entry);
    if (!entry.hasUUID(SavedRoster.OWNER_KEY) || place.isEmpty()) {
      LOGGER.warn(SKIPPED, entry);
      return;
    }
    chunks.restore(
        new ChunkRoster.Entry<>(
            place.get(), entry.getUUID(SavedRoster.OWNER_KEY), entry.getBoolean(WAITING_KEY)));
  }

  @Override
  public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
    ListTag list = new ListTag();
    for (ChunkRoster.Entry<GlobalPos> entry : chunks.entries()) {
      CompoundTag saved = new CompoundTag();
      saved.putUUID(SavedRoster.OWNER_KEY, entry.owner());
      SavedRoster.putPlace(saved, entry.place());
      saved.putBoolean(WAITING_KEY, entry.waiting());
      list.add(saved);
    }
    tag.put(CHUNKS_KEY, list);
    SavedRoster.save(tag, roster);
    ListTag marks = new ListTag();
    for (GlobalPos place : released) {
      CompoundTag saved = new CompoundTag();
      SavedRoster.putPlace(saved, place);
      marks.add(saved);
    }
    tag.put(RELEASED_KEY, marks);
    return tag;
  }
}
