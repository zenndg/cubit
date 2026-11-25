package io.github.zenndg.cubit.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The shapes of the blocks of the Network, from their models: the cabinet of the server, and the
 * base, the pedestal, the folded panel and the mast of the relay, by facing.
 */
final class NetworkShapes {
  private static final double FULL = 16;

  /** The body goes up to 29 px, and only the two antennas to 32. */
  private static final VoxelShape SERVER_UPPER = Block.box(0, 0, 0, FULL, 13, FULL);

  private static final VoxelShape SERVER_LOWER = Shapes.block();
  private static final VoxelShape RELAY_BASE = Block.box(3, 0, 3, 13, 5, 13);
  private static final double[] COLUMN = {5, 5, 4, 11, FULL, 13.5};
  private static final double[] PANEL_TOP = {5, 0, 4, 11, 3.6, 8};
  private static final double[] MAST = {6.8, 0, 11, 9.2, FULL, 13.6};
  private static final double[] DISH = {5.4, 6, 7.6, 10.6, 11.4, 13.6};
  private static final int MIN_X = 0;
  private static final int MIN_Y = 1;
  private static final int MIN_Z = 2;
  private static final int MAX_X = 3;
  private static final int MAX_Y = 4;
  private static final int MAX_Z = 5;

  private NetworkShapes() {}

  static VoxelShape server(boolean lower) {
    return lower ? SERVER_LOWER : SERVER_UPPER;
  }

  static VoxelShape relay(Direction facing, boolean lower) {
    return lower
        ? Shapes.or(RELAY_BASE, turned(COLUMN, facing))
        : Shapes.or(turned(PANEL_TOP, facing), turned(MAST, facing), turned(DISH, facing));
  }

  /** The boxes are written with the front to the north. */
  private static VoxelShape turned(double[] box, Direction facing) {
    double x = box[MIN_X];
    double z = box[MIN_Z];
    double toX = box[MAX_X];
    double toZ = box[MAX_Z];
    return switch (facing) {
      case SOUTH -> Block.box(FULL - toX, box[MIN_Y], FULL - toZ, FULL - x, box[MAX_Y], FULL - z);
      case EAST -> Block.box(FULL - toZ, box[MIN_Y], x, FULL - z, box[MAX_Y], toX);
      case WEST -> Block.box(z, box[MIN_Y], FULL - toX, toZ, box[MAX_Y], FULL - x);
      default -> Block.box(x, box[MIN_Y], z, toX, box[MAX_Y], toZ);
    };
  }
}
