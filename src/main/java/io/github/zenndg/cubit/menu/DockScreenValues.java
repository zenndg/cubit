package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DockedRobot;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.SolarReading;
import io.github.zenndg.cubit.domain.robot.HealTime;

/** The numbers of the dock screen, with the ones of the robot in the base. */
final class DockScreenValues extends ScreenValues {
  static final int STOCK = 0;
  static final int CAPACITY = 2;
  static final int INPUT = 4;
  static final int STATUS = 6;
  static final int WAITING = 7;
  static final int ROBOT_PRESENT = 8;
  static final int ROBOT_CHARGE = 9;
  static final int ROBOT_CAPACITY = 11;
  static final int ROBOT_HEALTH = 13;
  static final int ROBOT_MAX_HEALTH = 14;
  static final int ROBOT_RECHARGE = 15;
  static final int HEAL_KIND = 17;
  static final int HEAL_MINUTES = 18;
  static final int EXPRESSION = 19;
  static final int TIER = 20;
  static final int SOLAR = 21;
  static final int SUN_STATE = 23;
  static final int SOLAR_MAX = 24;
  static final int PANEL = 26;
  static final int SOLAR_NIGHT = 27;
  static final int STANDBY = 28;
  static final int FULL = 30;
  static final int COUNT = 31;

  private final InductionDockBlockEntity dock;

  DockScreenValues(InductionDockBlockEntity dock) {
    super(COUNT);
    this.dock = dock;
  }

  @Override
  void refresh() {
    put(STOCK, dock.energy());
    put(CAPACITY, dock.capacity());
    put(INPUT, dock.inputShown());
    put(STANDBY, dock.standbyCost());
    put(FULL, dock.isFull());
    SolarReading solar = dock.solar();
    put(SOLAR, solar.given());
    put(SUN_STATE, solar.state().ordinal());
    put(SOLAR_MAX, solar.max());
    put(PANEL, solar.panel().ordinal());
    put(SOLAR_NIGHT, solar.givesAtNight());
    put(STATUS, dock.status().ordinal());
    put(WAITING, dock.waitingCount());
    put(ROBOT_PRESENT, false);
    dock.dockedRobot().ifPresent(this::putRobot);
  }

  private void putRobot(DockedRobot robot) {
    put(ROBOT_PRESENT, true);
    put(ROBOT_CHARGE, robot.battery().charge());
    put(ROBOT_CAPACITY, robot.battery().capacity());
    put(ROBOT_HEALTH, (int) Math.ceil(robot.health()));
    put(ROBOT_MAX_HEALTH, (int) Math.ceil(robot.maxHealth()));
    put(ROBOT_RECHARGE, dock.rechargePerTick());
    HealTime heal =
        HealTime.of(robot.health(), robot.maxHealth(), CubitMod.CONFIG.dock().fullHealTicks());
    put(HEAL_KIND, heal.kind().ordinal());
    put(HEAL_MINUTES, heal.minutes());
    put(EXPRESSION, robot.expression().ordinal());
    put(TIER, robot.tier().ordinal());
  }
}
