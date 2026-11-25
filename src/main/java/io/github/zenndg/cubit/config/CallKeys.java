package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.behavior.CallSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

/** The keys of calling a Cubit, in the general section (answers 2464 and 2482). */
final class CallKeys {
  private static final String COOLDOWN = "call_cooldown_seconds";
  private static final int COOLDOWN_DEFAULT = 10;
  private static final String ARRIVAL = "call_arrival_blocks";
  private static final int ARRIVAL_DEFAULT = 4;
  private static final String TIMEOUT = "call_timeout_seconds";
  private static final int TIMEOUT_DEFAULT = 10;

  private final ModConfigSpec.IntValue cooldown;
  private final ModConfigSpec.IntValue arrival;
  private final ModConfigSpec.IntValue timeout;

  CallKeys(ModConfigSpec.Builder builder) {
    cooldown = builder.defineInRange(COOLDOWN, COOLDOWN_DEFAULT, 0, CallSpec.MAX_COOLDOWN_SECONDS);
    arrival = builder.defineInRange(ARRIVAL, ARRIVAL_DEFAULT, 1, CallSpec.MAX_ARRIVAL_BLOCKS);
    timeout = builder.defineInRange(TIMEOUT, TIMEOUT_DEFAULT, 1, CallSpec.MAX_TIMEOUT_SECONDS);
  }

  CallSpec spec() {
    return new CallSpec(cooldown.get(), arrival.get(), timeout.get());
  }
}
