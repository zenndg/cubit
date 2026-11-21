package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskLook;
import io.github.zenndg.cubit.registry.CubitSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

/** The low loop of a desk that crafts. It stops by itself in a pause and at the end (1452). */
final class DeskLoopSound extends AbstractTickableSoundInstance {
  /** A short gap between two crafts would otherwise restart the loop. */
  private static final int STOP_DELAY_TICKS = 5;

  private static final double CENTER = 0.5;

  private final EngineeringDeskBlockEntity desk;
  private int ticksStopped;

  DeskLoopSound(EngineeringDeskBlockEntity desk) {
    super(
        CubitSounds.DESK_CRAFTING.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
    this.desk = desk;
    looping = true;
    delay = 0;
    x = desk.getBlockPos().getX() + CENTER;
    y = desk.getBlockPos().getY() + CENTER;
    z = desk.getBlockPos().getZ() + CENTER;
  }

  @Override
  public boolean canStartSilent() {
    return true;
  }

  @Override
  public void tick() {
    ticksStopped = desk.look() == DeskLook.CRAFTING ? 0 : ticksStopped + 1;
    if (desk.isRemoved() || ticksStopped > STOP_DELAY_TICKS) {
      stop();
    }
  }
}
