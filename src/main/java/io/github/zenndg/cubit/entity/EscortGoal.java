package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.FollowAction;
import io.github.zenndg.cubit.domain.behavior.FollowDecision;
import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * The marked escort of the Galactic Patroller: it follows the escorted one by the rules of
 * following, with the distance, the teleport when far and the wings, glances to each side at the
 * end of each leg of the round, and stops to look around when the escorted one stands still
 * (answers 1654, 1657 and 1744). A fight comes first, by its priority.
 */
final class EscortGoal extends Goal {
  private static final double SPEED_MODIFIER = 1;
  private static final int GLANCE_TICKS = 10;
  private static final float GLANCE_ANGLE = 60f;
  private static final float LOOK_SPEED = 6f;
  private static final double STILL_BLOCKS = 0.05;

  private final RobotEntity robot;
  private final FollowDecision decision;
  private final PatrolPause pause;
  private int repathCountdown;
  private double walked;
  private int leg;
  private int glance;
  private int still;
  private boolean stoppedForIt;
  private Vec3 lastEscorted;
  private Vec3 lastSelf;

  EscortGoal(RobotEntity robot, FollowDecision decision) {
    this.robot = robot;
    this.decision = decision;
    this.pause = new PatrolPause(robot);
    setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    RobotWork work = robot.workArea().work();
    return work.patrol().hasCard()
        && work.patrolCombat().armed()
        && !work.patrolCombat().busy()
        && escorted().isPresent();
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
    walked = 0;
    leg = drawLeg();
    glance = 0;
    still = 0;
    stoppedForIt = false;
    lastEscorted = null;
    lastSelf = null;
    pause.reset();
  }

  @Override
  public void stop() {
    robot.getNavigation().stop();
  }

  @Override
  public void tick() {
    LivingEntity escorted = escorted().orElse(null);
    if (escorted == null || robot.workArea().work().patrolCombat().wings().flying()) {
      return;
    }
    if (pause.hold()) {
      return;
    }
    if (standsStill(escorted)) {
      robot.workArea().work().patrol().stopHere();
      stoppedForIt = true;
      return;
    }
    follow(escorted);
    glanceWhileWalking();
  }

  /** It stops once each time the escorted one stands still long enough (answer 1744). */
  private boolean standsStill(LivingEntity escorted) {
    Vec3 now = escorted.position();
    boolean moved = lastEscorted != null && now.distanceTo(lastEscorted) > STILL_BLOCKS;
    lastEscorted = now;
    if (moved) {
      still = 0;
      stoppedForIt = false;
      return false;
    }
    return !stoppedForIt && ++still >= CubitMod.CONFIG.patrol().escortStillTicks();
  }

  private void follow(LivingEntity escorted) {
    if (--repathCountdown > 0) {
      return;
    }
    repathCountdown = CubitMod.CONFIG.followRepathTicks();
    boolean sameLevel = escorted.level() == robot.level();
    FollowAction action =
        decision.decide(robot.powerState(), robot.distanceTo(escorted), sameLevel);
    if (action == FollowAction.TELEPORT) {
      FollowOwnerGoal.teleportNear(robot, escorted, decision.stopDistanceBlocks());
      lastSelf = null;
    } else if (action == FollowAction.FOLLOW) {
      robot
          .workArea()
          .work()
          .patrolCombat()
          .wings()
          .moveTo(escorted.position(), SPEED_MODIFIER, false);
    } else {
      robot.getNavigation().stop();
    }
  }

  /** At the end of each leg it glances once to each side, without stopping (answer 1744). */
  private void glanceWhileWalking() {
    Vec3 now = robot.position();
    if (lastSelf != null) {
      walked += now.subtract(lastSelf).horizontalDistance();
    }
    lastSelf = now;
    if (glance > 0) {
      float turn = glance > GLANCE_TICKS ? GLANCE_ANGLE : -GLANCE_ANGLE;
      Vec3 look = Vec3.directionFromRotation(0, robot.getYRot() + turn).add(robot.getEyePosition());
      robot.getLookControl().setLookAt(look.x, look.y, look.z, LOOK_SPEED, LOOK_SPEED);
      glance--;
    } else if (walked >= leg) {
      walked = 0;
      leg = drawLeg();
      glance = GLANCE_TICKS * 2;
    }
  }

  private int drawLeg() {
    PatrolSettings.Round round = CubitMod.CONFIG.patrol().settings().round();
    return round.stopDistance(
        robot
            .getRandom()
            .nextIntBetweenInclusive(round.stopMinDistance(), round.stopMaxDistance()));
  }

  private Optional<LivingEntity> escorted() {
    return robot.workArea().work().escort().escortedHere();
  }
}
