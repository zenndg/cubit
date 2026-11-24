package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.menu.RobotStorage;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

/**
 * What a kill of the Galactic Patroller gives: a phrase, its drops and, with the blade, a chance of
 * the head of a mob the game has a head for, all into its inventory while they fit, and the
 * experience into its tank (answers 1559, 1587, 1635 and 1821).
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class PatrolKills {
  private static final Map<EntityType<?>, Item> HEADS =
      Map.of(
          EntityType.ZOMBIE, Items.ZOMBIE_HEAD,
          EntityType.SKELETON, Items.SKELETON_SKULL,
          EntityType.CREEPER, Items.CREEPER_HEAD,
          EntityType.PIGLIN, Items.PIGLIN_HEAD,
          EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL);

  private PatrolKills() {}

  @SubscribeEvent
  public static void onDeath(LivingDeathEvent event) {
    DamageSource source = event.getSource();
    Optional<RobotEntity> killer = killer(source);
    if (killer.isEmpty()) {
      return;
    }
    RobotEntity robot = killer.get();
    robot.workArea().work().talk().onWork(JobTalkTopic.PATROLLER_DEFEAT);
    if (source.is(CubitDamageTypes.PLASMA_BLADE)) {
      dropHead(robot, event.getEntity(), robot.getRandom().nextInt(Percentage.MAX));
    }
  }

  /**
   * The drops go straight into its inventory, without the magnet or the filter, and what does not
   * fit falls as usual (answer 1635).
   */
  @SubscribeEvent
  public static void onDrops(LivingDropsEvent event) {
    killer(event.getSource())
        .ifPresent(robot ->
            event
                .getDrops()
                .removeIf(drop -> {
                  ItemStack stack = drop.getItem();
                  RobotStorage.insert(robot.inventory(), stack);
                  return stack.isEmpty();
                }));
  }

  /**
   * The experience of its kill goes to its tank, near or far from its owner, who gets it at the
   * Dock (answer 1821). Without the owner online the game drops none (answer 2062). The game stores
   * the last damage only after it drops the experience, so the kill is known by the last one that
   * hurt the mob, stored before the death. An armed Patroller never uses the shock, so a hurt from
   * it is always one of its weapons (answer 1759).
   */
  @SubscribeEvent
  public static void onExperience(LivingExperienceDropEvent event) {
    if (event.getEntity().getLastHurtByMob() instanceof RobotEntity robot
        && !robot.level().isClientSide()
        && robot.workArea().work().patrolCombat().armed()
        && robot.pickup().xp().takesFrom(RobotJob.PATROLLER)) {
      robot.pickup().xp().onWork(event.getDroppedExperience());
      event.setDroppedExperience(0);
    }
  }

  /** The Patroller that killed with its weapons, on the server. */
  private static Optional<RobotEntity> killer(DamageSource source) {
    if (!(source.getEntity() instanceof RobotEntity robot)
        || robot.level().isClientSide()
        || !(source.is(CubitDamageTypes.LASER_ORB) || source.is(CubitDamageTypes.PLASMA_BLADE))) {
      return Optional.empty();
    }
    return Optional.of(robot);
  }

  /** The draw comes from the caller, so a test can fix it. */
  public static void dropHead(RobotEntity robot, LivingEntity mob, int roll) {
    Item head = HEADS.get(mob.getType());
    if (head == null || !CubitMod.CONFIG.patrol().settings().dropsHead(roll)) {
      return;
    }
    ItemStack stack = new ItemStack(head);
    RobotStorage.insert(robot.inventory(), stack);
    if (!stack.isEmpty()) {
      Containers.dropItemStack(robot.level(), robot.getX(), robot.getY(), robot.getZ(), stack);
    }
  }
}
