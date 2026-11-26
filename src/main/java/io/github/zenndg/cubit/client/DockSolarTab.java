package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.client.ScreenPainter.Colors;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.menu.InductionDockMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The Solar tab of every Dock: where the panel is, with the color of the light of the doors, what
 * the sky does, why it stops, what it gives now and at most at the tier of the dock (answers 1395,
 * 1396, 1863 and 1865).
 */
final class DockSolarTab {
  private static final int LINE = 9;
  private static final int DETAIL_LINE = 7;
  private static final Box TITLE = new Box(8, 6, 92, LINE);
  private static final Box STATUS = new Box(100, 6, 68, LINE);
  private static final Box STATE = new Box(8, 22, 160, LINE);
  private static final Box REASON = new Box(8, 34, 160, LINE);
  private static final Box NOW = new Box(8, 46, 160, LINE);
  private static final Box MAX = new Box(8, 58, 160, LINE);
  private static final Box PANEL = new Box(8, 70, 160, LINE);
  private static final Box HINT = new Box(8, 86, 160, DETAIL_LINE);

  private DockSolarTab() {}

  static void render(ScreenPainter painter, GuiGraphics graphics, InductionDockMenu menu) {
    Component title =
        Component.translatable(ScreenKeys.DOCK_TAB_SOLAR).withStyle(ChatFormatting.BOLD);
    painter.text(graphics, title, TITLE, ScreenPainter.TEXT, ScreenPainter.FULL_SCALE);
    SunState state = menu.solar().sun();
    Component sun = Component.translatable(ScreenKeys.dockSun(state));
    Component panel = Component.translatable(ScreenKeys.dockPanel(menu.solar().panel()));
    painter.status(graphics, panel, STATUS, lightOf(menu.solar().panel()));
    painter.pair(
        graphics,
        STATE,
        Component.translatable(ScreenKeys.DOCK_SOLAR_STATUS),
        sun,
        Colors.TITLE_FIRST);
    if (!state.generates()) {
      painter.pair(
          graphics,
          REASON,
          Component.translatable(ScreenKeys.DOCK_SOLAR_REASON),
          Component.translatable(ScreenKeys.dockSunReason(state)),
          Colors.TITLE_FIRST);
    }
    renderRates(painter, graphics, menu);
  }

  private static void renderRates(
      ScreenPainter painter, GuiGraphics graphics, InductionDockMenu menu) {
    painter.pair(
        graphics,
        NOW,
        Component.translatable(ScreenKeys.DOCK_SOLAR_NOW),
        Component.literal(EnergyFormat.rate(menu.solar().now())),
        Colors.TITLE_FIRST);
    painter.pair(
        graphics,
        MAX,
        Component.translatable(ScreenKeys.DOCK_SOLAR_MAX),
        Component.literal(EnergyFormat.rate(menu.solar().most())),
        Colors.TITLE_FIRST);
    painter.pair(
        graphics,
        PANEL,
        Component.translatable(ScreenKeys.DOCK_SOLAR_PANEL),
        Component.translatable(ScreenKeys.dockPanel(menu.solar().panel())),
        Colors.TITLE_FIRST);
    Component hint =
        Component.translatable(
            menu.solar().givesAtNight()
                ? ScreenKeys.DOCK_SOLAR_NIGHT_HINT
                : ScreenKeys.DOCK_SOLAR_HINT);
    painter.text(graphics, hint, HINT, ScreenPainter.DETAIL, ScreenPainter.DETAIL_SCALE);
  }

  /** The light of the doors: green on the tower, red in the cabinet, yellow on the way (1863). */
  private static int lightOf(PanelState state) {
    return switch (state) {
      case DEPLOYED -> ScreenPainter.STATUS_GREEN;
      case STORED -> ScreenPainter.STATUS_RED;
      case DEPLOYING, RETRACTING -> ScreenPainter.STATUS_YELLOW;
    };
  }
}
