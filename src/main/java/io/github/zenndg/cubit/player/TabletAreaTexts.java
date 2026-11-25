package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.AreaBounds;
import io.github.zenndg.cubit.domain.robot.AreaCheck;
import io.github.zenndg.cubit.domain.robot.AreaDrawing;
import io.github.zenndg.cubit.domain.robot.AreaDrawingSpec;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.domain.tier.AreaSpec;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotTablet;
import io.github.zenndg.cubit.menu.TabletKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** What the action bar says after a click of the Area app, with the reason of a refusal (2356). */
final class TabletAreaTexts {
  private TabletAreaTexts() {}

  static Component of(AreaDrawing.Click click, RobotEntity robot) {
    AreaDrawingSpec spec = CubitMod.CONFIG.areaDrawing();
    int points = click.drawing().points().size();
    return switch (click.step()) {
      case ADDED -> Component.translatable(TabletKeys.AREA_POINT, points, spec.maxPoints());
      case CLOSED -> closed(click.drawing());
      case REOPENED -> Component.translatable(TabletKeys.AREA_REOPENED, points);
      case ALREADY_CLOSED -> Component.translatable(TabletKeys.AREA_ALREADY_CLOSED);
      default -> refusal(click, spec, robot);
    };
  }

  private static Component refusal(
      AreaDrawing.Click click, AreaDrawingSpec spec, RobotEntity robot) {
    return switch (click.step()) {
      case TOO_FEW -> refused(TabletKeys.AREA_TOO_FEW, click.drawing().missing(spec));
      case FULL -> refused(TabletKeys.AREA_FULL, spec.maxPoints());
      case NOT_STRAIGHT -> refused(TabletKeys.AREA_NOT_STRAIGHT);
      case TOO_BIG -> tooBig(robot);
      case TOO_HIGH ->
          refused(
              TabletKeys.AREA_TOO_HIGH,
              robot.getDisplayName(),
              RobotTablet.areaOf(robot).heightBlocks());
      default -> refused(TabletKeys.AREA_CROSSES);
    };
  }

  static Component refusal(AreaCheck check, RobotEntity robot) {
    return switch (check) {
      case TOO_BIG -> tooBig(robot);
      case OTHER_DIMENSION -> refused(TabletKeys.AREA_OTHER_DIMENSION, robot.getDisplayName());
      case OUT_OF_REACH ->
          refused(TabletKeys.AREA_OUT_OF_REACH, robot.getDisplayName(), robot.dockCallRadius());
      case LOCKED, OK -> refused(TabletKeys.AREA_LOCKED, robot.getDisplayName());
    };
  }

  static Component applied(WorkArea area, RobotEntity robot) {
    AreaBounds bounds = area.bounds();
    return Component.translatable(
        TabletKeys.AREA_SET,
        robot.getDisplayName(),
        bounds.width(),
        bounds.height(),
        bounds.depth());
  }

  private static Component closed(AreaDrawing drawing) {
    AreaBounds bounds = drawing.bounds().orElseThrow();
    return Component.translatable(
            TabletKeys.AREA_CLOSED, bounds.width(), bounds.height(), bounds.depth())
        .withStyle(ChatFormatting.GREEN);
  }

  private static Component tooBig(RobotEntity robot) {
    AreaSpec tier = RobotTablet.areaOf(robot);
    return refused(
        TabletKeys.AREA_TOO_BIG,
        robot.getDisplayName(),
        tier.sizeBlocks(),
        tier.sizeBlocks(),
        tier.heightBlocks());
  }

  private static Component refused(String key, Object... values) {
    return Component.translatable(key, values).withStyle(ChatFormatting.RED);
  }
}
