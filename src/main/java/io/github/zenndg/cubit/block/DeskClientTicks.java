package io.github.zenndg.cubit.block;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the client does on each tick of a desk, as the loop of the crafting sound. The client sets
 * it at start up, so common code never loads a class of the client (rule 23).
 */
public final class DeskClientTicks {
  private static Consumer<EngineeringDeskBlockEntity> ticker = desk -> {};

  private DeskClientTicks() {}

  public static void set(Consumer<EngineeringDeskBlockEntity> clientTicker) {
    ticker = clientTicker;
  }

  static void tick(Level level, BlockPos pos, BlockState state, EngineeringDeskBlockEntity desk) {
    ticker.accept(desk);
  }
}
