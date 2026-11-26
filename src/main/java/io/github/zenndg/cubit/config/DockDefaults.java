package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.dock.DockSpec;
import io.github.zenndg.cubit.domain.dock.DockXpSpec;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.tier.RobotTier;

/**
 * The default numbers of the Induction Dock at each tier, from _docs/tiers.md (answers 1374, 1385,
 * 1833, 1835 and 1944).
 */
final class DockDefaults {
  private static final long BASE_CAPACITY = 300_000;
  private static final long ADVANCED_CAPACITY = 1_200_000;
  private static final long ELITE_CAPACITY = 6_000_000;
  private static final long ULTIMATE_CAPACITY = 16_000_000;
  private static final long BASE_RECHARGE = 500;
  private static final long ADVANCED_RECHARGE = 1_000;
  private static final long ELITE_RECHARGE = 2_500;
  private static final long ULTIMATE_RECHARGE = 5_000;
  private static final int BASE_SLOTS = 27;
  private static final int ADVANCED_SLOTS = 36;
  private static final int ELITE_SLOTS = 45;
  private static final int ULTIMATE_SLOTS = 54;
  private static final long BASE_SOLAR = 25;
  private static final long ADVANCED_SOLAR = 100;
  private static final long ELITE_SOLAR = 500;
  private static final long ULTIMATE_SOLAR = 1_400;
  private static final long NO_NIGHT = 0;
  private static final long ULTIMATE_NIGHT = 350;
  private static final long BASE_STANDBY = 3;
  private static final DockXpSpec BASE_XP = new DockXpSpec(32_000, 50);
  private static final long ADVANCED_STANDBY = 8;
  private static final DockXpSpec ADVANCED_XP = new DockXpSpec(64_000, 100);
  private static final long ELITE_STANDBY = 25;
  private static final DockXpSpec ELITE_XP = new DockXpSpec(128_000, 250);
  private static final long ULTIMATE_STANDBY = 60;
  private static final DockXpSpec ULTIMATE_XP = new DockXpSpec(256_000, 500);
  private static final DockSpec BASE =
      spec(BASE_CAPACITY, BASE_RECHARGE, BASE_SLOTS, BASE_SOLAR, NO_NIGHT, BASE_XP, BASE_STANDBY);
  private static final DockSpec ADVANCED =
      spec(
          ADVANCED_CAPACITY,
          ADVANCED_RECHARGE,
          ADVANCED_SLOTS,
          ADVANCED_SOLAR,
          NO_NIGHT,
          ADVANCED_XP,
          ADVANCED_STANDBY);
  private static final DockSpec ELITE =
      spec(
          ELITE_CAPACITY,
          ELITE_RECHARGE,
          ELITE_SLOTS,
          ELITE_SOLAR,
          NO_NIGHT,
          ELITE_XP,
          ELITE_STANDBY);
  private static final DockSpec ULTIMATE =
      spec(
          ULTIMATE_CAPACITY,
          ULTIMATE_RECHARGE,
          ULTIMATE_SLOTS,
          ULTIMATE_SOLAR,
          ULTIMATE_NIGHT,
          ULTIMATE_XP,
          ULTIMATE_STANDBY);

  private DockDefaults() {}

  static DockSpec of(RobotTier tier) {
    return switch (tier) {
      case BASE -> BASE;
      case ADVANCED -> ADVANCED;
      case ELITE -> ELITE;
      case ULTIMATE -> ULTIMATE;
    };
  }

  private static DockSpec spec(
      long capacity, long recharge, int slots, long day, long night, DockXpSpec xp, long standby) {
    return new DockSpec(
        new Energy(capacity),
        new Energy(recharge),
        slots,
        new SolarOutput(new Energy(day), new Energy(night)),
        xp,
        new Energy(standby));
  }
}
