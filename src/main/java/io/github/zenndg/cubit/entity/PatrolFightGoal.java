package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolRules;
import io.github.zenndg.cubit.domain.behavior.PatrolSiege;
import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import io.github.zenndg.cubit.domain.behavior.WeaponChoice;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * How the Galactic Patroller moves in a fight: close in for the blade, stand and aim for the
 * pistol, keep out of the blast of a creeper and, with the team, besiege the target from every
 * side. It chases only so far from what it guards, the route, the area or the one it escorts, and
 * then lets go (answers 1562, 1565, 1567, 1595, 1636 and 1645).
 */
final class PatrolFightGoal extends Goal {
  private static final double SPEED_MODIFIER = 1;
  private static final double BLADE_STOP = 1.8;
  private static final double CREEPER_BLAST = 4;
  private static final double KEEP_AWAY = 6;
  private static final int REPATH_TICKS = 5;
  private static final double ON_SPOT = 1;

  private final RobotEntity robot;
  private int repathCountdown;

  PatrolFightGoal(RobotEntity robot) {
    this.robot = robot;
    setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return robot.workArea().work().patrolCombat().busy();
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
    repathCountdown = 0;
  }

  @Override
  public void stop() {
    robot.getNavigation().stop();
  }

  @Override
  public void tick() {
    RobotPatrolCombat combat = robot.workArea().work().patrolCombat();
    if (combat.watch().holdsStill()) {
      holdStill(combat);
      return;
    }
    Optional<LivingEntity> found = combat.target();
    if (found.isEmpty()) {
      return;
    }
    LivingEntity target = found.get();
    if (!combat.inReach(target)) {
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
    if (weapon != WeaponChoice.NONE && besiege(weapon, target)) {
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

  /**
   * With others of the team on the same target, each goes to its side: the first keeps the one it
   * came from and the others spread evenly around (answers 1645 and 1718).
   */
  private boolean besiege(WeaponChoice weapon, LivingEntity target) {
    List<RobotEntity> siege = besiegers(target);
    if (siege.size() < 2) {
      return false;
    }
    RobotEntity first = siege.getFirst();
    double firstAngle = Math.atan2(first.getZ() - target.getZ(), first.getX() - target.getX());
    double angle = PatrolSiege.angle(siege.indexOf(robot), siege.size(), firstAngle);
    double distance = robot.position().subtract(target.position()).horizontalDistance();
    double radius = reach().radius(weapon == WeaponChoice.BLADE, distance);
    PatrolSiege.Spot spot = PatrolSiege.spot(target.getX(), target.getZ(), angle, radius);
    if (Math.hypot(robot.getX() - spot.x(), robot.getZ() - spot.z()) < ON_SPOT) {
      robot.getNavigation().stop();
    } else {
      robot.getNavigation().moveTo(spot.x(), target.getY(), spot.z(), SPEED_MODIFIER);
    }
    return true;
  }

  /** Itself and the mates on the same target, always in the same order. */
  private List<RobotEntity> besiegers(LivingEntity target) {
    Stream<RobotEntity> mates =
        PatrolTeams.mates(robot).stream()
            .filter(mate -> mate.workArea().work().patrolCombat().target().orElse(null) == target);
    return Stream.concat(Stream.of(robot), mates)
        .sorted(Comparator.comparing(RobotEntity::getUUID))
        .toList();
  }

  private PatrolSiege.Reach reach() {
    RobotPatrolCombat combat = robot.workArea().work().patrolCombat();
    return new PatrolSiege.Reach(
        BLADE_STOP,
        RobotPatrolCombat.settings().weaponSwitchDistance(),
        combat.spec().weapons().pistolRange());
  }

  /**
   * On the alert it stands while it turns, and after the last kill it looks at its owner (1629).
   */
  private void holdStill(RobotPatrolCombat combat) {
    robot.getNavigation().stop();
    if (combat.watch().phase() == PatrolWatch.Phase.OWNER_CHECK) {
      Player owner = RobotInteractions.onlineOwner(robot);
      if (owner != null) {
        robot.getLookControl().setLookAt(owner);
      }
    }
  }

  /** Away from the target, on the other side from it. */
  private void keepAway(LivingEntity target) {
    Vec3 away = robot.position().subtract(target.position()).multiply(1, 0, 1).normalize();
    Vec3 spot = robot.position().add(away.scale(KEEP_AWAY));
    robot.getNavigation().moveTo(spot.x, spot.y, spot.z, SPEED_MODIFIER);
  }
}
