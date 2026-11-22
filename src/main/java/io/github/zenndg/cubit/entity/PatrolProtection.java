package io.github.zenndg.cubit.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

/**
 * Who the Galactic Patroller protects and never hits: its owner, other players, robots, animals and
 * villagers (answer 1570).
 */
final class PatrolProtection {
  private PatrolProtection() {}

  static boolean isProtected(RobotEntity robot, LivingEntity entity) {
    return entity == robot
        || entity instanceof Player
        || entity instanceof RobotEntity
        || entity instanceof Animal
        || entity instanceof AbstractVillager;
  }
}
