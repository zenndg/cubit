package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.menu.TabletKeys;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * The Escort app: a click on a player or a mob the chosen Galactic Patroller never attacks marks
 * the one it escorts, a click on it again or Shift and a click unmark it, and a mob it attacks is
 * refused with a notice (answers 1655, 1656 and 1743). The click is checked on the server, for the
 * owner of the chosen Cubit only (rule 20).
 */
final class TabletEscortApp {
  private TabletEscortApp() {}

  static void click(ServerPlayer player, RobotEntity chosen, TabletUse.Click click) {
    if (player.isShiftKeyDown()) {
      stop(player, chosen);
      return;
    }
    Optional<LivingEntity> picked =
        click instanceof TabletUse.Living living && living.entity() instanceof LivingEntity hit
            ? Optional.of(hit)
            : TabletTargetApp.aimed(player);
    if (picked.isEmpty()) {
      player.displayClientMessage(Component.translatable(TabletKeys.NO_MOB), true);
      return;
    }
    LivingEntity one = picked.get();
    if (chosen.workArea().work().escort().escorts(one.getUUID())) {
      stop(player, chosen);
    } else if (!chosen.workArea().work().escort().mark(one)) {
      player.displayClientMessage(
          Component.translatable(TabletKeys.NOT_ESCORTABLE, chosen.getName()), true);
    } else {
      player.displayClientMessage(
          Component.translatable(TabletKeys.ESCORTING, chosen.getName(), one.getName()), true);
    }
  }

  private static void stop(ServerPlayer player, RobotEntity chosen) {
    if (chosen.workArea().work().escort().active()) {
      chosen.workArea().work().escort().stop();
      player.displayClientMessage(
          Component.translatable(TabletKeys.ESCORT_STOPPED, chosen.getName()), true);
    }
  }
}
