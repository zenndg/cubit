package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.network.LinkLimits;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * What a Cubit of one tier links in the Network app (answers 2226, 2232, 2233, 2235 and 2602). The
 * key names are a contract (answer 2334).
 */
final class LinkKeys {
  private static final String ANTENNAS = "network_antennas";
  private static final String CHESTS = "network_chests";
  private static final String DOCKS = "network_docks";
  private static final String ORGANIZER_CHESTS = "network_organizer_chests";
  private static final int MIN = 0;
  private static final int MAX_ANTENNAS = 64;
  private static final int MAX_MARKS = 64;

  private final ModConfigSpec.IntValue antennas;
  private final ModConfigSpec.IntValue chests;
  private final ModConfigSpec.IntValue docks;
  private final ModConfigSpec.IntValue organizerChests;

  LinkKeys(ModConfigSpec.Builder builder, LinkLimits defaults) {
    antennas = builder.defineInRange(ANTENNAS, defaults.antennas(), MIN, MAX_ANTENNAS);
    chests = builder.defineInRange(CHESTS, defaults.chests(), MIN, MAX_MARKS);
    docks = builder.defineInRange(DOCKS, defaults.docks(), MIN, MAX_MARKS);
    organizerChests =
        builder.defineInRange(ORGANIZER_CHESTS, defaults.organizerChests(), MIN, MAX_MARKS);
  }

  LinkLimits read() {
    return new LinkLimits(antennas.get(), docks.get(), chests.get(), organizerChests.get());
  }
}
