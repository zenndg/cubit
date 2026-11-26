package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.NetworkKeys;
import io.github.zenndg.cubit.menu.ScreenKeys;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;

/**
 * The Energy and Storage tabs of the Network Server, at their final places, with room for Network,
 * XP and Log (answers 2277 and 2397). The open tab is only a choice of the client.
 */
final class ServerScreenTabs {
  private static final int ENERGY_Y = 37;
  private static final int STORAGE_Y = 93;
  private static final int[] TOPS = {ENERGY_Y, STORAGE_Y};

  private Tab open = Tab.ENERGY;

  /** The tabs, from the top. */
  enum Tab {
    ENERGY(ENERGY_Y, NetworkKeys.TAB_ENERGY),
    STORAGE(STORAGE_Y, ScreenKeys.DOCK_TAB_STORAGE);

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
    ScreenSprites.energyTab(graphics, iconX, top + ENERGY_Y + ScreenTabStrip.ICON_Y);
    ScreenSprites.storageTab(graphics, iconX, top + STORAGE_Y + ScreenTabStrip.ICON_Y);
  }

  /** The tab under a point given from the left of the tabs and the top of the window. */
  Optional<Tab> at(double x, double y) {
    return Arrays.stream(Tab.values())
        .filter(each -> ScreenTabStrip.contains(x, y, each.y))
        .findFirst();
  }
}
