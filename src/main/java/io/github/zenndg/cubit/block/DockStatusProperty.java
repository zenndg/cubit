package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockStatus;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** The screen of a dock as a block state. The lowercase names are part of the saves. */
public enum DockStatusProperty implements StringRepresentable {
  OFF(DockStatus.OFF),
  IDLE(DockStatus.IDLE),
  CHARGING(DockStatus.CHARGING),
  COMPLETE(DockStatus.COMPLETE),
  NO_ENERGY(DockStatus.NO_ENERGY);

  private final DockStatus status;

  DockStatusProperty(DockStatus status) {
    this.status = status;
  }

  static DockStatusProperty of(DockStatus status) {
    for (DockStatusProperty property : values()) {
      if (property.status == status) {
        return property;
      }
    }
    return OFF;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  /** Shows the status in the block, only when it changed. */
  static void show(Level level, BlockPos pos, BlockState state, DockStatus status) {
    DockStatusProperty shown = of(status);
    if (state.getValue(InductionDockBlock.STATUS) != shown) {
      level.setBlock(pos, state.setValue(InductionDockBlock.STATUS, shown), Block.UPDATE_CLIENTS);
    }
  }
}
