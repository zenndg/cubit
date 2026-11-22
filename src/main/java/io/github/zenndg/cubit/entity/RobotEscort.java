package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.menu.TabletKeys;
import io.github.zenndg.cubit.registry.CubitEntities;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

/**
 * The one the Galactic Patroller escorts, marked by its owner with the Escort app: a player or a
 * mob it never attacks. It lives in the save of the entity, under EscortTarget, never in the item,
 * and comes back when both are loaded (answers 1655 to 1661 and 1742).
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class RobotEscort {
  private static final String KEY = "EscortTarget";

  private final RobotEntity robot;
  private UUID escorted;

  RobotEscort(RobotEntity robot) {
    this.robot = robot;
  }

  /** It escorts someone, loaded or not. */
  public boolean active() {
    return escorted != null;
  }

  public boolean escorts(UUID entity) {
    return entity.equals(escorted);
  }

  /** The escorted one, only while loaded in the level of the Patroller. */
  Optional<LivingEntity> escortedHere() {
    if (escorted == null || !(robot.level() instanceof ServerLevel level)) {
      return Optional.empty();
    }
    Entity found = level.getEntity(escorted);
    return found instanceof LivingEntity living && living.isAlive()
        ? Optional.of(living)
        : Optional.empty();
  }

  /**
   * A new escorted one replaces the last, one at a time, and a mob it attacks is refused (answers
   * 1656 and 1658).
   */
  public boolean mark(LivingEntity entity) {
    if (entity == robot || PatrolProtection.isTarget(robot, entity)) {
      return false;
    }
    stop();
    escorted = entity.getUUID();
    tell(entity, TabletKeys.ESCORTING_YOU);
    return true;
  }

  /** The escort ends, and a player escorted is told (answer 1661). */
  public void stop() {
    escortedHere().ifPresent(entity -> tell(entity, TabletKeys.STOPPED_ESCORTING_YOU));
    escorted = null;
  }

  private void tell(LivingEntity entity, String key) {
    if (entity instanceof ServerPlayer player) {
      player.displayClientMessage(Component.translatable(key, robot.getName()), true);
    }
  }

  void save(CompoundTag tag) {
    if (escorted != null) {
      tag.putUUID(KEY, escorted);
    }
  }

  void load(CompoundTag tag) {
    escorted = tag.hasUUID(KEY) ? tag.getUUID(KEY) : null;
  }

  /**
   * The escort ends when the escorted one dies, changes its dimension or, a player, leaves the
   * game. A mob in a chunk that unloads only waits (answers 1658 and 1742).
   */
  @SubscribeEvent
  public static void onLeave(EntityLeaveLevelEvent event) {
    Entity entity = event.getEntity();
    Entity.RemovalReason reason = entity.getRemovalReason();
    if (!(event.getLevel() instanceof ServerLevel level)
        || !(entity instanceof LivingEntity)
        || reason == null
        || reason == Entity.RemovalReason.UNLOADED_TO_CHUNK) {
      return;
    }
    UUID gone = entity.getUUID();
    level
        .getEntities(
            CubitEntities.GENERAL_ROBOT.get(),
            robot -> robot.workArea().work().escort().escorts(gone))
        .forEach(robot -> robot.workArea().work().escort().endFor(entity));
  }

  /** Ends the escort of one that is leaving, telling a player still in the game. */
  private void endFor(Entity leaving) {
    if (leaving instanceof LivingEntity living) {
      tell(living, TabletKeys.STOPPED_ESCORTING_YOU);
    }
    escorted = null;
  }
}
