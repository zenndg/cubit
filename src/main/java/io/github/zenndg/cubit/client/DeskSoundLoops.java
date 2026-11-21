package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskLook;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;

/** Starts the loop of each desk that crafts. Weak keys let the game forget a removed desk. */
final class DeskSoundLoops {
  private static final Map<EngineeringDeskBlockEntity, DeskLoopSound> LOOPS = new WeakHashMap<>();

  private DeskSoundLoops() {}

  static void tick(EngineeringDeskBlockEntity desk) {
    if (desk.look() != DeskLook.CRAFTING) {
      return;
    }
    SoundManager manager = Minecraft.getInstance().getSoundManager();
    DeskLoopSound current = LOOPS.get(desk);
    if (current == null || !manager.isActive(current)) {
      DeskLoopSound loop = new DeskLoopSound(desk);
      LOOPS.put(desk, loop);
      manager.play(loop);
    }
  }
}
