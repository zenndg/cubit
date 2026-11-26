package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.network.LinkLimits;
import io.github.zenndg.cubit.domain.tier.RobotTier;

/**
 * The default links of a Cubit at each tier, from _docs/tiers.md (answers 2226, 2232, 2233 and
 * 2235). The Organizer starts at Advanced, so a Base one links no chest of its own.
 */
final class LinkDefaults {
  private static final LinkLimits BASE = new LinkLimits(0, 1, 1, 0);
  private static final LinkLimits ADVANCED = new LinkLimits(1, 2, 2, 12);
  private static final LinkLimits ELITE = new LinkLimits(4, 4, 4, 18);
  private static final LinkLimits ULTIMATE = new LinkLimits(9, 6, 6, 25);

  private LinkDefaults() {}

  static LinkLimits of(RobotTier tier) {
    return switch (tier) {
      case BASE -> BASE;
      case ADVANCED -> ADVANCED;
      case ELITE -> ELITE;
      case ULTIMATE -> ULTIMATE;
    };
  }
}
