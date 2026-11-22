package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.menu.RobotStorage;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import java.util.Map;
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

/**
 * What a kill of the Galactic Patroller gives: a phrase, and with the blade, a chance of the head
 * of a mob the game has a head for, into its inventory (answers 1559 and 1587).
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
    if (!(source.getEntity() instanceof RobotEntity robot)
        || robot.level().isClientSide()
        || !(source.is(CubitDamageTypes.LASER_ORB) || source.is(CubitDamageTypes.PLASMA_BLADE))) {
      return;
    }
    robot.workArea().work().talk().onWork(JobTalkTopic.PATROLLER_DEFEAT);
    if (source.is(CubitDamageTypes.PLASMA_BLADE)) {
      dropHead(robot, event.getEntity(), robot.getRandom().nextInt(Percentage.MAX));
    }
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
