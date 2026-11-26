package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.tier.RobotTier;

/** Gives what a Cubit links at a tier. The domain defines it, the config serves it. */
public interface LinkCatalog {
  LinkLimits links(RobotTier tier);
}
