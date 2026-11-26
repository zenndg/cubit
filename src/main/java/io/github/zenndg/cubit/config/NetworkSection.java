package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.BigSlot;
import io.github.zenndg.cubit.domain.network.ServerCatalog;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.robot.AreaDrawingSpec;
import io.github.zenndg.cubit.domain.robot.AreaShape;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.EnumMap;
import java.util.Map;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The config keys of the Network: the general ones, and the ones of the server at each tier, in a
 * section of the tier. The names are a contract (answer 2334).
 */
public final class NetworkSection implements ServerCatalog {
  private static final String SECTION = "network";
  private static final String MIN_POINTS = "area_min_points";
  private static final int MIN_POINTS_DEFAULT = 4;
  private static final int MIN_POINTS_MAX = 32;
  private static final String MAX_POINTS = "area_max_points";
  private static final int MAX_POINTS_DEFAULT = 32;
  private static final String REACH = "area_reach_blocks";
  private static final int REACH_DEFAULT = 32;
  private static final int REACH_MAX = 128;

  private static final String RELAY_CAPACITY = "relay_energy_capacity";
  private static final int RELAY_CAPACITY_DEFAULT = 300_000;
  private static final String RELAY_INPUT = "relay_input_per_tick";
  private static final int RELAY_INPUT_DEFAULT = 5_000;
  private static final String SLOT_LIMIT = "storage_slot_limit";
  private static final int SLOT_LIMIT_DEFAULT = 128;

  private static final int MIN = 1;

  private final ModConfigSpec.IntValue minPoints;
  private final ModConfigSpec.IntValue maxPoints;
  private final ModConfigSpec.IntValue reach;
  private final ModConfigSpec.IntValue relayCapacity;
  private final ModConfigSpec.IntValue relayInput;
  private final ModConfigSpec.IntValue slotLimit;
  private final ChunkKeys chunks;
  private final AntennaKeys antennas;
  private final Map<RobotTier, ServerKeys> servers = new EnumMap<>(RobotTier.class);

  /** In a section of its own, out of the general one. */
  static NetworkSection in(ModConfigSpec.Builder builder) {
    builder.push(SECTION);
    NetworkSection section = new NetworkSection(builder);
    builder.pop();
    return section;
  }

  private NetworkSection(ModConfigSpec.Builder builder) {
    minPoints =
        builder.defineInRange(
            MIN_POINTS, MIN_POINTS_DEFAULT, AreaDrawingSpec.FEWEST_POINTS, MIN_POINTS_MAX);
    maxPoints =
        builder.defineInRange(
            MAX_POINTS, MAX_POINTS_DEFAULT, AreaDrawingSpec.FEWEST_POINTS, AreaShape.MAX_POINTS);
    reach = builder.defineInRange(REACH, REACH_DEFAULT, MIN, REACH_MAX);
    chunks = new ChunkKeys(builder);
    antennas = new AntennaKeys(builder);
    relayCapacity =
        builder.defineInRange(RELAY_CAPACITY, RELAY_CAPACITY_DEFAULT, MIN, Integer.MAX_VALUE);
    relayInput = builder.defineInRange(RELAY_INPUT, RELAY_INPUT_DEFAULT, MIN, Integer.MAX_VALUE);
    slotLimit = builder.defineInRange(SLOT_LIMIT, SLOT_LIMIT_DEFAULT, MIN, BigSlot.MAX_LIMIT);
    for (RobotTier tier : RobotTier.values()) {
      builder.push(tier.key());
      servers.put(tier, new ServerKeys(builder, ServerDefaults.of(tier)));
      builder.pop();
    }
  }

  @Override
  public ServerSpec server(RobotTier tier) {
    return servers.get(tier).read();
  }

  /** The keys of the chunks the mod holds. */
  public ChunkKeys chunks() {
    return chunks;
  }

  /** The keys of the Signal Antenna and of the cost of its distance. */
  public AntennaKeys antennas() {
    return antennas;
  }

  public Energy relayCapacity() {
    return new Energy(relayCapacity.get());
  }

  /** What the Solar Relay takes per tick by cables and redstone (answer 2326). */
  public Energy relayInputPerTick() {
    return new Energy(relayInput.get());
  }

  /** The items of each slot of the server (answer 2263). */
  public BigSlot slot() {
    return new BigSlot(slotLimit.get());
  }

  /**
   * The numbers of the drawing of an area (answers 2287 and 2311). With the limits reversed, the
   * fewest points win, so the area can still be closed (rule 17).
   */
  AreaDrawingSpec drawing() {
    return new AreaDrawingSpec(
        minPoints.get(), Math.max(minPoints.get(), maxPoints.get()), reach.get());
  }
}
