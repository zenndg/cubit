package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolTargets;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * What the Galactic Patroller sees around it: every mob it attacks, by the signs of the game and
 * the tags of the pack, only for it, so the shock of the other Cubits stays as it is (answers 1633,
 * 1634 and 1701 to 1705).
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
                    && PatrolProtection.isTarget(robot, entity));
  }

  static PatrolTargets.Candidate candidate(RobotEntity robot, LivingEntity mob) {
    Player owner = RobotInteractions.onlineOwner(robot);
    LivingEntity guarded = owner != null && owner.level() == robot.level() ? owner : robot;
    return new PatrolTargets.Candidate(
        mob.getId(),
        PatrolProtection.hurtingSomeone(robot, mob),
        mob.distanceTo(guarded),
        PatrolReservations.reservedByOther(mob.getUUID(), robot.getUUID()));
  }
}
