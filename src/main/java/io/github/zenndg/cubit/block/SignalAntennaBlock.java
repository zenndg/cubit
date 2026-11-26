package io.github.zenndg.cubit.block;

import com.mojang.serialization.MapCodec;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Signal Antenna, two blocks tall, which takes a channel of the Network Server it was linked to
 * and holds its own chunk (answers 2199, 2269 and 2546).
 */
public final class SignalAntennaBlock extends NetworkBlock {
  private static final MapCodec<SignalAntennaBlock> CODEC = simpleCodec(SignalAntennaBlock::new);

  public SignalAntennaBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return isLower(state) ? new SignalAntennaBlockEntity(pos, state) : null;
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return NetworkShapes.antenna(isLower(state));
  }

  /** Its link and its channel come first, and then what every block of the Network needs. */
  @Override
  Optional<Component> ownRefusal(BlockPlaceContext context) {
    return AntennaPlacing.refusal(this, context);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (!isLower(state) || level.isClientSide()) {
      return null;
    }
    return createTickerHelper(
        type, CubitBlockEntities.SIGNAL_ANTENNA.get(), SignalAntennaBlockEntity::serverTick);
  }
}
