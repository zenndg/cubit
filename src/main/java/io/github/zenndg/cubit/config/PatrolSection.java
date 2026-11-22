package io.github.zenndg.cubit.config;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import java.util.concurrent.atomic.AtomicBoolean;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

/**
 * The numbers of the Galactic Patroller that hold for every tier, beside the defense. The names are
 * a contract with the pack (answers 1557, 1559, 1562, 1577, 1614 to 1618, 1628 and 1632). A maximum
 * below its minimum counts as the minimum and goes to the log once (rule 17).
 */
public final class PatrolSection {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String REVERSED = "Patroller stop limits are reversed; using the minimum";
  private static final String SLOWNESS_CHANCE = "patroller_slowness_chance";
  private static final String SLOWNESS_TICKS = "patroller_slowness_ticks";
  private static final String HEAD_CHANCE = "patroller_head_chance";
  private static final String CHASE = "patroller_chase_distance";
  private static final String SWITCH = "patroller_weapon_switch_distance";
  private static final String STOP_MIN_TICKS = "patroller_stop_min_ticks";
  private static final String STOP_MAX_TICKS = "patroller_stop_max_ticks";
  private static final String STOP_MIN_DISTANCE = "patroller_stop_min_distance";
  private static final String STOP_MAX_DISTANCE = "patroller_stop_max_distance";
  private static final String SPEED = "patroller_patrol_speed";
  private static final int SLOWNESS_CHANCE_DEFAULT = 20;
  private static final int SLOWNESS_TICKS_DEFAULT = 60;
  private static final int HEAD_CHANCE_DEFAULT = 50;
  private static final int CHASE_DEFAULT = 16;
  private static final int SWITCH_DEFAULT = 4;
  private static final int STOP_MIN_TICKS_DEFAULT = 60;
  private static final int STOP_MAX_TICKS_DEFAULT = 100;
  private static final int STOP_MIN_DISTANCE_DEFAULT = 12;
  private static final int STOP_MAX_DISTANCE_DEFAULT = 20;
  private static final double SPEED_DEFAULT = 0.6;
  private static final double SPEED_MIN = 0.1;
  private static final double SPEED_MAX = 1;
  private static final int STOP_DISTANCE_MAX = 256;
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
  private final ModConfigSpec.IntValue stopMinTicks;
  private final ModConfigSpec.IntValue stopMaxTicks;
  private final ModConfigSpec.IntValue stopMinDistance;
  private final ModConfigSpec.IntValue stopMaxDistance;
  private final ModConfigSpec.DoubleValue speed;
  private final AtomicBoolean warned = new AtomicBoolean();

  PatrolSection(ModConfigSpec.Builder builder) {
    slownessChance =
        builder.defineInRange(SLOWNESS_CHANCE, SLOWNESS_CHANCE_DEFAULT, MIN_ZERO, PERCENT_MAX);
    slownessTicks =
        builder.defineInRange(SLOWNESS_TICKS, SLOWNESS_TICKS_DEFAULT, MIN_ONE, TICKS_MAX);
    headChance = builder.defineInRange(HEAD_CHANCE, HEAD_CHANCE_DEFAULT, MIN_ZERO, PERCENT_MAX);
    chase = builder.defineInRange(CHASE, CHASE_DEFAULT, MIN_ZERO, BLOCKS_MAX);
    weaponSwitch = builder.defineInRange(SWITCH, SWITCH_DEFAULT, MIN_ZERO, BLOCKS_MAX);
    stopMinTicks =
        builder.defineInRange(STOP_MIN_TICKS, STOP_MIN_TICKS_DEFAULT, MIN_ZERO, TICKS_MAX);
    stopMaxTicks =
        builder.defineInRange(STOP_MAX_TICKS, STOP_MAX_TICKS_DEFAULT, MIN_ZERO, TICKS_MAX);
    stopMinDistance =
        builder.defineInRange(
            STOP_MIN_DISTANCE, STOP_MIN_DISTANCE_DEFAULT, MIN_ONE, STOP_DISTANCE_MAX);
    stopMaxDistance =
        builder.defineInRange(
            STOP_MAX_DISTANCE, STOP_MAX_DISTANCE_DEFAULT, MIN_ONE, STOP_DISTANCE_MAX);
    speed = builder.defineInRange(SPEED, SPEED_DEFAULT, SPEED_MIN, SPEED_MAX);
  }

  public PatrolSettings settings() {
    return new PatrolSettings(
        slownessChance.get(),
        slownessTicks.get(),
        headChance.get(),
        chase.get(),
        weaponSwitch.get(),
        round());
  }

  private PatrolSettings.Round round() {
    int minTicks = stopMinTicks.get();
    int maxTicks = stopMaxTicks.get();
    int minDistance = stopMinDistance.get();
    int maxDistance = stopMaxDistance.get();
    if ((maxTicks < minTicks || maxDistance < minDistance) && warned.compareAndSet(false, true)) {
      LOGGER.warn(REVERSED);
    }
    return new PatrolSettings.Round(
        speed.get(),
        minTicks,
        Math.max(minTicks, maxTicks),
        minDistance,
        Math.max(minDistance, maxDistance));
  }
}
