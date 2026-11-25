package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import io.github.zenndg.cubit.domain.network.ChunkRoster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

/**
 * The Network in the data of the world, so it is known also with its chunks unloaded: in this
 * delivery, the chunks it holds and the ones that wait for their owner. The names are a contract
 * (answers 2334 and 2407), and a broken entry is skipped (rule 17).
 */
public final class NetworkData extends SavedData {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DATA_NAME = "cubit_network";
  private static final String CHUNKS_KEY = "Chunks";
  private static final String OWNER_KEY = "Owner";
  private static final String DIMENSION_KEY = "Dimension";
  private static final String POS_KEY = "Pos";
  private static final String WAITING_KEY = "Waiting";
  private static final String SKIPPED = "Skipping a broken chunk of a network: {}";

  private final ChunkRoster<GlobalPos> chunks = new ChunkRoster<>();
  private final NetworkWake wake = new NetworkWake(this);

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

  NetworkWake wake() {
    return wake;
  }

  public static NetworkData load(CompoundTag tag, HolderLookup.Provider registries) {
    NetworkData data = new NetworkData();
    ListTag list = tag.getList(CHUNKS_KEY, Tag.TAG_COMPOUND);
    for (int index = 0; index < list.size(); index++) {
      data.restore(list.getCompound(index));
    }
    return data;
  }

  private void restore(CompoundTag entry) {
    DataResult<ResourceLocation> dimension = ResourceLocation.read(entry.getString(DIMENSION_KEY));
    if (!entry.hasUUID(OWNER_KEY)
        || dimension.isError()
        || !entry.contains(POS_KEY, Tag.TAG_LONG)) {
      LOGGER.warn(SKIPPED, entry);
      return;
    }
    ResourceKey<Level> level = ResourceKey.create(Registries.DIMENSION, dimension.getOrThrow());
    GlobalPos place = GlobalPos.of(level, BlockPos.of(entry.getLong(POS_KEY)));
    chunks.restore(
        new ChunkRoster.Entry<>(place, entry.getUUID(OWNER_KEY), entry.getBoolean(WAITING_KEY)));
  }

  @Override
  public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
    ListTag list = new ListTag();
    for (ChunkRoster.Entry<GlobalPos> entry : chunks.entries()) {
      CompoundTag saved = new CompoundTag();
      saved.putUUID(OWNER_KEY, entry.owner());
      saved.putString(DIMENSION_KEY, entry.place().dimension().location().toString());
      saved.putLong(POS_KEY, entry.place().pos().asLong());
      saved.putBoolean(WAITING_KEY, entry.waiting());
      list.add(saved);
    }
    tag.put(CHUNKS_KEY, list);
    return tag;
  }
}
