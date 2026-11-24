package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.dock.XpUnloadCall;
import io.github.zenndg.cubit.menu.TabletKeys;
import io.github.zenndg.cubit.menu.TabletRobotMenu;
import io.github.zenndg.cubit.menu.XpKeys;
import java.util.OptionalInt;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The XP tab on the Tablet: the tank, the modes and the switch of the excess, without the trade,
 * and the Unload XP, with the reasons of Go to Dock (answers 2075, 2079, 2107 and 2115).
 */
final class TabletXpPanel {
  /** The line of the panel at the top of the content, under the header of the Tablet. */
  static final int TOP = 14;

  private static final Box UNLOAD = new Box(172, 40, 110, 16);
  private static final int MODES_TITLE_Y = 111;
  private static final int FIRST_MODE = 124;
  private static final int EXCESS_Y = 181;

  private final ScreenPainter painter;
  private final TabletRobotMenu menu;
  private final int right;
  private final XpTankView tank;
  private final XpModeRows modes;
  private final XpExcessRow excess;

  TabletXpPanel(ScreenPainter painter, TabletRobotMenu menu, int right) {
    this.painter = painter;
    this.menu = menu;
    this.right = right;
    this.tank = new XpTankView(painter, XpPanel.GLASS, right);
    this.modes = new XpModeRows(painter, XpPanel.modeRows(), XpModeRows.NARROW, FIRST_MODE, right);
    this.excess = new XpExcessRow(painter, menu.xp(), EXCESS_Y, right);
  }

  void render(GuiGraphics graphics, double mouseX, double mouseY) {
    XpHeading.render(graphics, painter, XpKeys.TANK, XpPanel.TANK_TITLE_Y, right);
    tank.render(graphics, menu.xp().tank(), menu.xp().rate());
    FlatButton.paint(
        graphics,
        UNLOAD,
        Component.translatable(XpKeys.UNLOAD),
        !menu.xpUnloadCall().ready(),
        UNLOAD.contains((int) mouseX, (int) mouseY));
    XpHeading.render(graphics, painter, XpKeys.MODES, MODES_TITLE_Y, right);
    modes.render(graphics, menu.xp().mode().ordinal());
    excess.render(graphics);
  }

  /** The button under the point, as the server numbers them. */
  OptionalInt buttonAt(double x, double y) {
    if (UNLOAD.contains((int) x, (int) y)) {
      return menu.xpUnloadCall().ready()
          ? OptionalInt.of(TabletRobotMenu.UNLOAD_XP_BUTTON)
          : OptionalInt.empty();
    }
    OptionalInt toggled = excess.buttonAt(x, y);
    return toggled.isPresent() ? toggled : XpPanel.modeAt(modes, menu.xp(), x, y);
  }

  boolean renderDetail(GuiGraphics graphics, Font font, int mouseX, int mouseY, int left, int top) {
    if (UNLOAD.contains(mouseX - left, mouseY - top)) {
      XpTips.show(
          graphics, font, XpKeys.UNLOAD_DETAIL, reason(menu.xpUnloadCall()), mouseX, mouseY);
      return true;
    }
    return excess.renderDetail(graphics, font, mouseX, mouseY, left, top);
  }

  /** Why the Cubit does not go now, or null. */
  private static String reason(XpUnloadCall call) {
    return switch (call) {
      case READY -> null;
      case SWITCHED_OFF -> TabletKeys.DOCK_SWITCHED_OFF;
      case HIBERNATING -> TabletKeys.DOCK_HIBERNATING;
      case NO_DOCK -> TabletKeys.DOCK_OUT_OF_REACH;
      case EMPTY -> XpKeys.TANK_EMPTY;
    };
  }
}
