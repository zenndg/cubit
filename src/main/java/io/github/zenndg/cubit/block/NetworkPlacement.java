package io.github.zenndg.cubit.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Why a block of the Network cannot be placed: another one in the chunk, of any owner, no solid top
 * below or no room above (answers 2255 and 2271), after what is only of its kind, as the link of an
 * antenna.
 */
public final class NetworkPlacement {
  public static final String TAKEN_KEY = "notice.cubit.network_taken";
  public static final String NEEDS_SUPPORT_KEY = "notice.cubit.network_needs_support";
  public static final String NEEDS_ROOM_KEY = "notice.cubit.network_needs_room";

  private NetworkPlacement() {}

  public static Optional<Component> refusal(Block block, BlockPlaceContext context) {
    Optional<Component> own =
        block instanceof NetworkBlock network ? network.ownRefusal(context) : Optional.empty();
    return own.isPresent() ? own : common(block, context);
  }

  private static Optional<Component> common(Block block, BlockPlaceContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    if (NetworkChunk.has(level, pos, block)) {
      return Optional.of(Component.translatable(TAKEN_KEY, block.getName()));
    }
    BlockPos below = pos.below();
    if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
      return Optional.of(Component.translatable(NEEDS_SUPPORT_KEY, block.getName()));
    }
    return roomAt(context, pos.above())
        ? Optional.empty()
        : Optional.of(Component.translatable(NEEDS_ROOM_KEY, block.getName()));
  }

  private static boolean roomAt(BlockPlaceContext context, BlockPos pos) {
    Level level = context.getLevel();
    return pos.getY() < level.getMaxBuildHeight()
        && level.getBlockState(pos).canBeReplaced(context);
  }
}
