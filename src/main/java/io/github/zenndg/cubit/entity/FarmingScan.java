package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.robot.AreaColumn;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * The search for the work of the Farmer in its area, spread over the ticks with the budget of
 * blocks of the scanners (rules 21 and 22): from the marked blocks up to one block over the top,
 * where the plants grow (answer 1224).
 */
final class FarmingScan {
  /** Reading the water around ground to till costs much more than one block. */
  private static final int READS_PER_BLOCK = 4;

  private final Level level;
  private final WorkArea area;
  private final boolean special;
  private final List<FarmTask> found = new ArrayList<>();
  private final long volume;
  private long next;

  FarmingScan(Level level, WorkArea area, boolean special) {
    this.level = level;
    this.area = area;
    this.special = special;
    this.volume = (long) area.columnCount() * (area.height() + 1);
  }

  boolean done() {
    return next >= volume;
  }

  List<FarmTask> found() {
    return found;
  }

  void step(int budget) {
    int blocks = Math.max(1, budget / READS_PER_BLOCK);
    for (int read = 0; read < blocks && !done(); read++) {
      BlockPos pos = posAt(next++);
      if (level.isLoaded(pos)) {
        FarmPlants.actionAt(level, pos, special)
            .ifPresent(action -> found.add(new FarmTask(pos, action)));
      }
    }
  }

  private BlockPos posAt(long index) {
    AreaColumn column = area.column((int) (index % area.columnCount()));
    int level = area.min().y() + (int) (index / area.columnCount());
    return new BlockPos(column.x(), level, column.z());
  }
}
