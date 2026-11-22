package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.domain.behavior.PatrolRules;
import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import io.github.zenndg.cubit.domain.behavior.PatrolTargets;
import io.github.zenndg.cubit.domain.behavior.PatrolTeam;
import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import io.github.zenndg.cubit.domain.behavior.WeaponChoice;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.RoutePoint;
import io.github.zenndg.cubit.domain.tier.PatrollerSpec;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * The fight of the Galactic Patroller, in place of the shock: it picks a hostile, chooses the
 * weapon by the distance, shoots its orb or swings its blade, jumps at a target in the air it
 * cannot shoot and glides when it falls (answers 1556, 1557, 1564, 1568 and 1596). It fights with
 * its team, on the target of all, and comes to the call and to the rescue of a wounded one (answers
 * 1643, 1644 and 1691). The search runs on an interval, never on every tick (rule 21).
 */
public final class RobotPatrolCombat {
  private static final double IN_AIR_BLOCKS = 1.5;
  private static final double GLIDE_FALL_SPEED = -0.12;
  private static final double JUMP_GRAVITY = 0.08;
  private static final double FALLING = -0.08;
  private static final float QUARTER_TURN = 90f;

  private final RobotEntity robot;
  private final RobotPatrolWeapons weapons;
  private LivingEntity target;
  private PatrolWatch watch = PatrolWatch.START;
  private final PatrolTrail trail = new PatrolTrail();
  private boolean threatNear;
  private long nextSearch;

  RobotPatrolCombat(RobotEntity robot) {
    this.robot = robot;
    this.weapons = new RobotPatrolWeapons(robot);
  }

  /** With the card and awake, the weapons replace the shock (answer 1556). */
  public boolean armed() {
    return robot.workArea().work().patrol().hasCard() && robot.powerState() == PowerState.ACTIVE;
  }

  public boolean fighting() {
    return armed() && target != null && target.isAlive();
  }

  /**
   * In a fight, checking on its owner after it or investigating, so the round waits (answers 1624
   * and 1629).
   */
  boolean busy() {
    PatrolWatch.Phase phase = watch.phase();
    return fighting()
        || armed()
            && (phase == PatrolWatch.Phase.OWNER_CHECK || phase == PatrolWatch.Phase.INVESTIGATION);
  }

  /**
   * The errands wait: the trip to a chest and the fetch of an item stop in a fight and come back
   * after it, and none starts with a hostile in its search (answer 1640).
   */
  boolean holdsErrands() {
    return busy() || armed() && threatNear;
  }

  /** Where it walks to investigate: the last place it saw the hostile (answer 1624). */
  Optional<Vec3> investigationSpot() {
    return trail.lastSeen();
  }

  /** It looked around the last place: back to the round, with the visor up (answer 1629). */
  void searched() {
    watch = watch.searched();
    trail.clear();
  }

  /** What it is doing as a lookout, which the look of the visor follows. */
  public PatrolWatch watch() {
    return watch;
  }

  Optional<LivingEntity> target() {
    return fighting() ? Optional.of(target) : Optional.empty();
  }

  PatrollerSpec spec() {
    return robot.spec().jobs().patroller();
  }

  static PatrolSettings settings() {
    return CubitMod.CONFIG.patrol().settings();
  }

  /** Called by the defense on every tick, in place of the shock, while it has the card. */
  void tick() {
    PatrolArmor.apply(robot, armed() ? spec().armor() : 0);
    PatrolTeams.update(robot, armed());
    glide();
    if (!armed()) {
      drop();
      watch = PatrolWatch.START;
      trail.clear();
      threatNear = false;
      return;
    }
    watch = watch.tick(timings());
    if (fighting()) {
      trail.tick(robot, target, weapon() == WeaponChoice.PISTOL);
    }
    long now = robot.level().getGameTime();
    if (now >= nextSearch) {
      nextSearch = now + CubitMod.CONFIG.patrol().searchIntervalTicks();
      search();
    }
    if (fighting()) {
      act(now);
    }
  }

  /** The wings open in a fall: no fall damage and no energy (answers 1564 and 1571). */
  private void glide() {
    if (!armed() || robot.onGround() || robot.getDeltaMovement().y > FALLING) {
      return;
    }
    Vec3 motion = robot.getDeltaMovement();
    robot.setDeltaMovement(motion.x, Math.max(motion.y, GLIDE_FALL_SPEED), motion.z);
    robot.resetFallDistance();
  }

  private void search() {
    List<RobotEntity> mates = escorting() ? List.of() : PatrolTeams.mates(robot);
    List<RobotEntity> wounded = wounded(mates);
    List<LivingEntity> pool = pool(mates, wounded);
    Optional<Integer> chosen =
        PatrolTargets.choose(
            pool.stream().map(mob -> PatrolSight.candidate(robot, mob, wounded)).toList(),
            Optional.ofNullable(target).map(LivingEntity::getId));
    LivingEntity next =
        chosen
            .flatMap(id -> pool.stream().filter(mob -> mob.getId() == id).findFirst())
            .orElse(null);
    if (next != null && target == null && !targetOfAny(mates, next)) {
      robot.workArea().work().talk().onWork(JobTalkTopic.PATROLLER_COMBAT);
    }
    follow(next);
  }

  /**
   * The watch follows the target: a new one brings the alert, the last one killed brings the look
   * at the owner, and one gone without a kill ends the fight (answers 1623 and 1629).
   */
  private void follow(LivingEntity next) {
    if (next != null) {
      boolean hurting = PatrolProtection.hurtingSomeone(robot, next);
      watch = watch.sighted(hurting, trail.lookedFor(next), timings());
      trail.follow(next);
    } else if (target != null && target.isDeadOrDying()) {
      watch = watch.killedTheLast(ownerNear(), timings());
      trail.clear();
    } else if (target != null) {
      trail.lost(target);
      watch = watch.lost(trail.lastSeen().filter(this::inReachAt).isPresent());
    }
    target = next;
  }

  private boolean ownerNear() {
    Player owner = RobotInteractions.onlineOwner(robot);
    return owner != null
        && owner.level() == robot.level()
        && timings().ownerNear(owner.distanceTo(robot));
  }

  private static PatrolWatch.Timings timings() {
    return CubitMod.CONFIG.patrol().watch();
  }

  /**
   * What it may fight: the hostiles in its search, the targets of its team, which answers the call,
   * and the hostiles near a wounded one, all within its own chase (answers 1643, 1644 and 1691).
   */
  private List<LivingEntity> pool(List<RobotEntity> mates, List<RobotEntity> wounded) {
    Set<LivingEntity> pool = new LinkedHashSet<>();
    List<LivingEntity> seen = PatrolSight.hostiles(robot, spec().searchRadius());
    threatNear = !seen.isEmpty();
    seen.stream().filter(mob -> trail.mayChoose(robot, mob, timings())).forEach(pool::add);
    mates.forEach(mate -> mate.workArea().work().patrolCombat().target().ifPresent(pool::add));
    int rescue = team().rescueRadius();
    wounded.forEach(one -> pool.addAll(PatrolSight.hostiles(one, rescue)));
    return pool.stream()
        .filter(mob -> mob.isAlive() && PatrolProtection.isTarget(robot, mob) && inReach(mob))
        .filter(mob -> !trail.gone(mob, timings()))
        .toList();
  }

  /** The Patrollers of the team under the low health, itself among them (answer 1691). */
  private List<RobotEntity> wounded(List<RobotEntity> mates) {
    PatrolTeam team = team();
    return Stream.concat(Stream.of(robot), mates.stream())
        .filter(one -> team.wounded(one.getHealth(), one.getMaxHealth()))
        .toList();
  }

  /** Only one of the team speaks when a fight begins (answer 1646). */
  private static boolean targetOfAny(List<RobotEntity> mates, LivingEntity mob) {
    return mates.stream()
        .anyMatch(mate -> mate.workArea().work().patrolCombat().target().orElse(null) == mob);
  }

  /** On escort it never leaves the one it follows for a call (answers 1644 and 1691). */
  private boolean escorting() {
    return RobotInteractions.followedPlayer(robot) != null;
  }

  /** Within the chase, measured from what it guards (answers 1562, 1565 and 1636). */
  boolean inReach(LivingEntity mob) {
    return inReachAt(mob.position());
  }

  private boolean inReachAt(Vec3 at) {
    return PatrolRules.mayChase(guardDistance(at), settings().chaseDistance());
  }

  /**
   * How far a place is from what it guards: the one it escorts, else the nearest point of its
   * route, else the base of its area, else itself (answers 1565 and 1636).
   */
  private double guardDistance(Vec3 at) {
    Player escorted = RobotInteractions.followedPlayer(robot);
    if (escorted != null) {
      return escorted.position().distanceTo(at);
    }
    OptionalDouble toRoute =
        robot.workArea().work().patrol().walkable().points().stream()
            .mapToDouble(point -> topOf(point).distanceTo(at))
            .min();
    if (toRoute.isPresent()) {
      return toRoute.getAsDouble();
    }
    return robot
        .workArea()
        .here()
        .map(area -> area.distanceToBase(at.x, at.z))
        .orElse(robot.position().distanceTo(at));
  }

  private static Vec3 topOf(RoutePoint point) {
    return Vec3.atBottomCenterOf(new BlockPos(point.x(), point.y(), point.z()).above());
  }

  private static PatrolTeam team() {
    return CubitMod.CONFIG.patrol().team();
  }

  private void act(long now) {
    robot.getLookControl().setLookAt(target);
    if (!watch.attacks()) {
      face(target);
      return;
    }
    WeaponChoice choice = weapon();
    if (choice == WeaponChoice.PISTOL) {
      weapons.shoot(target, spec(), now);
    } else if (choice == WeaponChoice.BLADE
        && robot.distanceTo(target) <= spec().weapons().bladeReach()) {
      weapons.swing(target, spec(), now);
    }
    if (choice != WeaponChoice.PISTOL) {
      jumpIfNeeded();
    }
  }

  /** On the alert, the body turns to the hostile with the head (answer 1623). */
  private void face(LivingEntity mob) {
    Vec3 toward = mob.position().subtract(robot.position());
    float yaw = (float) (Math.toDegrees(Math.atan2(toward.z, toward.x)) - QUARTER_TURN);
    robot.setYRot(yaw);
    robot.setYBodyRot(yaw);
  }

  /**
   * A target the orb cannot hurt: the enderman, which dodges it as it dodges arrows, and the mobs
   * of the game that send projectiles back, as the breeze (answers 1695 and 1696).
   */
  private static boolean shotUseless(LivingEntity mob) {
    return mob instanceof EnderMan || mob.getType().is(EntityTypeTags.DEFLECTS_PROJECTILES);
  }

  /** Under the low health it fights with care, by the pistol, from a distance (answer 1686). */
  boolean cautious() {
    return team().wounded(robot.getHealth(), robot.getMaxHealth());
  }

  /** What the movement goal reads to approach, stop or keep away. */
  WeaponChoice weapon() {
    if (!fighting()) {
      return WeaponChoice.NONE;
    }
    return WeaponChoice.choose(
        new WeaponChoice.Check(
            robot.distanceTo(target),
            inAir(target),
            target instanceof Creeper,
            spec().canShoot(robot.battery()) && robot.hasLineOfSight(target),
            robot.distanceTo(target) <= spec().weapons().pistolRange(),
            settings().weaponSwitchDistance(),
            cautious(),
            shotUseless(target)));
  }

  private void jumpIfNeeded() {
    double height = target.getY() - robot.getY();
    boolean shot =
        spec().canShoot(robot.battery()) && robot.hasLineOfSight(target) && !shotUseless(target);
    PatrolRules.JumpCheck check =
        new PatrolRules.JumpCheck(
            inAir(target),
            height,
            spec().jump().height(),
            shot,
            spec().canJump(robot.battery()),
            robot.onGround());
    if (PatrolRules.jumps(check)) {
      robot.setBattery(spec().payForJump(robot.battery()));
      double speed = Math.sqrt(2 * JUMP_GRAVITY * height) + JUMP_GRAVITY;
      Vec3 toward = target.position().subtract(robot.position()).multiply(1, 0, 1).normalize();
      robot.setDeltaMovement(toward.x * JUMP_GRAVITY, speed, toward.z * JUMP_GRAVITY);
      robot.hasImpulse = true;
    }
  }

  private static boolean inAir(LivingEntity mob) {
    return !mob.onGround() && !mob.isInWater() && mob.getY() - floorBelow(mob) > IN_AIR_BLOCKS;
  }

  private static double floorBelow(LivingEntity mob) {
    return mob.level()
        .getHeight(
            net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
            mob.getBlockX(),
            mob.getBlockZ());
  }

  void drop() {
    target = null;
  }
}
