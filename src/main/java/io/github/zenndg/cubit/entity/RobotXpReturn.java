package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.xp.XpFlow;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.domain.xp.XpNotice;
import io.github.zenndg.cubit.domain.xp.XpReturn;
import io.github.zenndg.cubit.domain.xp.XpSettings;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * The XP of a Cubit and the Dock: in the mode Return to Dock, a full tank goes there as a low
 * battery does, without the switch of that return (answer 1934); docked, the XP passes to the Dock,
 * and the owner reads once that the tank is full (answers 1835 and 1840).
 */
public final class RobotXpReturn {
  public static final String FULL_KEY = "notice.cubit.xp_tank_full";

  private final RobotEntity robot;
  private final RobotPreferredDock docks;
  private XpNotice notice = XpNotice.START;
  private XpFlow flow = XpFlow.START;
  private long nextCheck = Long.MIN_VALUE;

  RobotXpReturn(RobotEntity robot, RobotPreferredDock docks) {
    this.robot = robot;
    this.docks = docks;
  }

  private RobotXp xp() {
    return robot.pickup().xp();
  }

  /** Called on the server while the robot is active, and looks once per interval (rule 21). */
  void tick(boolean onTrip) {
    long now = robot.level().getGameTime();
    if (now < nextCheck) {
      return;
    }
    nextCheck = now + CubitMod.CONFIG.followRepathTicks();
    boolean full = xp().isFull();
    Optional<BlockPos> found = full && !onTrip ? docks.dockToGo() : Optional.empty();
    found
        .filter(spot -> goes())
        .ifPresent(spot -> {
          robot.workArea().work().patrolCombat().leaveForTheDock();
          robot.callToDock(spot);
        });
    tell(full, onTrip || found.isPresent());
  }

  private boolean goes() {
    return XpReturn.returns(
        xp().state().settings().mode(),
        robot.powerState(),
        true,
        RobotAutoReturn.onDuty(robot),
        robot.workArea().work().patrolCombat().holdsTheReturn());
  }

  /** In the store that deletes nothing, and in the return with no Dock to go to (answer 1840). */
  private void tell(boolean full, boolean going) {
    XpSettings settings = xp().state().settings();
    boolean waits =
        switch (settings.mode()) {
          case IGNORE -> false;
          case STORE -> !settings.deleteExcess();
          case RETURN_TO_DOCK -> !going;
        };
    boolean tells = full && waits;
    if (full && !waits && settings.mode() == XpMode.RETURN_TO_DOCK) {
      return;
    }
    if (notice.tells(tells) && !say()) {
      return;
    }
    notice = notice.update(tells);
  }

  private boolean say() {
    Player owner = RobotInteractions.onlineOwner(robot);
    if (owner == null || !robot.chatter().switchedOn()) {
      return false;
    }
    owner.displayClientMessage(Component.translatable(FULL_KEY, robot.getName()), true);
    robot.playSound(CubitSounds.EXPRESSION_BEEP.get());
    return true;
  }

  /** While docked, for any reason and whatever the energy (answer 1835 and detail 4). */
  void pass(InductionDockBlockEntity dock) {
    XpFlow.Step step = flow.drain(xp().tank(), dock.xp().transferPerTick(), RobotXp.rate());
    flow = step.flow();
    if (step.points() > 0) {
      xp().setTank(step.from());
      dock.receiveXp(step.points());
    }
  }

  /** It leaves by itself with an empty tank, and the Dock always takes it (1831 and 2067). */
  boolean done() {
    return xp().tank().isEmpty(RobotXp.rate());
  }

  void left() {
    flow = XpFlow.START;
  }
}
