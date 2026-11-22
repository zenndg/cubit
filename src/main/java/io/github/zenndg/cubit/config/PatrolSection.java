package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The numbers of the Galactic Patroller that hold for every tier, beside the defense. The names are
 * a contract with the pack (answers 1557, 1559, 1562, 1577 and 1581).
 */
public final class PatrolSection {
  private static final String SLOWNESS_CHANCE = "patroller_slowness_chance";
  private static final String SLOWNESS_TICKS = "patroller_slowness_ticks";
  private static final String HEAD_CHANCE = "patroller_head_chance";
  private static final String CHASE = "patroller_chase_distance";
  private static final String SWITCH = "patroller_weapon_switch_distance";
  private static final String PAUSE = "patroller_point_pause_ticks";
  private static final int SLOWNESS_CHANCE_DEFAULT = 20;
  private static final int SLOWNESS_TICKS_DEFAULT = 60;
  private static final int HEAD_CHANCE_DEFAULT = 50;
  private static final int CHASE_DEFAULT = 16;
  private static final int SWITCH_DEFAULT = 4;
  private static final int PAUSE_DEFAULT = 40;
  private static final int MIN_ZERO = 0;
  private static final int MIN_ONE = 1;
  private static final int PERCENT_MAX = 100;
  private static final int BLOCKS_MAX = 64;
  private static final int TICKS_MAX = 72000;

  private final ModConfigSpec.IntValue slownessChance;
  private final ModConfigSpec.IntValue slownessTicks;
  private final ModConfigSpec.IntValue headChance;
  private final ModConfigSpec.IntValue chase;
  private final ModConfigSpec.IntValue weaponSwitch;
  private final ModConfigSpec.IntValue pause;

  PatrolSection(ModConfigSpec.Builder builder) {
    slownessChance =
        builder.defineInRange(SLOWNESS_CHANCE, SLOWNESS_CHANCE_DEFAULT, MIN_ZERO, PERCENT_MAX);
    slownessTicks =
        builder.defineInRange(SLOWNESS_TICKS, SLOWNESS_TICKS_DEFAULT, MIN_ONE, TICKS_MAX);
    headChance = builder.defineInRange(HEAD_CHANCE, HEAD_CHANCE_DEFAULT, MIN_ZERO, PERCENT_MAX);
    chase = builder.defineInRange(CHASE, CHASE_DEFAULT, MIN_ZERO, BLOCKS_MAX);
    weaponSwitch = builder.defineInRange(SWITCH, SWITCH_DEFAULT, MIN_ZERO, BLOCKS_MAX);
    pause = builder.defineInRange(PAUSE, PAUSE_DEFAULT, MIN_ZERO, TICKS_MAX);
  }

  public PatrolSettings settings() {
    return new PatrolSettings(
        slownessChance.get(),
        slownessTicks.get(),
        headChance.get(),
        chase.get(),
        weaponSwitch.get(),
        pause.get());
  }
}
