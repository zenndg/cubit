package io.github.zenndg.cubit.compat.jade;

import io.github.zenndg.cubit.block.NetworkBlock;
import io.github.zenndg.cubit.block.NetworkBlockEntity;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.EnergyReading;
import io.github.zenndg.cubit.menu.NetworkKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * The blocks of the Network in Jade, short: the name, the battery, the tier, the switch of the
 * chunk of the server and whether they are paired (answer 2285).
 */
enum NetworkJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
  INSTANCE;

  private static final String NAME = "Name";
  private static final String ENERGY = "Energy";
  private static final String CAPACITY = "Capacity";
  private static final String PAIRED = "Paired";
  private static final String CHUNK = "Chunk";

  @Override
  public void appendServerData(CompoundTag data, BlockAccessor accessor) {
    if (!(accessor.getBlockEntity() instanceof NetworkBlockEntity block)) {
      return;
    }
    data.putString(NAME, block.identity().name().value());
    data.putLong(ENERGY, block.energy().amount());
    data.putLong(CAPACITY, block.capacity().amount());
    if (block instanceof NetworkServerBlockEntity server) {
      data.putBoolean(CHUNK, server.chunkLoading());
      data.putBoolean(PAIRED, server.relay().isPresent());
    } else if (block instanceof SolarRelayBlockEntity relay) {
      data.putBoolean(PAIRED, relay.server().isPresent());
    }
  }

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    CompoundTag data = accessor.getServerData();
    if (!data.contains(NAME)) {
      return;
    }
    tooltip.add(Component.literal(data.getString(NAME)));
    tooltip.add(
        JadeEnergyText.stock(EnergyReading.capped(data.getLong(ENERGY), data.getLong(CAPACITY))));
    if (accessor.getBlockState().hasProperty(NetworkBlock.TIER)) {
      tooltip.add(JadeTierLine.of(accessor.getBlockState().getValue(NetworkBlock.TIER).tier()));
    }
    if (data.contains(CHUNK)) {
      tooltip.add(
          Component.translatable(data.getBoolean(CHUNK) ? JadeKeys.CHUNK_ON : JadeKeys.CHUNK_OFF));
    }
    if (data.contains(PAIRED)) {
      tooltip.add(
          Component.translatable(data.getBoolean(PAIRED) ? JadeKeys.PAIRED : NetworkKeys.NO_PAIR));
    }
  }

  @Override
  public ResourceLocation getUid() {
    return JadeKeys.NETWORK_UID;
  }
}
