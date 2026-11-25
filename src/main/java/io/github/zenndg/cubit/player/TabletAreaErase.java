package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.robot.AreaErase;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.menu.TabletKeys;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Shift and two clicks on an own Cubit, with no drawing, erase its area (answers 2299 and 2352).
 * The first click lives only in the memory of the server.
 */
final class TabletAreaErase {
  private static final Map<UUID, AreaErase> ASKED = new ConcurrentHashMap<>();

  private TabletAreaErase() {}

  static void click(ServerPlayer player, Entity entity) {
    if (!(entity instanceof RobotEntity robot)
        || !Ownership.owns(robot.ownerId(), player.getUUID())) {
      say(player, TabletKeys.AREA_ONLY_YOURS);
      return;
    }
    if (robot.workArea().area().isEmpty()) {
      say(player, TabletKeys.AREA_NOTHING_TO_ERASE, robot.getDisplayName());
      return;
    }
    long now = player.serverLevel().getGameTime();
    AreaErase asked = ASKED.remove(player.getUUID());
    if (asked != null && asked.confirmedBy(robot.getUUID(), now)) {
      robot.workArea().erase();
      say(player, TabletKeys.AREA_ERASED, robot.getDisplayName());
    } else {
      ASKED.put(player.getUUID(), new AreaErase(robot.getUUID(), now));
      say(player, TabletKeys.AREA_ERASE_CONFIRM, robot.getDisplayName());
    }
  }

  static void forget(ServerPlayer player) {
    ASKED.remove(player.getUUID());
  }

  private static void say(ServerPlayer player, String key, Object... values) {
    TabletAreaApp.say(player, Component.translatable(key, values));
  }
}
