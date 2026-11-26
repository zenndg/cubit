package io.github.zenndg.cubit.block;

import com.mojang.serialization.MapCodec;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The Solar Relay, which brings the sun to the server of its chunk. */
public final class SolarRelayBlock extends NetworkBlock {
  private static final MapCodec<SolarRelayBlock> CODEC = simpleCodec(SolarRelayBlock::new);

  public SolarRelayBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return isLower(state) ? new SolarRelayBlockEntity(pos, state) : null;
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return NetworkShapes.relay(state.getValue(FACING), isLower(state));
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (!isLower(state)) {
      return null;
    }
    return createTickerHelper(
        type,
        CubitBlockEntities.SOLAR_RELAY.get(),
        level.isClientSide() ? NetworkClientTicks::tick : SolarRelayBlockEntity::serverTick);
  }
}
