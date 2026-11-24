package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.registry.CubitFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * The Cubit XP Fluid, which lives only in tanks and pipes: no bucket, no block and no flow in the
 * world (answer 1815).
 */
public final class XpFluid extends Fluid {
  @Override
  public FluidType getFluidType() {
    return CubitFluids.XP_FLUID_TYPE.get();
  }

  @Override
  public Item getBucket() {
    return Items.AIR;
  }

  @Override
  protected boolean canBeReplacedWith(
      FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
    return true;
  }

  @Override
  protected Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) {
    return Vec3.ZERO;
  }

  @Override
  public int getTickDelay(LevelReader level) {
    return 0;
  }

  @Override
  protected float getExplosionResistance() {
    return 0;
  }

  @Override
  public float getHeight(FluidState state, BlockGetter level, BlockPos pos) {
    return 0;
  }

  @Override
  public float getOwnHeight(FluidState state) {
    return 0;
  }

  @Override
  protected BlockState createLegacyBlock(FluidState state) {
    return Blocks.AIR.defaultBlockState();
  }

  /** The only form of the fluid, so a tank and a pipe take it as a whole one. */
  @Override
  public boolean isSource(FluidState state) {
    return true;
  }

  @Override
  public int getAmount(FluidState state) {
    return 0;
  }

  @Override
  public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) {
    return Shapes.empty();
  }
}
