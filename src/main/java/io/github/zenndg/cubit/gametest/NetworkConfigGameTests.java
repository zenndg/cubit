package io.github.zenndg.cubit.gametest;

import com.electronwill.nightconfig.core.CommentedConfig;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.CubitConfig;
import java.util.Map;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The names and the defaults of the config keys of the Network (answer 2334). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkConfigGameTests {
  private static final String EMPTY_TEMPLATE = "empty";
  private static final String WRONG_KEY = "Wrong value for a config key";
  private static final String CHUNK_LOADING_KEY = "network.chunk_loading_enabled";
  private static final String OWNER_OFFLINE_KEY = "network.release_when_owner_offline";
  private static final String CHUNKS_PER_PLAYER_KEY = "network.chunks_per_player";
  private static final String CHUNKS_PER_WORLD_KEY = "network.chunks_per_world";
  private static final String CHUNK_COST_KEY = "network.chunk_cost";
  private static final String CHUNK_RESUME_ENERGY_KEY = "network.chunk_resume_energy";
  private static final String CHUNK_RESUME_SECONDS_KEY = "network.chunk_resume_seconds";
  private static final String BASE_STANDBY_KEY = "network.base.standby_per_tick";
  private static final String ULTIMATE_STANDBY_KEY = "network.ultimate.standby_per_tick";
  private static final String BASE_RELAY_STANDBY_KEY = "network.base.relay_standby_per_tick";
  private static final String ELITE_RELAY_STANDBY_KEY = "network.elite.relay_standby_per_tick";
  private static final String WAKE_INTERVAL_TICKS_KEY = "network.wake_interval_ticks";
  private static final String SERVER_UPS_PERCENT_KEY = "network.server_ups_percent";
  private static final String RELAY_ENERGY_CAPACITY_KEY = "network.relay_energy_capacity";
  private static final String RELAY_INPUT_PER_TICK_KEY = "network.relay_input_per_tick";
  private static final String STORAGE_SLOT_LIMIT_KEY = "network.storage_slot_limit";
  private static final String BASE_ENERGY_CAPACITY_KEY = "network.base.energy_capacity";
  private static final String BASE_CHANNELS_KEY = "network.base.channels";
  private static final String ADVANCED_INPUT_PER_TICK_KEY = "network.advanced.input_per_tick";
  private static final String ADVANCED_STORAGE_SLOTS_KEY = "network.advanced.storage_slots";
  private static final String ELITE_DAY_KEY = "network.elite.relay_solar_per_tick";
  private static final String ELITE_NIGHT_KEY = "network.elite.relay_solar_night_per_tick";
  private static final String ULTIMATE_ENERGY_CAPACITY_KEY = "network.ultimate.energy_capacity";
  private static final String ULTIMATE_CHANNELS_KEY = "network.ultimate.channels";
  private static final String ULTIMATE_STORAGE_SLOTS_KEY = "network.ultimate.storage_slots";
  private static final String ULTIMATE_NIGHT_KEY = "network.ultimate.relay_solar_night_per_tick";
  private static final Map<String, Object> GENERAL_DEFAULTS =
      Map.ofEntries(
          Map.entry(CHUNK_LOADING_KEY, true),
          Map.entry(OWNER_OFFLINE_KEY, true),
          Map.entry(CHUNKS_PER_PLAYER_KEY, 48),
          Map.entry(CHUNKS_PER_WORLD_KEY, 192),
          Map.entry(CHUNK_COST_KEY, 25),
          Map.entry(CHUNK_RESUME_ENERGY_KEY, 30_000),
          Map.entry(CHUNK_RESUME_SECONDS_KEY, 60),
          Map.entry(WAKE_INTERVAL_TICKS_KEY, 40),
          Map.entry(SERVER_UPS_PERCENT_KEY, 25),
          Map.entry(RELAY_ENERGY_CAPACITY_KEY, 300_000),
          Map.entry(RELAY_INPUT_PER_TICK_KEY, 5_000),
          Map.entry(STORAGE_SLOT_LIMIT_KEY, 128));
  private static final Map<String, Object> TIER_DEFAULTS =
      Map.ofEntries(
          Map.entry(BASE_ENERGY_CAPACITY_KEY, 1_200_000),
          Map.entry(BASE_CHANNELS_KEY, 4),
          Map.entry(ADVANCED_INPUT_PER_TICK_KEY, 40_000),
          Map.entry(ADVANCED_STORAGE_SLOTS_KEY, 36),
          Map.entry(ELITE_DAY_KEY, 2_000),
          Map.entry(ELITE_NIGHT_KEY, 0),
          Map.entry(ULTIMATE_ENERGY_CAPACITY_KEY, 64_000_000),
          Map.entry(ULTIMATE_CHANNELS_KEY, 16),
          Map.entry(ULTIMATE_STORAGE_SLOTS_KEY, 54),
          Map.entry(ULTIMATE_NIGHT_KEY, 1_000),
          Map.entry(BASE_STANDBY_KEY, 20),
          Map.entry(ULTIMATE_STANDBY_KEY, 160),
          Map.entry(BASE_RELAY_STANDBY_KEY, 2),
          Map.entry(ELITE_RELAY_STANDBY_KEY, 15));

  private NetworkConfigGameTests() {}

  @GameTest(template = EMPTY_TEMPLATE)
  public static void keepsTheGeneralKeysOfTheNetwork(GameTestHelper helper) {
    checkDefaults(helper, GENERAL_DEFAULTS);
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void keepsTheKeysOfEachTierOfTheServer(GameTestHelper helper) {
    checkDefaults(helper, TIER_DEFAULTS);
  }

  private static void checkDefaults(GameTestHelper helper, Map<String, Object> defaults) {
    CommentedConfig file = CommentedConfig.inMemory();
    new CubitConfig().spec().correct(file);
    defaults.forEach((key, value) -> helper.assertValueEqual(file.get(key), value, WRONG_KEY));
    helper.succeed();
  }
}
