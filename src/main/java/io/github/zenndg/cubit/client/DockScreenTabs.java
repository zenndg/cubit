package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.RobotMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import io.github.zenndg.cubit.menu.XpKeys;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;

/**
 * The Dock, Storage, Solar and XP tabs on the right of the dock screen, like the ones of the robot
 * screen, the Solar one with the icon of the solar panel of the Cubit and the XP one with the icon
 * of its tank (answers 956, 957, 960, 1395, 1837 and 1865). The open tab is only a choice of the
 * client.
 */
final class DockScreenTabs {
  private static final int DOCK_Y = 9;
  private static final int STORAGE_Y = 37;
  private static final int SOLAR_Y = 65;
  private static final int XP_Y = 93;

  private static final int[] TOPS = {DOCK_Y, STORAGE_Y, SOLAR_Y, XP_Y};

  private Tab open = Tab.DOCK;

  /** The tabs, from the top. */
  enum Tab {
    DOCK(DOCK_Y, ScreenKeys.DOCK_TAB_DOCK),
    STORAGE(STORAGE_Y, ScreenKeys.DOCK_TAB_STORAGE),
    SOLAR(SOLAR_Y, ScreenKeys.DOCK_TAB_SOLAR),
    XP(XP_Y, XpKeys.TAB);

    private final int y;
    private final String name;

    Tab(int y, String name) {
      this.y = y;
      this.name = name;
    }

    String title() {
      return name;
    }
  }

  Tab open() {
    return open;
  }

  void open(Tab tab) {
    open = tab;
  }

  void renderBehind(GuiGraphics graphics, int left, int top) {
    ScreenTabStrip.renderBehind(graphics, left, top, TOPS, open.y);
  }

  void renderFront(GuiGraphics graphics, int left, int top) {
    ScreenTabStrip.renderOpen(graphics, left, top, open.y);
    int iconX = left + ScreenTabStrip.ICON_X;
    ScreenSprites.dockTab(graphics, iconX, top + DOCK_Y + ScreenTabStrip.ICON_Y);
    ScreenSprites.storageTab(graphics, iconX, top + STORAGE_Y + ScreenTabStrip.ICON_Y);
    ScreenSprites.function(
        graphics, iconX, top + SOLAR_Y + ScreenTabStrip.ICON_Y, RobotMenu.SOLAR_BUTTON);
    ScreenSprites.xpTab(graphics, iconX, top + XP_Y + ScreenTabStrip.ICON_Y);
  }

  /** The tab under a point given from the left of the tabs and the top of the window. */
  Optional<Tab> at(double x, double y) {
    return Arrays.stream(Tab.values())
        .filter(each -> ScreenTabStrip.contains(x, y, each.y))
        .findFirst();
  }
}
