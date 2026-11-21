package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskLook;
import io.github.zenndg.cubit.domain.desk.DeskProgress;
import net.minecraft.nbt.CompoundTag;

/**
 * What a client knows of the desk beyond its block state: the progress, moved on by itself between
 * two updates, the people at the screen and when the last item was ready (answer 1508). It lives in
 * common code, but only the client fills it.
 */
public final class DeskClientView {
  static final String VIEWERS_KEY = "Viewers";
  static final String CRAFTS_KEY = "Crafts";

  /** How long the hologram shines when an item is ready (answer 1471). */
  private static final long SHINE_TICKS = 10;

  private static final long NEVER = Long.MIN_VALUE;

  private DeskProgress progress = DeskProgress.NONE;
  private long receivedAt;
  private int viewers;
  private int crafts;
  private long readyAt = NEVER;

  void receive(CompoundTag tag, long gameTime) {
    progress = new DeskProgress(Math.max(0, tag.getInt(EngineeringDeskBlockEntity.PROGRESS_KEY)));
    receivedAt = gameTime;
    viewers = Math.max(0, tag.getInt(VIEWERS_KEY));
    int now = tag.getInt(CRAFTS_KEY);
    if (now != crafts) {
      crafts = now;
      readyAt = gameTime;
    }
  }

  public int viewers() {
    return viewers;
  }

  /** The share done of the craft, which only moves on while the desk crafts. */
  public float fraction(DeskLook look, int craftTicks, long gameTime) {
    DeskProgress shown =
        look == DeskLook.CRAFTING ? progress.plus((int) (gameTime - receivedAt)) : progress;
    return shown.fraction(craftTicks);
  }

  public boolean isShining(long gameTime) {
    return readyAt != NEVER && gameTime >= readyAt && gameTime - readyAt < SHINE_TICKS;
  }
}
