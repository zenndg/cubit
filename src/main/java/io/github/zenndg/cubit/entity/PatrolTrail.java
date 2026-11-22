package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * The trail of the target of the Galactic Patroller: how long it has been out of sight, where it
 * was last seen and which hostile it looks for after losing it (answers 1624, 1693, 1694, 1721 and
 * 1722). A target in the water it cannot shoot counts as out of sight, as it never swims after it.
 */
final class PatrolTrail {
  private LivingEntity followed;
  private int unseen;
  private Vec3 lastSeen;
  private LivingEntity lookedFor;

  /** A new target: seen now, where it stands. */
  void follow(LivingEntity target) {
    if (target != followed) {
      followed = target;
      unseen = 0;
      lastSeen = target.position();
    }
  }

  /** Each tick of the fight: in sight, or one more tick out of it (answers 1693 and 1721). */
  void tick(RobotEntity robot, LivingEntity target, boolean canShoot) {
    if (target == null || target != followed) {
      return;
    }
    boolean reachable = !target.isInWater() || robot.isInWater() || canShoot;
    if (robot.hasLineOfSight(target) && reachable) {
      unseen = 0;
      lastSeen = target.position();
    } else {
      unseen++;
    }
  }

  /** The target out of sight long enough to be gone (answer 1721). */
  boolean gone(LivingEntity mob, PatrolWatch.Timings times) {
    return mob == followed && times.outOfSight(unseen);
  }

  /**
   * A hostile it may choose from its own search: one in sight, or the target it still follows. A
   * hostile behind a wall is not seen, so it never brings an alert (answer 1623).
   */
  boolean mayChoose(RobotEntity robot, LivingEntity mob, PatrolWatch.Timings times) {
    if (mob == followed) {
      return !gone(mob, times);
    }
    return robot.hasLineOfSight(mob);
  }

  /** The target got away: it looks for it where it was last seen (answer 1624). */
  void lost(LivingEntity target) {
    lookedFor = target;
    followed = null;
  }

  boolean lookedFor(LivingEntity mob) {
    return mob == lookedFor;
  }

  Optional<Vec3> lastSeen() {
    return Optional.ofNullable(lastSeen);
  }

  /** The search is over, or the fight is: nothing left to look for. */
  void clear() {
    followed = null;
    lookedFor = null;
    unseen = 0;
    lastSeen = null;
  }
}
