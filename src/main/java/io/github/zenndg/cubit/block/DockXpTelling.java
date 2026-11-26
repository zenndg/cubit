package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** The notice of a full XP tank, on the action bar of the owner near the dock (1840, 2103). */
public final class DockXpTelling {
  public static final String FULL_KEY = "notice.cubit.dock_xp_tank_full";

  private DockXpTelling() {}

  /** False when the owner is not there to read it, so it waits for them. */
  public static boolean tell(Level level, BlockPos dock, Optional<UUID> owner, BlockName name) {
    int blocks = CubitMod.CONFIG.xp().dockNoticeBlocks();
    Player player = owner.map(level::getPlayerByUUID).orElse(null);
    if (player == null || !player.position().closerThan(Vec3.atCenterOf(dock), blocks)) {
      return false;
    }
    player.displayClientMessage(Component.translatable(FULL_KEY, name.value()), true);
    return true;
  }
}
