package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.tier.PatrollerSpec;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import io.github.zenndg.cubit.registry.CubitSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * The pistol and the blade of the Galactic Patroller: each waits its interval, the shot pays its
 * energy, and the blade pushes the target back (answers 1551, 1571 and 1577), each with its own
 * sound (answers 1791 and 1795).
 */
final class RobotPatrolWeapons {
  private static final int SPARKS = 6;
  private static final double SPARK_SPREAD = 0.2;
  private static final double SPARK_SPEED = 0.1;
  private static final double MIDDLE = 0.5;

  private final RobotEntity robot;
  private long nextShot;
  private long nextSwing;

  RobotPatrolWeapons(RobotEntity robot) {
    this.robot = robot;
  }

  void shoot(Entity target, PatrollerSpec spec, long now) {
    if (now < nextShot || !spec.canShoot(robot.battery())) {
      return;
    }
    nextShot = now + spec.weapons().pistolIntervalTicks();
    robot.setBattery(spec.payForShot(robot.battery()));
    LaserOrb.shoot(
        robot,
        target,
        spec.weapons().pistolDamage(),
        spec.weapons().slownessLevel(),
        RobotPatrolCombat.settings());
    robot.playSound(CubitSounds.PATROL_PISTOL_SHOT.get());
  }

  void swing(LivingEntity target, PatrollerSpec spec, long now) {
    if (now < nextSwing) {
      return;
    }
    nextSwing = now + spec.weapons().bladeIntervalTicks();
    Player owner = RobotInteractions.onlineOwner(robot);
    if (owner != null) {
      target.setLastHurtByPlayer(owner);
    }
    if (target.hurt(CubitDamageTypes.plasmaBlade(robot), spec.weapons().bladeDamage())) {
      double strength =
          CubitMod.CONFIG.patrol().knockback().strength(spec.weapons().bladeKnockback());
      target.knockback(strength, robot.getX() - target.getX(), robot.getZ() - target.getZ());
      robot.playSound(CubitSounds.PATROL_BLADE_HIT.get());
    }
    robot.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
    robot.playSound(CubitSounds.PATROL_BLADE_SWING.get());
    sparks(target);
  }

  private void sparks(LivingEntity target) {
    if (robot.level() instanceof ServerLevel level) {
      level.sendParticles(
          ParticleTypes.ELECTRIC_SPARK,
          target.getX(),
          target.getY(MIDDLE),
          target.getZ(),
          SPARKS,
          SPARK_SPREAD,
          SPARK_SPREAD,
          SPARK_SPREAD,
          SPARK_SPEED);
    }
  }
}
