package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * The Servers and the antennas of the Network in the data of the world, the lists {@code Servers}
 * and {@code Antennas} (Delivery 4, detail 19). The names are a contract, the keys are new and
 * optional, and a broken entry is skipped (rule 17).
 */
final class SavedRoster {
  static final String OWNER_KEY = "Owner";
  static final String DIMENSION_KEY = "Dimension";
  static final String POS_KEY = "Pos";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String SERVERS_KEY = "Servers";
  private static final String ANTENNAS_KEY = "Antennas";
  private static final String ID_KEY = "Id";
  private static final String TIER_KEY = "Tier";
  private static final String SERVER_KEY = "Server";
  private static final String CHANNEL_KEY = "Channel";
  private static final String SKIPPED = "Skipping a broken entry of a network: {}";

  private SavedRoster() {}

  static void load(CompoundTag tag, NetworkRoster<GlobalPos> roster) {
    ListTag servers = tag.getList(SERVERS_KEY, Tag.TAG_COMPOUND);
    for (int index = 0; index < servers.size(); index++) {
      server(servers.getCompound(index)).ifPresent(roster::putServer);
    }
    ListTag antennas = tag.getList(ANTENNAS_KEY, Tag.TAG_COMPOUND);
    for (int index = 0; index < antennas.size(); index++) {
      antenna(antennas.getCompound(index)).ifPresent(roster::putAntenna);
    }
  }

  static void save(CompoundTag tag, NetworkRoster<GlobalPos> roster) {
    ListTag servers = new ListTag();
    for (NetworkRoster.Server<GlobalPos> server : roster.servers()) {
      CompoundTag saved = new CompoundTag();
      saved.putUUID(ID_KEY, server.id());
      saved.putUUID(OWNER_KEY, server.owner());
      putPlace(saved, server.place());
      saved.putString(TIER_KEY, server.tier().key());
      servers.add(saved);
    }
    tag.put(SERVERS_KEY, servers);
    ListTag antennas = new ListTag();
    for (NetworkRoster.Antenna<GlobalPos> antenna : roster.antennas()) {
      CompoundTag saved = new CompoundTag();
      saved.putUUID(OWNER_KEY, antenna.owner());
      putPlace(saved, antenna.place());
      saved.putUUID(SERVER_KEY, antenna.server());
      saved.putString(CHANNEL_KEY, antenna.channel().key());
      antennas.add(saved);
    }
    tag.put(ANTENNAS_KEY, antennas);
  }

  private static Optional<NetworkRoster.Server<GlobalPos>> server(CompoundTag entry) {
    Optional<GlobalPos> place = place(entry);
    Optional<RobotTier> tier = RobotTier.fromKey(entry.getString(TIER_KEY));
    if (!entry.hasUUID(ID_KEY) || !entry.hasUUID(OWNER_KEY) || place.isEmpty() || tier.isEmpty()) {
      LOGGER.warn(SKIPPED, entry);
      return Optional.empty();
    }
    return Optional.of(
        new NetworkRoster.Server<>(
            entry.getUUID(ID_KEY), entry.getUUID(OWNER_KEY), place.get(), tier.get()));
  }

  private static Optional<NetworkRoster.Antenna<GlobalPos>> antenna(CompoundTag entry) {
    Optional<GlobalPos> place = place(entry);
    Optional<AntennaChannel> channel = AntennaChannel.of(entry.getString(CHANNEL_KEY));
    if (!entry.hasUUID(OWNER_KEY)
        || !entry.hasUUID(SERVER_KEY)
        || place.isEmpty()
        || channel.isEmpty()) {
      LOGGER.warn(SKIPPED, entry);
      return Optional.empty();
    }
    return Optional.of(
        new NetworkRoster.Antenna<>(
            place.get(), entry.getUUID(OWNER_KEY), entry.getUUID(SERVER_KEY), channel.get()));
  }

  /** The dimension and the position of an entry, or nothing when one of them is broken. */
  static Optional<GlobalPos> place(CompoundTag entry) {
    DataResult<ResourceLocation> dimension = ResourceLocation.read(entry.getString(DIMENSION_KEY));
    if (dimension.isError() || !entry.contains(POS_KEY, Tag.TAG_LONG)) {
      return Optional.empty();
    }
    ResourceKey<Level> level = ResourceKey.create(Registries.DIMENSION, dimension.getOrThrow());
    return Optional.of(GlobalPos.of(level, BlockPos.of(entry.getLong(POS_KEY))));
  }

  static void putPlace(CompoundTag entry, GlobalPos place) {
    entry.putString(DIMENSION_KEY, place.dimension().location().toString());
    entry.putLong(POS_KEY, place.pos().asLong());
  }
}
