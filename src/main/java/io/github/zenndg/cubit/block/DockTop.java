package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The third block of a dock, two blocks above its lower part: where the panel goes, reserved for
 * the dock (answers 1892, 1896 and 1897). It is always in the column of the dock, so its chunk is
 * the one of the dock, already loaded (rule 22).
 */
final class DockTop {
  private static final int ABOVE_THE_LOWER_PART = 2;

  /** How many blocks tall a dock is, with this one. */
  static final int DOCK_BLOCKS = ABOVE_THE_LOWER_PART + 1;

  private DockTop() {}

  static BlockPos of(BlockPos lower) {
    return lower.above(ABOVE_THE_LOWER_PART);
  }

  static BlockPos lowerOf(BlockPos top) {
    return top.below(ABOVE_THE_LOWER_PART);
  }

  static void place(Level level, BlockPos lower) {
    level.setBlock(of(lower), top().defaultBlockState(), Block.UPDATE_ALL);
  }

  /**
   * Whether the dock has its third block: there already, or set now in an empty space, for a dock
   * from before it had one. A taken space is never cleared, and the dock works without its panel
   * until the space is free (answer 1897).
   */
  static boolean claim(Level level, BlockPos lower) {
    BlockPos pos = of(lower);
    BlockState state = level.getBlockState(pos);
    if (state.is(top())) {
      return true;
    }
    if (!state.isAir() || pos.getY() >= level.getMaxBuildHeight()) {
      return false;
    }
    place(level, lower);
    return true;
  }

  /** The plate of the panel stands only with the panel deployed (answer 1898). */
  static void show(Level level, BlockPos lower, boolean deployed) {
    BlockPos pos = of(lower);
    BlockState state = level.getBlockState(pos);
    if (state.is(top()) && state.getValue(InductionDockTopBlock.DEPLOYED) != deployed) {
      level.setBlock(
          pos, state.setValue(InductionDockTopBlock.DEPLOYED, deployed), Block.UPDATE_ALL);
    }
  }

  private static Block top() {
    return CubitBlocks.INDUCTION_DOCK_TOP.get();
  }
}
