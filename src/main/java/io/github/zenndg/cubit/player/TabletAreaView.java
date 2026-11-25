package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.AreaDrawingSpec;
import io.github.zenndg.cubit.domain.robot.AreaMarking;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.AreaSpec;
import io.github.zenndg.cubit.entity.RobotTablet;
import io.github.zenndg.cubit.network.AreaMarkPayload;
import io.github.zenndg.cubit.network.CubitNetwork;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

/**
 * Sends the owner what the Area app draws: the drawing halfway with the limits of its Cubit, and
 * the area of the chosen Cubit in the dimension of the player (answers 2240 and 2358 to 2360).
 */
public final class TabletAreaView {
  private TabletAreaView() {}

  public static void sync(ServerPlayer player) {
    String dimension = TabletAreaApp.dimensionOf(player);
    Optional<AreaMarkPayload.Drawing> drawing =
        TabletAreaApp.marking(player)
            .filter(marking -> marking.dimension().equals(dimension))
            .flatMap(marking -> drawing(player, marking));
    Optional<AreaMarkPayload.Area> area =
        TabletUse.chosen(player)
            .flatMap(robot -> robot.workArea().area())
            .filter(found -> found.isIn(dimension))
            .map(TabletAreaView::area);
    CubitNetwork.sendAreaMark(player, new AreaMarkPayload(drawing, area));
  }

  private static Optional<AreaMarkPayload.Drawing> drawing(
      ServerPlayer player, AreaMarking marking) {
    return RobotTablet.find(player, marking.robot())
        .map(RobotTablet::areaOf)
        .map(tier ->
            new AreaMarkPayload.Drawing(
                marking.drawing().points().stream().map(TabletAreaView::pos).toList(),
                marking.drawing().closed(),
                limits(tier)));
  }

  private static AreaMarkPayload.Limits limits(AreaSpec tier) {
    AreaDrawingSpec spec = CubitMod.CONFIG.areaDrawing();
    return new AreaMarkPayload.Limits(
        tier.sizeBlocks(),
        tier.heightBlocks(),
        spec.minPoints(),
        spec.maxPoints(),
        spec.reachBlocks());
  }

  private static AreaMarkPayload.Area area(WorkArea area) {
    return new AreaMarkPayload.Area(
        area.shape().corners().stream()
            .map(corner -> new BlockPos(corner.x(), area.low(), corner.z()))
            .toList(),
        area.high());
  }

  private static BlockPos pos(AreaCorner corner) {
    return new BlockPos(corner.x(), corner.y(), corner.z());
  }
}
