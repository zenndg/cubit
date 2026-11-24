package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.tier.XpSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

/** The XP tank of the Cubit at one tier. The key name is a contract with the pack (1826). */
final class XpKey {
  private static final String CAPACITY = "xp_tank_capacity";
  private static final int MIN = 1;

  private final ModConfigSpec.IntValue capacity;

  XpKey(ModConfigSpec.Builder builder, XpSpec defaults) {
    capacity = builder.defineInRange(CAPACITY, defaults.tankCapacity(), MIN, Integer.MAX_VALUE);
  }

  XpSpec read() {
    return new XpSpec(capacity.get());
  }
}
