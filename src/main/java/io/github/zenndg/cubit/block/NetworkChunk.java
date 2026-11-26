package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.ownership.Ownership;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The blocks of the Network in a chunk, read from its block entities, never by looking at each
 * block and never loading it (rules 21 and 22). With two of a kind, put by a command, only the one
 * at the lowest position counts (Delivery 3, details 1 and 2).
 */
final class NetworkChunk {
  private NetworkChunk() {}

  /** Whether the chunk already has that block, of any owner (answer 2271). */
  static boolean has(Level level, BlockPos pos, Block block) {
    return at(level, pos)
        .filter(chunk ->
            chunk.getBlockEntities().values().stream()
                .anyMatch(entity -> entity.getBlockState().is(block)))
        .isPresent();
  }

  /**
   * Pairs the server and the relay of the chunk, of the same owner, after one is placed, broken or
   * loaded (answer 2268).
   */
  static void pair(Level level, BlockPos pos) {
    at(level, pos).ifPresent(NetworkChunk::pair);
  }

  private static void pair(LevelChunk chunk) {
    List<NetworkServerBlockEntity> servers = all(chunk, NetworkServerBlockEntity.class);
    List<SolarRelayBlockEntity> relays = all(chunk, SolarRelayBlockEntity.class);
    Optional<NetworkServerBlockEntity> server = servers.stream().findFirst();
    Optional<SolarRelayBlockEntity> relay = relays.stream().findFirst();
    boolean paired =
        server.isPresent()
            && relay.isPresent()
            && Ownership.sameOwner(server.get().owner(), relay.get().owner());
    for (NetworkServerBlockEntity each : servers) {
      boolean first = each == server.orElseThrow();
      each.pairWith(paired && first ? relay : Optional.empty(), first);
    }
    for (SolarRelayBlockEntity each : relays) {
      each.pairWith(paired && each == relay.orElseThrow() ? server : Optional.empty());
    }
  }

  private static <T extends BlockEntity> List<T> all(LevelChunk chunk, Class<T> type) {
    return chunk.getBlockEntities().values().stream()
        .filter(entity -> type.isInstance(entity) && !entity.isRemoved())
        .map(type::cast)
        .sorted(Comparator.comparing(BlockEntity::getBlockPos))
        .toList();
  }

  private static Optional<LevelChunk> at(Level level, BlockPos pos) {
    int x = SectionPos.blockToSectionCoord(pos.getX());
    int z = SectionPos.blockToSectionCoord(pos.getZ());
    return Optional.ofNullable(level.getChunkSource().getChunkNow(x, z));
  }
}
