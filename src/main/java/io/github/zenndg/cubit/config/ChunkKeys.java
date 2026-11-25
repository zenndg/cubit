package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.ChunkCall;
import io.github.zenndg.cubit.domain.network.ChunkLimits;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The keys of the chunks the mod holds (answers 227, 2210, 2215, 2216, 2221, 2330, 2372 and 2438).
 */
public final class ChunkKeys {
  private static final String ENABLED = "chunk_loading_enabled";
  private static final String OWNER_OFFLINE = "release_when_owner_offline";
  private static final String PER_PLAYER = "chunks_per_player";
  private static final int PER_PLAYER_DEFAULT = 48;
  private static final String PER_WORLD = "chunks_per_world";
  private static final int PER_WORLD_DEFAULT = 192;
  private static final String COST = "chunk_cost";
  private static final int COST_DEFAULT = 25;
  private static final String RESUME = "chunk_resume_energy";
  private static final int RESUME_DEFAULT = 30_000;
  private static final String RESUME_SECONDS = "chunk_resume_seconds";
  private static final int RESUME_SECONDS_DEFAULT = 60;
  private static final int RESUME_SECONDS_MAX = 3_600;
  private static final String WAKE_INTERVAL = "wake_interval_ticks";
  private static final int WAKE_INTERVAL_DEFAULT = 40;
  private static final int WAKE_INTERVAL_MAX = 1_200;
  private static final String SERVER_UPS = "server_ups_percent";
  private static final int SERVER_UPS_DEFAULT = 25;
  private static final int NONE = 0;
  private static final int ONE_TICK = 1;

  private final ModConfigSpec.BooleanValue enabled;
  private final ModConfigSpec.BooleanValue ownerOffline;
  private final ModConfigSpec.IntValue perPlayer;
  private final ModConfigSpec.IntValue perWorld;
  private final ModConfigSpec.IntValue cost;
  private final ModConfigSpec.IntValue resume;
  private final ModConfigSpec.IntValue resumeSeconds;
  private final ModConfigSpec.IntValue wakeInterval;
  private final ModConfigSpec.IntValue serverUps;

  ChunkKeys(ModConfigSpec.Builder builder) {
    enabled = builder.define(ENABLED, true);
    ownerOffline = builder.define(OWNER_OFFLINE, true);
    perPlayer = builder.defineInRange(PER_PLAYER, PER_PLAYER_DEFAULT, NONE, Integer.MAX_VALUE);
    perWorld = builder.defineInRange(PER_WORLD, PER_WORLD_DEFAULT, NONE, Integer.MAX_VALUE);
    cost = builder.defineInRange(COST, COST_DEFAULT, NONE, Integer.MAX_VALUE);
    resume = builder.defineInRange(RESUME, RESUME_DEFAULT, NONE, Integer.MAX_VALUE);
    resumeSeconds =
        builder.defineInRange(RESUME_SECONDS, RESUME_SECONDS_DEFAULT, NONE, RESUME_SECONDS_MAX);
    wakeInterval =
        builder.defineInRange(WAKE_INTERVAL, WAKE_INTERVAL_DEFAULT, ONE_TICK, WAKE_INTERVAL_MAX);
    serverUps = builder.defineInRange(SERVER_UPS, SERVER_UPS_DEFAULT, NONE, UpsReserve.MAX);
  }

  public boolean enabled() {
    return enabled.get();
  }

  public boolean releasesWithOwnerOffline() {
    return ownerOffline.get();
  }

  public ChunkLimits limits() {
    return new ChunkLimits(perPlayer.get(), perWorld.get());
  }

  /** What the chunk costs, and the charge that takes it back with what the block spends (2438). */
  public ChunkCall.Price price(Energy standby) {
    return ChunkCall.Price.of(perTick(), standby, new Energy(resume.get()), resumeSeconds.get());
  }

  public Energy perTick() {
    return new Energy(cost.get());
  }

  /** Between one chunk and the next, when their owner comes back (answers 2330 and 2413). */
  public int wakeIntervalTicks() {
    return wakeInterval.get();
  }

  public UpsReserve serverUps() {
    return UpsReserve.of(serverUps.get());
  }
}
