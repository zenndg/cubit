package io.github.zenndg.cubit.config;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import io.github.zenndg.cubit.domain.behavior.PatrolTeam;
import io.github.zenndg.cubit.domain.behavior.PatrolWatch;
import java.util.concurrent.atomic.AtomicBoolean;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

/**
 * The numbers of the Galactic Patroller that hold for every tier, beside the defense. The names are
 * a contract with the pack (answers 1557, 1559, 1562, 1577, 1614 to 1618, 1628, 1632, 1644 and
 * 1691). A maximum below its minimum counts as the minimum and goes to the log once (rule 17).
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
  private static final String CALL_RANGE = "patroller_call_range";
  private static final String RESCUE_RADIUS = "patroller_rescue_radius";
  private static final String LOW_HEALTH = "patroller_low_health_percent";
  private static final String ALERT = "patroller_alert_ticks";
  private static final String OWNER_CHECK = "patroller_owner_check_ticks";
  private static final String OWNER_DISTANCE = "patroller_owner_check_distance";
  private static final String VISOR_CALM = "patroller_visor_calm_ticks";
  private static final String LOST_SIGHT = "patroller_lost_sight_ticks";
  private static final int LOST_SIGHT_DEFAULT = 40;
  private static final int ALERT_DEFAULT = 10;
  private static final int OWNER_CHECK_DEFAULT = 20;
  private static final int OWNER_DISTANCE_DEFAULT = 16;
  private static final int VISOR_CALM_DEFAULT = 60;
  private static final int SHORT_TICKS_MAX = 1200;
  private static final int CALL_RANGE_DEFAULT = 32;
  private static final int CALL_RANGE_MAX = 128;
  private static final int RESCUE_RADIUS_DEFAULT = 8;
  private static final int RESCUE_RADIUS_MAX = 32;
  private static final int LOW_HEALTH_DEFAULT = 25;
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
  private final ModConfigSpec.IntValue callRange;
  private final ModConfigSpec.IntValue rescueRadius;
  private final ModConfigSpec.IntValue lowHealth;
  private final ModConfigSpec.IntValue alert;
  private final ModConfigSpec.IntValue ownerCheck;
  private final ModConfigSpec.IntValue ownerDistance;
  private final ModConfigSpec.IntValue visorCalm;
  private final ModConfigSpec.IntValue lostSight;
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
    callRange = builder.defineInRange(CALL_RANGE, CALL_RANGE_DEFAULT, MIN_ONE, CALL_RANGE_MAX);
    rescueRadius =
        builder.defineInRange(RESCUE_RADIUS, RESCUE_RADIUS_DEFAULT, MIN_ONE, RESCUE_RADIUS_MAX);
    lowHealth = builder.defineInRange(LOW_HEALTH, LOW_HEALTH_DEFAULT, MIN_ZERO, PERCENT_MAX);
    alert = builder.defineInRange(ALERT, ALERT_DEFAULT, MIN_ZERO, SHORT_TICKS_MAX);
    ownerCheck = builder.defineInRange(OWNER_CHECK, OWNER_CHECK_DEFAULT, MIN_ZERO, SHORT_TICKS_MAX);
    ownerDistance =
        builder.defineInRange(OWNER_DISTANCE, OWNER_DISTANCE_DEFAULT, MIN_ONE, BLOCKS_MAX);
    visorCalm = builder.defineInRange(VISOR_CALM, VISOR_CALM_DEFAULT, MIN_ZERO, SHORT_TICKS_MAX);
    lostSight = builder.defineInRange(LOST_SIGHT, LOST_SIGHT_DEFAULT, MIN_ZERO, SHORT_TICKS_MAX);
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

  /** The team of the Patrollers of one owner (answers 1644, 1686 and 1691). */
  public PatrolTeam team() {
    return new PatrolTeam(callRange.get(), rescueRadius.get(), lowHealth.get());
  }

  /**
   * The alert, the check on the owner, the calm of the visor and the lost sight (1623 to 1632,
   * 1721).
   */
  public PatrolWatch.Timings watch() {
    return new PatrolWatch.Timings(
        alert.get(), ownerCheck.get(), ownerDistance.get(), visorCalm.get(), lostSight.get());
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
