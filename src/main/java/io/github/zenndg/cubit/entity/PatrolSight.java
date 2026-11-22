package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.DefenseRules;
import io.github.zenndg.cubit.domain.behavior.PatrolTargets;
import io.github.zenndg.cubit.domain.behavior.ShockSignals;
import io.github.zenndg.cubit.domain.robot.DefenseMode;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

/**
 * What the Galactic Patroller sees around it: every hostile, and any mob hurting someone it
 * protects, by the rules of the Aggressive mode (answers 1566 and 1570).
 */
final class PatrolSight {
  private PatrolSight() {}

  static List<LivingEntity> hostiles(RobotEntity robot, int radius) {
    return robot
        .level()
        .getEntitiesOfClass(
            LivingEntity.class,
            robot.getBoundingBox().inflate(radius),
            entity ->
                entity.isAlive()
                    && robot.distanceTo(entity) <= radius
                    && !PatrolProtection.isProtected(robot, entity)
                    && attacks(robot, entity));
  }

  private static boolean attacks(RobotEntity robot, LivingEntity entity) {
    Player owner = RobotInteractions.onlineOwner(robot);
    boolean turned = ShockCandidates.angryAtOwner(owner, entity) || hurtingSomeone(robot, entity);
    return DefenseRules.shocks(
        DefenseMode.AGGRESSIVE,
        ShockCandidates.of(robot, entity),
        new ShockSignals(turned, false, false, false));
  }

  /** A mob chasing anyone the Patroller protects. */
  static boolean hurtingSomeone(RobotEntity robot, LivingEntity entity) {
    return entity instanceof Mob mob
        && mob.getTarget() != null
        && PatrolProtection.isProtected(robot, mob.getTarget());
  }

  static PatrolTargets.Candidate candidate(RobotEntity robot, LivingEntity mob) {
    Player owner = RobotInteractions.onlineOwner(robot);
    LivingEntity guarded = owner != null && owner.level() == robot.level() ? owner : robot;
    return new PatrolTargets.Candidate(
        mob.getId(),
        hurtingSomeone(robot, mob),
        mob.distanceTo(guarded),
        PatrolReservations.reservedByOther(mob.getUUID(), robot.getUUID()));
  }
}
