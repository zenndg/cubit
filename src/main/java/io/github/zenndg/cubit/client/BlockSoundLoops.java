package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskLook;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Starts the loop of each block that works. Weak keys let the game forget a removed block. */
final class BlockSoundLoops {
  private static final Map<BlockEntity, BlockLoopSound> LOOPS = new WeakHashMap<>();

  private BlockSoundLoops() {}

  static void tick(EngineeringDeskBlockEntity desk) {
    keep(desk, CubitSounds.DESK_CRAFTING.get(), () -> desk.look() == DeskLook.CRAFTING);
  }

  static void tick(NetworkServerBlockEntity server) {
    keep(server, CubitSounds.SERVER_FANS.get(), () -> server.view().fansTurn());
  }

  static void tick(SolarRelayBlockEntity relay) {
    keep(relay, CubitSounds.RELAY_TRANSMIT.get(), () -> relay.view().transmitting());
  }

  private static void keep(BlockEntity block, SoundEvent sound, BooleanSupplier works) {
    if (!works.getAsBoolean()) {
      return;
    }
    SoundManager manager = Minecraft.getInstance().getSoundManager();
    BlockLoopSound current = LOOPS.get(block);
    if (current == null || !manager.isActive(current)) {
      BlockLoopSound loop = new BlockLoopSound(block, sound, works);
      LOOPS.put(block, loop);
      manager.play(loop);
    }
  }
}
