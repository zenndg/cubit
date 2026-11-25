package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockRemoval;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.SavedNetwork;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A block of the Network, two blocks tall, like the Dock: the lower half holds everything, and the
 * upper one only keeps the space. One of each per chunk, placed on a solid top, and only its owner
 * uses and breaks it (answers 2255, 2259 and 2271).
 */
public abstract class NetworkBlock extends BaseEntityBlock {
  private static final String TIER_NAME = "tier";
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
  public static final EnumProperty<TierProperty> TIER =
      EnumProperty.create(TIER_NAME, TierProperty.class);
  private static final float CANNOT_BREAK = 0f;
  private static final int OPERATOR_LEVEL = 2;
  private static final int REMOVE_WITHOUT_DROPS = 35;

  NetworkBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(HALF, DoubleBlockHalf.LOWER)
            .setValue(TIER, TierProperty.BASE));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, HALF, TIER);
  }

  static boolean isLower(BlockState state) {
    return state.getValue(HALF) == DoubleBlockHalf.LOWER;
  }

  /** The block entity lives in the lower half, and the upper one answers for it. */
  public static NetworkBlockEntity networkAt(BlockGetter level, BlockPos pos, BlockState state) {
    BlockPos lower = isLower(state) ? pos : pos.below();
    return level.getBlockEntity(lower) instanceof NetworkBlockEntity network ? network : null;
  }

  /** Both halves show the tier, which picks the band and the body. */
  static void showTier(Level level, BlockPos lower, RobotTier tier) {
    TierProperty property = TierProperty.of(tier);
    for (BlockPos pos : new BlockPos[] {lower, lower.above()}) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() instanceof NetworkBlock && state.getValue(TIER) != property) {
        level.setBlock(pos, state.setValue(TIER, property), UPDATE_ALL);
      }
    }
  }

  /** GeckoLib draws the whole block from its lower half. */
  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return isLower(state) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
  }

  /** The item tells the player why, and nothing is placed. */
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return NetworkPlacement.refusal(this, context).isPresent()
        ? null
        : defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  /** The tier of the item. The relay keeps none, and takes the one of its server. */
  @Override
  public void setPlacedBy(
      Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    BlockState placed = state.setValue(TIER, TierProperty.of(SavedNetwork.of(stack).tier()));
    level.setBlock(pos, placed, UPDATE_ALL);
    level.setBlock(pos.above(), placed.setValue(HALF, DoubleBlockHalf.UPPER), UPDATE_ALL);
    if (placer instanceof Player player
        && level.getBlockEntity(pos) instanceof NetworkBlockEntity network) {
      network.setOwner(player);
    }
    BlockNaming.placed(level, pos, placer, stack);
  }

  /** Each half only exists with the other, as in a door. */
  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighbor,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    Direction other = isLower(state) ? Direction.UP : Direction.DOWN;
    if (direction == other
        && !(neighbor.is(this) && neighbor.getValue(HALF) != state.getValue(HALF))) {
      return Blocks.AIR.defaultBlockState();
    }
    return state;
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    NetworkBlockEntity network = networkAt(level, pos, state);
    boolean used =
        !level.isClientSide()
            && network != null
            && network.isOwner(player)
            && network.click(player, stack);
    return used
        ? ItemInteractionResult.CONSUME
        : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  /** As the Dock: only the owner, or an operator in creative (Delivery 3, detail 3). */
  private static boolean mayBreak(
      BlockGetter level, BlockPos pos, BlockState state, Player player) {
    NetworkBlockEntity network = networkAt(level, pos, state);
    return DockRemoval.allows(
        player.isCreative(),
        player.hasPermissions(OPERATOR_LEVEL),
        network == null ? Optional.empty() : network.owner(),
        player.getUUID());
  }

  @Override
  protected float getDestroyProgress(
      BlockState state, Player player, BlockGetter level, BlockPos pos) {
    return mayBreak(level, pos, state, player)
        ? super.getDestroyProgress(state, player, level, pos)
        : CANNOT_BREAK;
  }

  /** The creative mode ignores the destroy progress, so the rule is checked here as well. */
  @Override
  public boolean onDestroyedByPlayer(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      boolean willHarvest,
      FluidState fluid) {
    return mayBreak(level, pos, state, player)
        && super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
  }

  /** A player in creative gets no drops, and what the block keeps must not be lost. */
  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    NetworkBlockEntity network = networkAt(level, pos, state);
    if (!level.isClientSide()
        && player.isCreative()
        && network != null
        && mayBreak(level, pos, state, player)) {
      ItemStack item = new ItemStack(this);
      item.applyComponents(network.collectComponents());
      popResource(level, network.getBlockPos(), item);
      if (!isLower(state)) {
        level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), REMOVE_WITHOUT_DROPS);
      }
    }
    return super.playerWillDestroy(level, pos, state, player);
  }

  /** The redstone of the slot drops, as in the Dock, and the pair of the chunk is made again. */
  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    boolean gone = !state.is(newState.getBlock()) && isLower(state);
    if (gone && level.getBlockEntity(pos) instanceof NetworkBlockEntity network) {
      Containers.dropContents(level, pos, network.slot());
      network.gone();
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
    if (gone && !level.isClientSide()) {
      NetworkChunk.pair(level, pos);
    }
  }

  @Override
  public boolean canEntityDestroy(
      BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
    return false;
  }

  /** Pistons cannot move it, and the block properties make it resist explosions. */
  @Override
  public PushReaction getPistonPushReaction(BlockState state) {
    return PushReaction.BLOCK;
  }
}
