package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.network.Standby;
import io.github.zenndg.cubit.domain.tier.RobotTier;

/**
 * The default numbers of the Network Server at each tier, from _docs/tiers.md (answers 2220 to
 * 2223, 2267, 2269, 2326 and 2435).
 */
final class ServerDefaults {
  private static final long NO_NIGHT = 0;
  private static final long BASE_CAPACITY = 1_200_000;
  private static final long ADVANCED_CAPACITY = 4_800_000;
  private static final long ELITE_CAPACITY = 24_000_000;
  private static final long ULTIMATE_CAPACITY = 64_000_000;
  private static final int BASE_CHANNELS = 4;
  private static final int ADVANCED_CHANNELS = 8;
  private static final int ELITE_CHANNELS = 12;
  private static final long BASE_INPUT = 20_000;
  private static final long ADVANCED_INPUT = 40_000;
  private static final long ELITE_INPUT = 80_000;
  private static final long ULTIMATE_INPUT = 160_000;
  private static final int BASE_SLOTS = 27;
  private static final int ADVANCED_SLOTS = 36;
  private static final int ELITE_SLOTS = 45;
  private static final SolarOutput BASE_RELAY = relay(500, NO_NIGHT);
  private static final SolarOutput ADVANCED_RELAY = relay(1_000, NO_NIGHT);
  private static final SolarOutput ELITE_RELAY = relay(2_000, NO_NIGHT);
  private static final SolarOutput ULTIMATE_RELAY = relay(4_000, 1_000);

  private static final Standby BASE_STANDBY = standby(20, 2);
  private static final Standby ADVANCED_STANDBY = standby(40, 5);
  private static final Standby ELITE_STANDBY = standby(80, 15);
  private static final Standby ULTIMATE_STANDBY = standby(160, 40);

  private ServerDefaults() {}

  static ServerSpec of(RobotTier tier) {
    return switch (tier) {
      case BASE ->
          new ServerSpec(
              new Energy(BASE_CAPACITY),
              BASE_CHANNELS,
              new Energy(BASE_INPUT),
              BASE_SLOTS,
              BASE_RELAY,
              BASE_STANDBY);
      case ADVANCED ->
          new ServerSpec(
              new Energy(ADVANCED_CAPACITY),
              ADVANCED_CHANNELS,
              new Energy(ADVANCED_INPUT),
              ADVANCED_SLOTS,
              ADVANCED_RELAY,
              ADVANCED_STANDBY);
      case ELITE ->
          new ServerSpec(
              new Energy(ELITE_CAPACITY),
              ELITE_CHANNELS,
              new Energy(ELITE_INPUT),
              ELITE_SLOTS,
              ELITE_RELAY,
              ELITE_STANDBY);
      case ULTIMATE -> ultimate();
    };
  }

  private static ServerSpec ultimate() {
    return new ServerSpec(
        new Energy(ULTIMATE_CAPACITY),
        ServerSpec.MAX_CHANNELS,
        new Energy(ULTIMATE_INPUT),
        ServerSpec.MAX_SLOTS,
        ULTIMATE_RELAY,
        ULTIMATE_STANDBY);
  }

  private static Standby standby(long server, long relay) {
    return new Standby(new Energy(server), new Energy(relay));
  }

  private static SolarOutput relay(long day, long night) {
    return new SolarOutput(new Energy(day), new Energy(night));
  }
}
