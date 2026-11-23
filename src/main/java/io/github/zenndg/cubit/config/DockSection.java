package io.github.zenndg.cubit.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** The config keys of the Induction Dock, valid for every robot. The names are a contract. */
public final class DockSection {
  private static final String INPUT = "dock_input_per_tick";
  private static final int INPUT_DEFAULT = 5_000;
  private static final String FULL_HEAL = "dock_full_heal_ticks";
  private static final int FULL_HEAL_DEFAULT = 1_200;
  private static final int FULL_HEAL_MAX = 72_000;
  private static final String STUCK = "dock_stuck_ticks";
  private static final int STUCK_DEFAULT = 100;
  private static final int STUCK_MAX = 1_200;
  private static final String COMPLETE_HOLD = "dock_complete_hold_ticks";
  private static final int COMPLETE_HOLD_DEFAULT = 60;
  private static final int COMPLETE_HOLD_MAX = 1_200;
  private static final int MIN = 1;
  private static final String SOLAR_DELAY = "dock_solar_deploy_delay_ticks";
  private static final int SOLAR_DELAY_DEFAULT = 60;
  private static final int SOLAR_DELAY_MAX = 1_200;
  private static final int NO_DELAY = 0;

  private final ModConfigSpec.IntValue input;
  private final ModConfigSpec.IntValue fullHealTicks;
  private final ModConfigSpec.IntValue stuckTicks;
  private final ModConfigSpec.IntValue completeHoldTicks;
  private final ModConfigSpec.IntValue solarDelayTicks;

  DockSection(ModConfigSpec.Builder builder) {
    input = builder.defineInRange(INPUT, INPUT_DEFAULT, MIN, Integer.MAX_VALUE);
    fullHealTicks = builder.defineInRange(FULL_HEAL, FULL_HEAL_DEFAULT, MIN, FULL_HEAL_MAX);
    stuckTicks = builder.defineInRange(STUCK, STUCK_DEFAULT, MIN, STUCK_MAX);
    completeHoldTicks =
        builder.defineInRange(COMPLETE_HOLD, COMPLETE_HOLD_DEFAULT, MIN, COMPLETE_HOLD_MAX);
    solarDelayTicks =
        builder.defineInRange(SOLAR_DELAY, SOLAR_DELAY_DEFAULT, NO_DELAY, SOLAR_DELAY_MAX);
  }

  /** The sky in a row before the panel leaves or goes back to the cabinet (answer 1856). */
  public int solarDelayTicks() {
    return solarDelayTicks.get();
  }

  public int inputPerTick() {
    return input.get();
  }

  public int fullHealTicks() {
    return fullHealTicks.get();
  }

  public int completeHoldTicks() {
    return completeHoldTicks.get();
  }

  public int stuckTicks() {
    return stuckTicks.get();
  }
}
