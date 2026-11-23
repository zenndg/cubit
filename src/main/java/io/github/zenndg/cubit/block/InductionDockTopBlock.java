package io.github.zenndg.cubit.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The third block of the Induction Dock, where the panel goes (answers 1892 and 1896 to 1898). The
 * dock below draws it. It is solid only with the panel deployed, as the thin plate of the panel,
 * and breaking it breaks the whole dock, with the rules of the dock.
 */
public final class InductionDockTopBlock extends Block {
  public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");
  private static final MapCodec<InductionDockTopBlock> CODEC =
      simpleCodec(InductionDockTopBlock::new);

  /** The panel lies near the middle of the third block, about 1 px thick (answer 1909). */
  private static final VoxelShape PANEL = Block.box(0, 4, 0, 16, 6, 16);

  public InductionDockTopBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(DEPLOYED, false));
  }

  @Override
  protected MapCodec<? extends Block> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(DEPLOYED);
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.INVISIBLE;
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return state.getValue(DEPLOYED) ? PANEL : Shapes.empty();
  }

  @Override
  protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
    return true;
  }

  /** It only exists over the upper part of a dock, as the upper part over the lower one. */
  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighbor,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    if (direction == Direction.DOWN && !isUpperDock(neighbor)) {
      return Blocks.AIR.defaultBlockState();
    }
    return state;
  }

  private static boolean isUpperDock(BlockState state) {
    return state.getBlock() instanceof InductionDockBlock
        && state.getValue(InductionDockBlock.HALF) == DoubleBlockHalf.UPPER;
  }

  @Override
  protected float getDestroyProgress(
      BlockState state, Player player, BlockGetter level, BlockPos pos) {
    BlockPos lower = DockTop.lowerOf(pos);
    return level.getBlockState(lower).getDestroyProgress(player, level, lower);
  }

  @Override
  public boolean onDestroyedByPlayer(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      boolean willHarvest,
      FluidState fluid) {
    BlockPos lower = DockTop.lowerOf(pos);
    return InductionDockBlock.mayBreak(level, lower, level.getBlockState(lower), player)
        && super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
  }

  /** The whole dock breaks, with its drops, as if the player broke its lower part (1896). */
  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    BlockPos lower = DockTop.lowerOf(pos);
    BlockState dock = level.getBlockState(lower);
    if (!level.isClientSide()
        && dock.getBlock() instanceof InductionDockBlock
        && InductionDockBlock.mayBreak(level, lower, dock, player)) {
      dock.getBlock().playerWillDestroy(level, lower, dock, player);
      level.destroyBlock(lower, !player.isCreative(), player);
    }
    return super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  public boolean canEntityDestroy(
      BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
    return false;
  }

  @Override
  public PushReaction getPistonPushReaction(BlockState state) {
    return PushReaction.BLOCK;
  }
}
