package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.domain.robot.RoutePoint;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.network.PatrolRoutePayload;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * The route of the Galactic Patroller chosen in the Tablet, while the Mark app is open: only its
 * points in the dimension of the player, numbered in the order of the route, so routes of different
 * Patrollers never mix (answers 1580 and 1612).
 */
final class TabletRouteView {
  private TabletRouteView() {}

  static PatrolRoutePayload of(ServerPlayer player) {
    Optional<RobotEntity> patroller =
        TabletUse.chosen(player).filter(robot -> robot.workArea().work().patrol().hasCard());
    if (!marking(player) || patroller.isEmpty()) {
      return PatrolRoutePayload.NONE;
    }
    String dimension = player.level().dimension().location().toString();
    List<RoutePoint> route = patroller.get().workArea().work().patrol().route().points();
    List<PatrolRoutePayload.Point> points = new ArrayList<>();
    for (int index = 0; index < route.size(); index++) {
      RoutePoint point = route.get(index);
      if (point.in(dimension)) {
        BlockPos pos = new BlockPos(point.x(), point.y(), point.z());
        points.add(new PatrolRoutePayload.Point(pos, index + 1));
      }
    }
    return new PatrolRoutePayload(points);
  }

  private static boolean marking(ServerPlayer player) {
    for (InteractionHand hand : InteractionHand.values()) {
      ItemStack held = player.getItemInHand(hand);
      if (held.is(CubitItems.TABLET.get()) && TabletItem.appOf(held) == TabletApp.MARK) {
        return true;
      }
    }
    return false;
  }
}
