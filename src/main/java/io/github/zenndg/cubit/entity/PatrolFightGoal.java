package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
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
 * then lets go, to investigate where it last saw the hostile. Wounded, it backs off toward the
 * nearest one it protects (answers 1562, 1565, 1567, 1595, 1624, 1636, 1645 and 1686).
 */
final class PatrolFightGoal extends Goal {
  private static final double SPEED_MODIFIER = 1;
  private static final double BLADE_STOP = 1.8;
  private static final int REPATH_TICKS = 5;
  private static final double ON_SPOT = 1;
  private static final double ARRIVED_BLOCKS = 1.5;
  private static final double PROGRESS_BLOCKS = 0.5;

  private final RobotEntity robot;
  private final PatrolPause pause;
  private boolean investigating;
  private boolean lookingAround;
  private double nearest;
  private int stuckTicks;
  private int repathCountdown;

  PatrolFightGoal(RobotEntity robot) {
    this.robot = robot;
    this.pause = new PatrolPause(robot);
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
    investigating = false;
  }

  @Override
  public void stop() {
    robot.getNavigation().stop();
  }

  @Override
  public void tick() {
    RobotPatrolCombat combat = robot.workArea().work().patrolCombat();
    if (combat.wings().flying()) {
      return;
    }
    if (combat.watch().holdsStill()) {
      holdStill(combat);
      return;
    }
    if (combat.watch().phase() == PatrolWatch.Phase.INVESTIGATION) {
      investigate(combat);
      return;
    }
    investigating = false;
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
    if (backedOff(weapon, target)) {
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
      robot
          .workArea()
          .work()
          .patrolCombat()
          .wings()
          .moveTo(target.position(), SPEED_MODIFIER, true);
    }
  }

  /**
   * Out of the blast of a creeper, or away from a creeper it cannot shoot, and, wounded, back from
   * a target too near (answers 1567, 1595 and 1686).
   */
  private boolean backedOff(WeaponChoice weapon, LivingEntity target) {
    boolean swelling = target instanceof Creeper creeper && creeper.getSwellDir() > 0;
    double distance = robot.distanceTo(target);
    PatrolRules.CreeperDistances creeper = CubitMod.CONFIG.patrol().creeper();
    if (PatrolRules.backsAway(swelling, distance, creeper.blast())) {
      keepAway(target, creeper.keepAway());
      return true;
    }
    if (weapon == WeaponChoice.NONE) {
      shield(target, creeper.keepAway());
      return true;
    }
    if (robot.workArea().work().patrolCombat().cautious() && distance < switchDistance()) {
      retreat(target);
      return true;
    }
    return false;
  }

  /**
   * Without a shot at a creeper it stands as a shield, out of the blast, on the way to the one it
   * protects nearest to the creeper, or else just away from it (answers 1595, 1641 and 1731).
   */
  private void shield(LivingEntity creeper, int keepAway) {
    Optional<LivingEntity> guarded = nearestProtected(creeper);
    if (guarded.isEmpty()) {
      if (robot.distanceTo(creeper) < keepAway) {
        keepAway(creeper, keepAway);
      }
      return;
    }
    LivingEntity one = guarded.get();
    PatrolSiege.Spot spot =
        PatrolRules.shield(creeper.getX(), creeper.getZ(), one.getX(), one.getZ(), keepAway);
    robot.getNavigation().moveTo(spot.x(), creeper.getY(), spot.z(), SPEED_MODIFIER);
  }

  /** The one it protects nearest to a place, within its search, never itself. */
  private Optional<LivingEntity> nearestProtected(LivingEntity around) {
    int radius = robot.workArea().work().patrolCombat().spec().searchRadius();
    return robot
        .level()
        .getEntitiesOfClass(
            LivingEntity.class,
            around.getBoundingBox().inflate(radius),
            one -> one != robot && one.isAlive() && PatrolProtection.isProtected(robot, one))
        .stream()
        .min(Comparator.comparingDouble(around::distanceTo));
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
   * It walks to where it last saw the hostile, at the normal speed, and there stops and looks
   * around, as at a stop of the round, before it goes back (answer 1624).
   */
  private void investigate(RobotPatrolCombat combat) {
    if (!investigating) {
      investigating = true;
      lookingAround = false;
      nearest = Double.MAX_VALUE;
      stuckTicks = 0;
    }
    if (lookingAround) {
      if (!pause.hold()) {
        combat.searched();
      }
      return;
    }
    Vec3 spot = combat.investigationSpot().orElse(robot.position());
    double distance = robot.position().subtract(spot).horizontalDistance();
    if (distance < ARRIVED_BLOCKS || stuck(distance)) {
      robot.workArea().work().patrol().stopHere();
      pause.reset();
      lookingAround = true;
      return;
    }
    if (--repathCountdown <= 0) {
      repathCountdown = REPATH_TICKS;
      robot.workArea().work().patrolCombat().wings().moveTo(spot, SPEED_MODIFIER, true);
    }
  }

  /** A spot it cannot reach, as in the water, is looked at from as near as it got (answer 1693). */
  private boolean stuck(double distance) {
    if (distance < nearest - PROGRESS_BLOCKS) {
      nearest = distance;
      stuckTicks = 0;
      return false;
    }
    return ++stuckTicks >= CubitMod.CONFIG.dock().stuckTicks();
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

  /**
   * Wounded and too near, it backs off toward the nearest one it protects, or else away from the
   * target (answer 1686).
   */
  private void retreat(LivingEntity target) {
    Optional<LivingEntity> shelter = nearestProtected(robot);
    if (shelter.isEmpty()) {
      keepAway(target, CubitMod.CONFIG.patrol().creeper().keepAway());
      return;
    }
    robot.getNavigation().moveTo(shelter.get(), SPEED_MODIFIER);
  }

  private static int switchDistance() {
    return RobotPatrolCombat.settings().weaponSwitchDistance();
  }

  /** Away from the target, on the other side from it. */
  private void keepAway(LivingEntity target, int distance) {
    Vec3 away = robot.position().subtract(target.position()).multiply(1, 0, 1).normalize();
    Vec3 spot = robot.position().add(away.scale(distance));
    robot.getNavigation().moveTo(spot.x, spot.y, spot.z, SPEED_MODIFIER);
  }
}
