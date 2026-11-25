package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.ChunkKeys;
import io.github.zenndg.cubit.domain.network.ChunkRoster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import org.slf4j.Logger;

/**
 * The tickets of the chunks the Network holds, one chunk per block, where plants grow and machines
 * run (answers 227 and 2199). Only the Server and the antennas hold one (rule 22).
 */
public final class NetworkTickets {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String ID = "network";
  private static final String NO_LEVEL = "The network at {} is in a dimension that is not there.";
  private static final boolean TICKING = true;
  private static final TicketController CONTROLLER =
      new TicketController(
          ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, ID), NetworkTickets::validate);

  private NetworkTickets() {}

  public static void register(RegisterTicketControllersEvent event) {
    event.register(CONTROLLER);
  }

  /** Adding loads the chunk at once. False when its dimension is not there. */
  static boolean set(MinecraftServer server, GlobalPos place, boolean held) {
    ServerLevel level = server.getLevel(place.dimension());
    if (level == null) {
      LOGGER.warn(NO_LEVEL, place);
      return false;
    }
    BlockPos pos = place.pos();
    CONTROLLER.forceChunk(
        level,
        pos,
        SectionPos.blockToSectionCoord(pos.getX()),
        SectionPos.blockToSectionCoord(pos.getZ()),
        held,
        TICKING);
    return true;
  }

  /**
   * As the world opens no owner is there yet, so every chunk waits for its own, and one the data
   * does not know is released (answer 2210 and Delivery 3, detail 14).
   */
  private static void validate(ServerLevel level, TicketHelper tickets) {
    NetworkData data = NetworkData.of(level.getServer());
    ChunkKeys keys = CubitMod.CONFIG.network().chunks();
    ChunkRoster<GlobalPos> chunks = data.chunks();
    for (BlockPos pos : tickets.getBlockTickets().keySet()) {
      GlobalPos place = GlobalPos.of(level.dimension(), pos);
      boolean stays = keys.enabled() && !keys.releasesWithOwnerOffline() && chunks.holds(place);
      if (!stays) {
        tickets.removeAllTickets(pos);
        chunks.await(place);
        data.setDirty();
      }
    }
  }
}
