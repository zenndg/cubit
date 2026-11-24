package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.ScanRules;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.xp.XpIntake;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The orbs of the ground, taken as the items: by touch and, with the magnet, pulled and paid once
 * each (answer 1822). The ones the game already takes to another player stay for that player
 * (1933).
 */
final class RobotXpOrbs {
  private static final double PULL_SPEED = 0.5;
  private static final float VOLUME = 0.1f;

  /** A little lower than the sound of a player taking an orb (answer 1848). */
  private static final float PITCH = 0.7f;

  private final RobotEntity robot;
  private final RobotXp xp;
  private final Map<UUID, ExperienceOrb> pulled = new HashMap<>();

  RobotXpOrbs(RobotEntity robot, RobotXp xp) {
    this.robot = robot;
    this.xp = xp;
  }

  /**
   * The touch box and the magnet are the ones of the pickup, which calls only while it collects.
   */
  void tick(AABB touch, AABB magnet, BooleanSupplier payForPull) {
    if (!XpIntake.collects(xp.tank(), xp.state().settings(), RobotXp.rate())) {
      forget();
      return;
    }
    steer(touch);
    if (ScanRules.isDue(robot.tickCount, CubitMod.CONFIG.pickupCheckTicks())) {
      wanted(touch).forEach(this::collect);
    }
    if (ScanRules.isDue(robot.tickCount, CubitMod.CONFIG.magnetCheckTicks())) {
      pull(magnet, payForPull);
    }
  }

  void forget() {
    pulled.clear();
  }

  private List<ExperienceOrb> wanted(AABB box) {
    return robot.level().getEntitiesOfClass(ExperienceOrb.class, box, this::free);
  }

  private boolean free(ExperienceOrb orb) {
    return orb.isAlive()
        && orb.value > 0
        && (orb.followingPlayer == null
            || Ownership.owns(robot.ownerId(), orb.followingPlayer.getUUID()));
  }

  private void steer(AABB touch) {
    pulled.values().removeIf(orb -> !free(orb));
    for (ExperienceOrb orb : List.copyOf(pulled.values())) {
      if (orb.getBoundingBox().intersects(touch)) {
        collect(orb);
      } else {
        Vec3 toRobot = robot.position().subtract(orb.position());
        orb.setDeltaMovement(toRobot.normalize().scale(Math.min(PULL_SPEED, toRobot.length())));
      }
    }
  }

  /** Each orb is paid once, however many checks it takes to arrive, as an item (answer 877). */
  private void pull(AABB magnet, BooleanSupplier payForPull) {
    Set<UUID> inRange = new HashSet<>();
    for (ExperienceOrb orb : wanted(magnet)) {
      if (!pulled.containsKey(orb.getUUID()) && !payForPull.getAsBoolean()) {
        continue;
      }
      inRange.add(orb.getUUID());
      pulled.put(orb.getUUID(), orb);
    }
    pulled.keySet().retainAll(inRange);
  }

  private void collect(ExperienceOrb orb) {
    if (!orb.isAlive() || !(robot.level() instanceof ServerLevel level)) {
      return;
    }
    int points = (int) Math.min((long) orb.value * orb.count, Integer.MAX_VALUE);
    XpIntake.Orb taken = XpIntake.ofOrb(xp.tank(), xp.state().settings(), points, RobotXp.rate());
    if (taken.taken() == 0) {
      return;
    }
    xp.setTank(taken.tank());
    pulled.remove(orb.getUUID());
    robot.take(orb, 1);
    shrink(level, orb, points - taken.taken());
    level.playSound(
        null,
        robot.blockPosition(),
        SoundEvents.EXPERIENCE_ORB_PICKUP,
        SoundSource.NEUTRAL,
        VOLUME,
        PITCH);
  }

  /** The whole orbs of the rest stay, and a broken one falls as a smaller orb (answer 2065). */
  private static void shrink(ServerLevel level, ExperienceOrb orb, int left) {
    int broken = left % orb.value;
    orb.count = left / orb.value;
    if (orb.count == 0) {
      orb.discard();
    }
    if (broken > 0) {
      ExperienceOrb.award(level, orb.position(), broken);
    }
  }
}
