package io.github.zenndg.cubit.config;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.network.Standby;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

/**
 * The Network Server at one tier, with the generation of its Solar Relay. The key names are a
 * contract (answer 2334).
 */
final class ServerKeys {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String CAPACITY = "energy_capacity";
  private static final String CHANNELS = "channels";
  private static final String INPUT = "input_per_tick";
  private static final String SLOTS = "storage_slots";
  private static final String SOLAR = "relay_solar_per_tick";
  private static final String SOLAR_NIGHT = "relay_solar_night_per_tick";
  private static final String STANDBY = "standby_per_tick";
  private static final String RELAY_STANDBY = "relay_standby_per_tick";
  private static final int MIN_ZERO = 0;
  private static final int MIN_POSITIVE = 1;
  private static final int MAX_ENERGY = Integer.MAX_VALUE;
  private static final String NOT_A_ROW = "storage_slots {} is not whole rows of 9, using {}.";

  private final ModConfigSpec.IntValue capacity;
  private final ModConfigSpec.IntValue channels;
  private final ModConfigSpec.IntValue input;
  private final ModConfigSpec.IntValue slots;
  private final ModConfigSpec.IntValue solar;
  private final ModConfigSpec.IntValue solarNight;
  private final ModConfigSpec.IntValue standby;
  private final ModConfigSpec.IntValue relayStandby;

  ServerKeys(ModConfigSpec.Builder builder, ServerSpec defaults) {
    capacity = energy(builder, CAPACITY, defaults.capacity(), MIN_POSITIVE);
    channels =
        builder.defineInRange(CHANNELS, defaults.channels(), MIN_POSITIVE, ServerSpec.MAX_CHANNELS);
    input = energy(builder, INPUT, defaults.inputPerTick(), MIN_POSITIVE);
    slots =
        builder.defineInRange(SLOTS, defaults.storageSlots(), ServerSpec.ROW, ServerSpec.MAX_SLOTS);
    solar = energy(builder, SOLAR, defaults.relay().dayPerTick(), MIN_ZERO);
    solarNight = energy(builder, SOLAR_NIGHT, defaults.relay().nightPerTick(), MIN_ZERO);
    standby = energy(builder, STANDBY, defaults.standby().server(), MIN_ZERO);
    relayStandby = energy(builder, RELAY_STANDBY, defaults.standby().relay(), MIN_ZERO);
  }

  /** Slots that are not whole rows fall to the row below, with a warning (rule 17). */
  ServerSpec read() {
    int configured = slots.get();
    int rows = configured - configured % ServerSpec.ROW;
    if (rows != configured) {
      LOGGER.warn(NOT_A_ROW, configured, rows);
    }
    return new ServerSpec(
        new Energy(capacity.get()),
        channels.get(),
        new Energy(input.get()),
        rows,
        new SolarOutput(new Energy(solar.get()), new Energy(solarNight.get())),
        new Standby(new Energy(standby.get()), new Energy(relayStandby.get())));
  }

  private static ModConfigSpec.IntValue energy(
      ModConfigSpec.Builder builder, String key, Energy defaultValue, int min) {
    return builder.defineInRange(key, (int) defaultValue.amount(), min, MAX_ENERGY);
  }
}
