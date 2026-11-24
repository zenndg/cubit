package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.behavior.RobotNotice;
import io.github.zenndg.cubit.domain.dock.AutoReturn;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.menu.RobotSwitch;
import io.github.zenndg.cubit.registry.CubitBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * The auto return: with a low battery, the Cubit that follows, or works in an area, goes to the
 * same Dock as `Go to Dock`, says it once and, without a Dock in reach, tries again on the next
 * interval (answers 1053 and 1065 to 1068). It looks only once per interval of the dock search
 * (rule 21).
 */
public final class RobotAutoReturn implements RobotSwitch {
  public static final String HEADING_KEY = "notice.cubit.patroller_heading_to_dock";
  public static final String NO_DOCK_KEY = "notice.cubit.patroller_no_dock";

  private final RobotEntity robot;
  private final RobotPreferredDock docks;
  private boolean switchedOn = true;
  private long nextCheck = Long.MIN_VALUE;
  private boolean told;
  private boolean toldToDock;

  RobotAutoReturn(RobotEntity robot, RobotPreferredDock docks) {
    this.robot = robot;
    this.docks = docks;
  }

  boolean switchedOn() {
    return switchedOn;
  }

  void setSwitchedOn(boolean switchedOn) {
    this.switchedOn = switchedOn;
  }

  @Override
  public boolean isOn() {
    return switchedOn;
  }

  @Override
  public boolean isAvailable() {
    return robot.powerState() != PowerState.HIBERNATING;
  }

  @Override
  public void toggle() {
    switchedOn = !switchedOn;
    nextCheck = Long.MIN_VALUE;
  }

  /** Called on the server while the robot is active. */
  void tick(boolean onTrip) {
    long now = robot.level().getGameTime();
    if (now < nextCheck) {
      return;
    }
    nextCheck = now + CubitMod.CONFIG.followRepathTicks();
    RobotPatrolCombat guard = robot.workArea().work().patrolCombat();
    AutoReturn rule = new AutoReturn(switchedOn, returnCharge());
    if (!rule.returns(
        robot.powerState(), robot.battery(), onDuty(robot), onTrip, guard.holdsTheReturn())) {
      told = false;
      return;
    }
    Optional<BlockPos> dock = docks.dockToGo();
    dock.ifPresent(spot -> {
      guard.leaveForTheDock();
      robot.callToDock(spot);
    });
    tell(dock);
  }

  /** Following, working or, as the Patroller, escorting, so it goes back from it too (1689). */
  static boolean onDuty(RobotEntity robot) {
    return RobotInteractions.followedPlayer(robot) != null
        || robot.workArea().working()
        || robot.workArea().work().escort().active();
  }

  /**
   * The Patroller keeps on guard longer, so it goes back at a lower charge of its own (answer
   * 1688).
   */
  private Percentage returnCharge() {
    return robot.workArea().work().patrol().hasCard()
        ? robot.spec().jobs().patroller().returnCharge()
        : robot.spec().returnCharge();
  }

  /**
   * The Patroller tells its owner on the action bar, with its name, once, in place of the text of
   * every Cubit, and only with its talk on (answers 1690 and 1699).
   */
  private void tell(Optional<BlockPos> dock) {
    boolean toDock = dock.isPresent();
    if (!robot.workArea().work().patrol().hasCard()) {
      if (toDock) {
        robot.notices().show(RobotNotice.NEED_RECHARGE);
      }
      return;
    }
    Player owner = RobotInteractions.onlineOwner(robot);
    // A Dock found after the notice without one is told too, once (answer 1690).
    if (owner == null || !robot.chatter().switchedOn() || told && toldToDock == toDock) {
      return;
    }
    told = true;
    toldToDock = toDock;
    Component text =
        toDock
            ? Component.translatable(HEADING_KEY, robot.getName(), dockName(dock.get()))
            : Component.translatable(NO_DOCK_KEY, robot.getName());
    owner.displayClientMessage(text, true);
  }

  /** The name of the Dock it goes to, or the name of the block when it is not there (1870). */
  private Component dockName(BlockPos spot) {
    return robot.level().getBlockEntity(spot) instanceof InductionDockBlockEntity dock
        ? Component.literal(dock.identity().name().value())
        : CubitBlocks.INDUCTION_DOCK.get().getName();
  }
}
