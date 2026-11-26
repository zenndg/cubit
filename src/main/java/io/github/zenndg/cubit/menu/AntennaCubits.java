package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.LinkSpots;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.network.LinkSpot;
import io.github.zenndg.cubit.registry.CubitEntities;
import net.minecraft.server.level.ServerLevel;

/**
 * The loaded Cubits of the owner linked to an antenna, for the line Links of its screen (answer
 * 2593). Counted only while the screen is open, once a second, so no tick of the world pays for it
 * (rule 21).
 */
final class AntennaCubits {
  private static final int COUNT_TICKS = 20;

  private long countedAt = Long.MIN_VALUE;
  private int count;

  int count(SignalAntennaBlockEntity antenna) {
    if (!(antenna.getLevel() instanceof ServerLevel level)) {
      return count;
    }
    long now = level.getGameTime();
    if (countedAt != Long.MIN_VALUE && now - countedAt < COUNT_TICKS) {
      return count;
    }
    countedAt = now;
    LinkSpot spot = LinkSpots.of(level, antenna.getBlockPos());
    count = 0;
    for (ServerLevel each : level.getServer().getAllLevels()) {
      count +=
          each.getEntities(
                  CubitEntities.GENERAL_ROBOT.get(),
                  robot ->
                      robot.ownerId().equals(antenna.owner())
                          && robot.docking().network().links().antennas().contains(spot))
              .size();
    }
    return count;
  }
}
