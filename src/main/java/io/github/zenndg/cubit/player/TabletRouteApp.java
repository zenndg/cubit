package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.domain.robot.RouteMarking;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotPatrol;
import io.github.zenndg.cubit.menu.TabletKeys;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * The Mark app for the chosen Galactic Patroller: a click marks the top of a solid block inside its
 * area, or unmarks a marked point, and Shift and a click take out the clicked point, or else the
 * last one, each with its notice on the action bar (answers 1579, 1599 and 1611).
 */
final class TabletRouteApp {
  private TabletRouteApp() {}

  static void click(ServerPlayer player, RobotEntity robot, TabletUse.Click click) {
    RobotPatrol patrol = robot.workArea().work().patrol();
    Optional<BlockPos> pos =
        click instanceof TabletUse.Block block ? Optional.of(block.pos()) : Optional.empty();
    if (player.isShiftKeyDown()) {
      String key = patrol.unmark(player, pos) ? TabletKeys.MARK_UNMARKED : TabletKeys.MARK_NONE;
      say(player, Component.translatable(key, robot.getDisplayName()));
    } else if (pos.isEmpty()) {
      say(player, Component.translatable(TabletKeys.ROUTE_NOT_SOLID));
    } else {
      patrol
          .click(player, pos.get())
          .ifPresent(result -> say(player, notice(result, robot, patrol)));
    }
    TabletChestView.send(player);
  }

  private static Component notice(
      RouteMarking.Result result, RobotEntity robot, RobotPatrol patrol) {
    Component name = robot.getDisplayName();
    return switch (result) {
      case ADDED ->
          Component.translatable(
              TabletKeys.ROUTE_POINT, patrol.route().size(), patrol.limit(), name);
      case REMOVED -> Component.translatable(TabletKeys.MARK_UNMARKED, name);
      case FULL -> Component.translatable(TabletKeys.MARK_FULL, name, patrol.limit());
      case NO_AREA -> Component.translatable(TabletKeys.ROUTE_NO_AREA);
      case OUTSIDE_AREA -> Component.translatable(TabletKeys.ROUTE_OUTSIDE_AREA, name);
      case NOT_SOLID -> Component.translatable(TabletKeys.ROUTE_NOT_SOLID);
    };
  }

  private static void say(ServerPlayer player, Component text) {
    player.displayClientMessage(text, true);
  }
}
