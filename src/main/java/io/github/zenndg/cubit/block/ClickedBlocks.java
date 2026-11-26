package io.github.zenndg.cubit.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The block entity a click of the Tablet is about: a Dock and a block of the Network keep it in
 * their lower part, and their upper parts, with the third block of the Dock, answer for it.
 */
public final class ClickedBlocks {
  private ClickedBlocks() {}

  public static BlockEntity at(Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    if (state.getBlock() instanceof NetworkBlock) {
      return NetworkBlock.networkAt(level, pos, state);
    }
    if (state.getBlock() instanceof InductionDockTopBlock) {
      BlockPos lower = DockTop.lowerOf(pos);
      return InductionDockBlock.dockAt(level, lower, level.getBlockState(lower));
    }
    if (state.getBlock() instanceof InductionDockBlock) {
      return InductionDockBlock.dockAt(level, pos, state);
    }
    return level.getBlockEntity(pos);
  }
}
