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

/** The Network Server, the center of a network, a cabinet two blocks tall. */
public final class NetworkServerBlock extends NetworkBlock {
  private static final MapCodec<NetworkServerBlock> CODEC = simpleCodec(NetworkServerBlock::new);

  public NetworkServerBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return isLower(state) ? new NetworkServerBlockEntity(pos, state) : null;
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return NetworkShapes.server(isLower(state));
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide() || !isLower(state)
        ? null
        : createTickerHelper(
            type, CubitBlockEntities.NETWORK_SERVER.get(), NetworkServerBlockEntity::serverTick);
  }
}
