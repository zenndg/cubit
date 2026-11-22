package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.domain.behavior.PatrolRules;
import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import io.github.zenndg.cubit.domain.behavior.PatrolTargets;
import io.github.zenndg.cubit.domain.behavior.WeaponChoice;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.tier.PatrollerSpec;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;

/**
 * The fight of the Galactic Patroller, in place of the shock: it picks a hostile, chooses the
 * weapon by the distance, shoots its orb or swings its blade, jumps at a target in the air it
 * cannot shoot and glides when it falls (answers 1556, 1557, 1564, 1568 and 1596). The search runs
 * on an interval, never on every tick (rule 21).
 */
public final class RobotPatrolCombat {
  private static final int SEARCH_TICKS = 10;
  private static final double BLADE_REACH = 2.5;
  private static final double IN_AIR_BLOCKS = 1.5;
  private static final double GLIDE_FALL_SPEED = -0.12;
  private static final double JUMP_GRAVITY = 0.08;
  private static final double FALLING = -0.08;

  private final RobotEntity robot;
  private final RobotPatrolWeapons weapons;
  private LivingEntity target;
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

  Optional<LivingEntity> target() {
    return fighting() ? Optional.of(target) : Optional.empty();
  }

  PatrollerSpec spec() {
    return robot.spec().jobs().patroller();
  }

  static PatrolSettings settings() {
    return CubitMod.CONFIG.patrolSettings();
  }

  /** Called by the defense on every tick, in place of the shock, while it has the card. */
  void tick() {
    PatrolArmor.apply(robot, armed() ? spec().armor() : 0);
    glide();
    if (!armed()) {
      drop();
      return;
    }
    long now = robot.level().getGameTime();
    if (now >= nextSearch) {
      nextSearch = now + SEARCH_TICKS;
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
    List<LivingEntity> hostiles = PatrolSight.hostiles(robot, spec().searchRadius());
    Optional<Integer> chosen =
        PatrolTargets.choose(
            hostiles.stream().map(mob -> PatrolSight.candidate(robot, mob)).toList());
    LivingEntity next =
        chosen
            .flatMap(id -> hostiles.stream().filter(mob -> mob.getId() == id).findFirst())
            .orElse(null);
    if (next != null && target == null) {
      robot.workArea().work().talk().onWork(JobTalkTopic.PATROLLER_COMBAT);
    }
    target = next;
    if (target == null) {
      PatrolReservations.release(robot.getUUID());
    } else {
      PatrolReservations.reserve(target.getUUID(), robot.getUUID());
    }
  }

  private void act(long now) {
    robot.getLookControl().setLookAt(target);
    WeaponChoice choice = weapon();
    if (choice == WeaponChoice.PISTOL) {
      weapons.shoot(target, spec(), now);
    } else if (choice == WeaponChoice.BLADE && robot.distanceTo(target) <= BLADE_REACH) {
      weapons.swing(target, spec(), now);
    }
    if (choice != WeaponChoice.PISTOL) {
      jumpIfNeeded();
    }
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
            settings().weaponSwitchDistance()));
  }

  private void jumpIfNeeded() {
    double height = target.getY() - robot.getY();
    boolean shot = spec().canShoot(robot.battery()) && robot.hasLineOfSight(target);
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
    PatrolReservations.release(robot.getUUID());
  }
}
