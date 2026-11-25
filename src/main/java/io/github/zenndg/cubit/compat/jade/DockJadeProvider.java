package io.github.zenndg.cubit.compat.jade;

import io.github.zenndg.cubit.block.InductionDockBlock;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.energy.EnergyReading;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.menu.ScreenKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * The name, the stock, the panel and the status of the screen of an Induction Dock in Jade (answers
 * 662, 1865 and 1870). The name, the stock and the panel are only on the server, and the status is
 * in the block state the client already has.
 */
enum DockJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
  INSTANCE;

  private static final String STOCK = "Stock";
  private static final String CAPACITY = "Capacity";
  private static final String SOLAR = "Solar";
  private static final String MOONLIGHT = "Moonlight";
  private static final String NAME = "Name";
  private static final String PANEL = "Panel";
  private static final String SEPARATOR = ": ";

  @Override
  public void appendServerData(CompoundTag data, BlockAccessor accessor) {
    if (accessor.getBlockEntity() instanceof InductionDockBlockEntity dock) {
      data.putLong(STOCK, dock.energy().amount());
      data.putLong(CAPACITY, dock.capacity().amount());
      data.putLong(SOLAR, dock.solar().given().amount());
      data.putBoolean(MOONLIGHT, dock.solar().state() == SunState.MOONLIGHT);
      data.putString(NAME, dock.identity().name().value());
      data.putInt(PANEL, dock.solar().panel().ordinal());
    }
  }

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    CompoundTag data = accessor.getServerData();
    if (data.contains(NAME)) {
      tooltip.add(Component.literal(data.getString(NAME)));
    }
    if (data.contains(STOCK) && data.contains(CAPACITY)) {
      tooltip.add(
          JadeEnergyText.stock(EnergyReading.capped(data.getLong(STOCK), data.getLong(CAPACITY))));
    }
    if (data.contains(SOLAR)) {
      tooltip.add(solarLine(data.getLong(SOLAR), data.getBoolean(MOONLIGHT)));
    }
    if (data.contains(PANEL)) {
      tooltip.add(panelLine(data.getInt(PANEL)));
    }
    BlockState state = accessor.getBlockState();
    if (state.hasProperty(InductionDockBlock.TIER)) {
      RobotTier tier = state.getValue(InductionDockBlock.TIER).tier();
      tooltip.add(Component.translatable(JadeKeys.TIER, TierLook.name(tier)));
    }
    if (state.hasProperty(InductionDockBlock.STATUS)) {
      String status = state.getValue(InductionDockBlock.STATUS).getSerializedName();
      tooltip.add(Component.translatable(JadeKeys.dockStatus(status)));
    }
  }

  /** Under the moon the line has the name of its state (answer 1948). */
  private static Component solarLine(long solar, boolean moonlight) {
    if (solar == 0) {
      return Component.translatable(ScreenKeys.DOCK_NO_SUN);
    }
    String key = moonlight ? ScreenKeys.DOCK_MOONLIGHT : ScreenKeys.DOCK_SOLAR;
    return Component.translatable(key, EnergyFormat.rate(new Energy(solar)));
  }

  /** An unknown state, as from a newer server, shows the panel in the cabinet. */
  private static Component panelLine(int ordinal) {
    DockPanelState[] states = DockPanelState.values();
    DockPanelState state =
        ordinal >= 0 && ordinal < states.length ? states[ordinal] : DockPanelState.STORED;
    return Component.translatable(ScreenKeys.DOCK_SOLAR_PANEL)
        .append(SEPARATOR)
        .append(Component.translatable(ScreenKeys.dockPanel(state)));
  }

  @Override
  public ResourceLocation getUid() {
    return JadeKeys.DOCK_UID;
  }
}
