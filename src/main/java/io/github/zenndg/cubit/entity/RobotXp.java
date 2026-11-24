package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.xp.XpState;
import io.github.zenndg.cubit.domain.xp.XpTank;

/**
 * The XP tank of a Cubit, with its mode (answers 1826 and 1827). The rules live in the domain, and
 * the capacity follows the tier.
 */
public final class RobotXp {
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
}
