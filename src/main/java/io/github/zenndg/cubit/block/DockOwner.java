package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * The owner of a dock, who alone uses it, kept in the save and sent to the client, which decides
 * who can break the block.
 */
final class DockOwner {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String NO_OWNER_MESSAGE = "The dock at {} has no owner.";
  private static final String KEY = "Owner";

  private UUID owner;

  Optional<UUID> get() {
    return Optional.ofNullable(owner);
  }

  void set(UUID player) {
    owner = player;
  }

  /**
   * The name of the owner, online or from the profile cache of the server. The cache may not know
   * it, and then the screen shows a safe text (AGENTS.md, rule 17).
   */
  Optional<String> name(Level level, BlockPos pos) {
    if (owner == null || level == null || level.getServer() == null) {
      return Optional.empty();
    }
    return OwnerName.of(level.getServer(), owner, pos);
  }

  void save(CompoundTag tag) {
    if (owner != null) {
      tag.putUUID(KEY, owner);
    }
  }

  void load(CompoundTag tag, Level level, BlockPos pos) {
    owner = tag.hasUUID(KEY) ? tag.getUUID(KEY) : null;
    if (owner == null && (level == null || !level.isClientSide())) {
      LOGGER.warn(NO_OWNER_MESSAGE, pos);
    }
  }
}
