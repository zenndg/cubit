package io.github.zenndg.cubit.compat.jade;

import io.github.zenndg.cubit.block.EngineeringDeskBlock;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.block.OwnerName;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.energy.EnergyReading;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

/**
 * The Engineering Desk in Jade: the progress of the craft with the item it delivers, the order, the
 * energy, the tier, the owner and, in a pause, its cause (answers 1468 and 1472). All but the tier
 * live on the server, and the tier is in the block state the client already has.
 */
enum DeskJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
  INSTANCE;

  private static final String PROGRESS = "Progress";
  private static final String RESULT = "Result";
  private static final String DONE = "Done";
  private static final String TOTAL = "Total";
  private static final String STOCK = "Stock";
  private static final String CAPACITY = "Capacity";
  private static final String OWNER = "Owner";
  private static final String STATUS = "Status";
  private static final float PERCENT = 100f;

  @Override
  public void appendServerData(CompoundTag data, BlockAccessor accessor) {
    BlockState state = accessor.getBlockState();
    EngineeringDeskBlockEntity desk =
        EngineeringDeskBlock.deskAt(accessor.getLevel(), accessor.getPosition(), state);
    if (desk == null) {
      return;
    }
    HolderLookup.Provider registries = accessor.getLevel().registryAccess();
    data.putInt(PROGRESS, desk.progressPercent());
    ItemStack result = desk.shownResult();
    if (!result.isEmpty()) {
      data.put(RESULT, result.save(registries));
    }
    data.putInt(DONE, desk.order().done());
    data.putInt(TOTAL, desk.order().total());
    data.putLong(STOCK, desk.energy().amount());
    data.putLong(CAPACITY, desk.capacity().amount());
    data.putString(STATUS, desk.status().name());
    desk.owner()
        .flatMap(owner -> OwnerName.of(accessor.getLevel().getServer(), owner, desk.getBlockPos()))
        .ifPresent(name -> data.putString(OWNER, name));
  }

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    CompoundTag data = accessor.getServerData();
    appendProgress(tooltip, accessor, data);
    if (data.getInt(TOTAL) > 0) {
      tooltip.add(
          Component.translatable(JadeKeys.DESK_ORDER, data.getInt(DONE), data.getInt(TOTAL)));
    }
    if (data.contains(STOCK) && data.contains(CAPACITY)) {
      tooltip.add(
          JadeEnergyText.stock(EnergyReading.capped(data.getLong(STOCK), data.getLong(CAPACITY))));
    }
    BlockState state = accessor.getBlockState();
    if (state.hasProperty(EngineeringDeskBlock.TIER)) {
      tooltip.add(JadeTierLine.of(state.getValue(EngineeringDeskBlock.TIER).tier()));
    }
    if (data.contains(OWNER)) {
      tooltip.add(Component.translatable(JadeKeys.OWNER, data.getString(OWNER)));
    }
    statusOf(data.getString(STATUS))
        .filter(DeskStatus::isPaused)
        .ifPresent(status ->
            tooltip.add(
                Component.translatable(
                    JadeKeys.DESK_PAUSED, Component.translatable(DeskScreenKeys.status(status)))));
  }

  /** The arrow of the progress and the item that comes out, as Jade shows a furnace. */
  private static void appendProgress(ITooltip tooltip, BlockAccessor accessor, CompoundTag data) {
    if (!data.contains(RESULT)) {
      return;
    }
    ItemStack result =
        ItemStack.parseOptional(accessor.getLevel().registryAccess(), data.getCompound(RESULT));
    IElementHelper elements = IElementHelper.get();
    tooltip.add(elements.progress(data.getInt(PROGRESS) / PERCENT));
    tooltip.append(elements.item(result));
  }

  private static Optional<DeskStatus> statusOf(String name) {
    return Arrays.stream(DeskStatus.values())
        .filter(status -> status.name().equals(name))
        .findFirst();
  }

  @Override
  public ResourceLocation getUid() {
    return JadeKeys.DESK_UID;
  }
}
