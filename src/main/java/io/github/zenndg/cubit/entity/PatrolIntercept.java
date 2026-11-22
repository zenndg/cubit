package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolRules;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;

/**
 * The fireball of a ghast heading for someone the Galactic Patroller protects: it becomes the
 * target of its pistol, before any hostile, and the orb sends it back to the ghast that threw it
 * (answers 1687, 1734 and 1735).
 */
final class PatrolIntercept {
  private PatrolIntercept() {}

  /** The nearest fireball in its pistol range, in sight, heading for someone it protects. */
  static Optional<LargeFireball> incoming(RobotEntity robot, int range) {
    return robot
        .level()
        .getEntitiesOfClass(
            LargeFireball.class,
            robot.getBoundingBox().inflate(range),
            shot ->
                shot.isAlive()
                    && !(shot.getOwner() instanceof RobotEntity)
                    && robot.distanceTo(shot) <= range
                    && robot.hasLineOfSight(shot))
        .stream()
        .filter(shot -> headsForSomeone(robot, shot))
        .min(Comparator.comparingDouble(robot::distanceTo));
  }

  private static boolean headsForSomeone(RobotEntity robot, LargeFireball shot) {
    Vec3 motion = shot.getDeltaMovement();
    int reach = CubitMod.CONFIG.patrol().interceptGuardDistance();
    return robot
        .level()
        .getEntitiesOfClass(
            LivingEntity.class,
            shot.getBoundingBox().inflate(reach),
            one -> one.isAlive() && PatrolProtection.isProtected(robot, one))
        .stream()
        .anyMatch(one -> {
          Vec3 toward = one.getBoundingBox().getCenter().subtract(shot.position());
          return PatrolRules.headsFor(motion.x, motion.y, motion.z, toward.x, toward.y, toward.z);
        });
  }

  /**
   * Back to the ghast that threw it, when in sight, or else the way the Patroller looks, as the
   * game does with a shot of a player (answer 1734).
   */
  static void returnToSender(LargeFireball shot, RobotEntity robot) {
    Entity sender = shot.getOwner();
    Vec3 way =
        sender != null && sender.isAlive() && robot.hasLineOfSight(sender)
            ? sender.getEyePosition().subtract(shot.position()).normalize()
            : robot.getLookAngle().normalize();
    shot.setDeltaMovement(way);
    shot.setOwner(robot);
    shot.hasImpulse = true;
  }
}
