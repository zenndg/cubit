package io.github.zenndg.cubit.client;

import java.util.function.BooleanSupplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The low loop of a block that works: a desk that crafts, the fans of a server or a relay that
 * transmits. It stops by itself when the block stops (answers 1452 and 2375).
 */
final class BlockLoopSound extends AbstractTickableSoundInstance {
  /** A short gap, as between two crafts, would otherwise restart the loop. */
  private static final int STOP_DELAY_TICKS = 5;

  private static final double CENTER = 0.5;

  private final BlockEntity block;
  private final BooleanSupplier works;
  private int ticksStopped;

  BlockLoopSound(BlockEntity block, SoundEvent sound, BooleanSupplier works) {
    super(sound, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
    this.block = block;
    this.works = works;
    looping = true;
    delay = 0;
    x = block.getBlockPos().getX() + CENTER;
    y = block.getBlockPos().getY() + CENTER;
    z = block.getBlockPos().getZ() + CENTER;
  }

  @Override
  public boolean canStartSilent() {
    return true;
  }

  @Override
  public void tick() {
    ticksStopped = works.getAsBoolean() ? 0 : ticksStopped + 1;
    if (block.isRemoved() || ticksStopped > STOP_DELAY_TICKS) {
      stop();
    }
  }
}
