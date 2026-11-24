package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.domain.xp.XpIntake;
import io.github.zenndg.cubit.domain.xp.XpRate;
import io.github.zenndg.cubit.domain.xp.XpState;
import io.github.zenndg.cubit.domain.xp.XpTank;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;

/**
 * The XP tank of a Cubit, with its mode, and what it gets from its work (answers 1819 and 1827).
 * The rules live in the domain, and the capacity follows the tier.
 */
public final class RobotXp {
  /** A catch of the rod of the game gives from 1 to 6 points (answer 1820). */
  private static final int CATCH_POINTS = 6;

  private final RobotEntity robot;
  private XpState state = XpState.EMPTY;

  RobotXp(RobotEntity robot) {
    this.robot = robot;
  }

  public XpState state() {
    return state;
  }

  public void setState(XpState next) {
    state = next;
  }

  public XpTank tank() {
    return state.tank(robot.spec().xp().tankCapacity());
  }

  void setTank(XpTank tank) {
    state = new XpState(tank.fluid(), state.settings());
  }

  static XpRate rate() {
    return CubitMod.CONFIG.xp().rate();
  }

  /** Only an active Cubit, and only the jobs of xp_jobs, which the pack may change (1839, 1844). */
  boolean takesFrom(RobotJob job) {
    return robot.powerState() == PowerState.ACTIVE && CubitMod.CONFIG.xp().jobs().contains(job);
  }

  /** Every catch gives XP, also the junk it throws away (answer 2066). */
  void onCatch() {
    if (takesFrom(RobotJob.FISHER)) {
      onWork(robot.getRandom().nextInt(CATCH_POINTS) + 1);
    }
  }

  /** The game gives no XP for a harvest, so this is a chance of the mod (answer 1823). */
  void onHarvest() {
    if (takesFrom(RobotJob.FARMER)
        && robot.getRandom().nextInt(Percentage.MAX)
            < CubitMod.CONFIG.xp().farmerChance().value()) {
      onWork(1);
    }
  }

  void onServed(BlockPos oven) {
    if (takesFrom(RobotJob.COOK)) {
      onWork(CookOvens.takeExperience(robot.level(), oven, robot.getRandom()));
    }
  }

  /** Straight to the tank, and what does not fit falls beside the Cubit, so nothing is lost. */
  void onWork(int points) {
    if (points <= 0 || !(robot.level() instanceof ServerLevel level)) {
      return;
    }
    XpIntake.Work taken = XpIntake.ofWork(tank(), state.settings(), points, rate());
    setTank(taken.tank());
    if (taken.spilled() > 0) {
      ExperienceOrb.award(level, robot.position(), taken.spilled());
    }
  }
}
