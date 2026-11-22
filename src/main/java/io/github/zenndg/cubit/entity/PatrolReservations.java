package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

/**
 * The hostile each Galactic Patroller keeps, so the others choose another (answer 1568). It lives
 * only in memory, on the server, and a Patroller that stops fighting or leaves the world,
 * collected, unloaded or dead, gives its target back.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class PatrolReservations {
  private static final Map<UUID, UUID> BY_TARGET = new HashMap<>();

  private PatrolReservations() {}

  static boolean reservedByOther(UUID target, UUID patroller) {
    UUID holder = BY_TARGET.get(target);
    return holder != null && !holder.equals(patroller);
  }

  static void reserve(UUID target, UUID patroller) {
    release(patroller);
    BY_TARGET.put(target, patroller);
  }

  /** Only on the server, which keeps the reservations; the client shares the map in one game. */
  @SubscribeEvent
  public static void onLeave(EntityLeaveLevelEvent event) {
    if (!event.getLevel().isClientSide() && event.getEntity() instanceof RobotEntity robot) {
      release(robot.getUUID());
    }
  }

  static void release(UUID patroller) {
    BY_TARGET.values().removeIf(patroller::equals);
  }
}
