package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.SkySun;
import io.github.zenndg.cubit.domain.energy.SolarPanel;
import io.github.zenndg.cubit.domain.energy.SunState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * The sky over the panel of a block of the mod. As the panel of the Cubit: light that passes
 * through glass still counts, an opaque block over it stops it, and the Nether and the End have no
 * sun. Only the reading of the column waits for the interval of the Cubit (rule 21).
 */
final class SkyWatch {
  private final int above;
  private boolean read;
  private boolean skyOpen;
  private boolean sunExists;

  /** The column is read that many blocks above the lower part. */
  SkyWatch(int above) {
    this.above = above;
  }

  /** True on the ticks that read the column, for what else waits for the same interval. */
  boolean refresh(Level level, BlockPos lower) {
    if (read && level.getGameTime() % CubitMod.CONFIG.solarCheckTicks() != 0) {
      return false;
    }
    read = true;
    skyOpen = level.canSeeSky(lower.above(above));
    sunExists = level.dimensionType().hasSkyLight() && !level.dimensionType().hasCeiling();
    return true;
  }

  SunState state(Level level, boolean room, boolean givesAtNight) {
    boolean raining = level.isRaining() || level.isThundering();
    boolean day = SolarPanel.isDay(level.getDayTime());
    return new SkySun(day, skyOpen, raining, sunExists, room, givesAtNight).state();
  }
}
