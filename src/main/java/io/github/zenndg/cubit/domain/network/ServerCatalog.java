package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.tier.RobotTier;

/**
 * Gives the numbers of the Network Server at a tier. The domain defines it, the config serves it.
 */
public interface ServerCatalog {
  ServerSpec server(RobotTier tier);
}
