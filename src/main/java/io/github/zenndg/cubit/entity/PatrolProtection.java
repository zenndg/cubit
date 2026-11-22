package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolTraits;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

/**
 * Who the Galactic Patroller protects and who it attacks, read from the signs of the game, never
 * from a fixed list, so the mobs of any mod count. The domain decides from them (answers 1570,
 * 1633, 1634 and 1701 to 1705).
 */
public final class PatrolProtection {
  private static final String TARGETS_PATH = "patroller_targets";
  private static final String IGNORES_PATH = "patroller_ignores";

  /** The mobs it always attacks, empty in the mod, for the pack (answer 1634). */
  public static final TagKey<EntityType<?>> TARGETS = tag(TARGETS_PATH);

  /** The mobs it never attacks, empty in the mod, which wins over TARGETS (answer 1634). */
  public static final TagKey<EntityType<?>> IGNORES = tag(IGNORES_PATH);

  private PatrolProtection() {}

  private static TagKey<EntityType<?>> tag(String path) {
    return TagKey.create(
        Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path));
  }

  /** Someone it guards: a mob chasing it hurts someone, and its weapons pass through it. */
  private static boolean isProtected(RobotEntity robot, LivingEntity entity) {
    return traits(robot, entity, false).guarded();
  }

  /** A mob it attacks: a hostile of any kind, or anyone chasing someone it guards. */
  static boolean isTarget(RobotEntity robot, LivingEntity entity) {
    return traits(robot, entity, hurtingSomeone(robot, entity)).attacked();
  }

  /** A mob chasing anyone the Patroller protects. */
  static boolean hurtingSomeone(RobotEntity robot, LivingEntity entity) {
    return entity instanceof Mob mob
        && mob.getTarget() != null
        && isProtected(robot, mob.getTarget());
  }

  private static PatrolTraits traits(RobotEntity robot, LivingEntity entity, boolean hurting) {
    EntityType<?> type = entity.getType();
    return new PatrolTraits(
        shielded(robot, entity),
        entity instanceof Animal,
        spared(entity),
        entity instanceof Enemy,
        type.getCategory() == MobCategory.MONSTER,
        entity instanceof NeutralMob,
        type.is(TARGETS),
        type.is(IGNORES),
        hurting,
        ShockCandidates.angryAtOwner(RobotInteractions.onlineOwner(robot), entity));
  }

  private static boolean shielded(RobotEntity robot, LivingEntity entity) {
    return entity == robot
        || entity instanceof Player
        || entity instanceof RobotEntity
        || entity instanceof AbstractVillager;
  }

  /** The golems, a tamed mob of anyone and the peaceful ones of the water (answer 1633). */
  private static boolean spared(LivingEntity entity) {
    boolean golem = entity instanceof IronGolem || entity instanceof SnowGolem;
    boolean tamed = entity instanceof OwnableEntity owned && owned.getOwnerUUID() != null;
    boolean calmWater = entity instanceof WaterAnimal && !(entity instanceof Enemy);
    return golem || tamed || calmWater;
  }
}
