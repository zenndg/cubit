package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolRules;
import io.github.zenndg.cubit.domain.behavior.WeaponChoice;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;

/**
 * How the Galactic Patroller moves in a fight: close in for the blade, stand and aim for the
 * pistol, and keep out of the blast of a creeper. It chases only so far from where the fight began,
 * the route or the owner it escorts, and then lets go (answers 1562, 1565, 1567 and 1595).
 */
final class PatrolFightGoal extends Goal {
  private static final double SPEED_MODIFIER = 1;
  private static final double BLADE_STOP = 1.8;
  private static final double CREEPER_BLAST = 4;
  private static final double KEEP_AWAY = 6;
  private static final int REPATH_TICKS = 5;

  private final RobotEntity robot;
  private Vec3 anchor;
  private int repathCountdown;

  PatrolFightGoal(RobotEntity robot) {
    this.robot = robot;
    setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return robot.workArea().work().patrolCombat().fighting();
  }

  @Override
  public boolean canContinueToUse() {
    return canUse();
  }

  @Override
  public boolean requiresUpdateEveryTick() {
    return true;
  }

  @Override
  public void start() {
    anchor = robot.position();
    repathCountdown = 0;
  }

  @Override
  public void stop() {
    robot.getNavigation().stop();
  }

  @Override
  public void tick() {
    RobotPatrolCombat combat = robot.workArea().work().patrolCombat();
    Optional<LivingEntity> found = combat.target();
    if (found.isEmpty()) {
      return;
    }
    LivingEntity target = found.get();
    Vec3 from = escortAnchor().orElse(anchor);
    int chase = RobotPatrolCombat.settings().chaseDistance();
    if (!PatrolRules.mayChase(target.position().distanceTo(from), chase)) {
      combat.drop();
      return;
    }
    if (--repathCountdown > 0) {
      return;
    }
    repathCountdown = REPATH_TICKS;
    move(combat.weapon(), target);
  }

  private void move(WeaponChoice weapon, LivingEntity target) {
    boolean swelling = target instanceof Creeper creeper && creeper.getSwellDir() > 0;
    if (PatrolRules.backsAway(swelling, robot.distanceTo(target), CREEPER_BLAST)
        || weapon == WeaponChoice.NONE && robot.distanceTo(target) < KEEP_AWAY) {
      keepAway(target);
      return;
    }
    if (weapon == WeaponChoice.PISTOL) {
      robot.getNavigation().stop();
      return;
    }
    if (weapon == WeaponChoice.BLADE && robot.distanceTo(target) > BLADE_STOP) {
      robot.getNavigation().moveTo(target, SPEED_MODIFIER);
    }
  }

  /** Away from the target, on the other side from it. */
  private void keepAway(LivingEntity target) {
    Vec3 away = robot.position().subtract(target.position()).multiply(1, 0, 1).normalize();
    Vec3 spot = robot.position().add(away.scale(KEEP_AWAY));
    robot.getNavigation().moveTo(spot.x, spot.y, spot.z, SPEED_MODIFIER);
  }

  /** On escort, the chase is measured from the owner it follows (answer 1565). */
  private Optional<Vec3> escortAnchor() {
    return Optional.ofNullable(RobotInteractions.followedPlayer(robot))
        .map(owner -> owner.position());
  }
}
