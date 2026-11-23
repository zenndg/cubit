package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The tabs of the Engineering Desk, in the order of the strip (answers 1473 and 2006). */
enum DeskTab {
  PROJECT(9),
  STORAGE(37),
  CASCADE(65);

  static final int[] TOPS = {PROJECT.top, STORAGE.top, CASCADE.top};
  private static final int ITEM_SHIFT = -2;

  private final int top;

  DeskTab(int top) {
    this.top = top;
  }

  int top() {
    return top;
  }

  /** The tab at a point from the left of the tabs and the top of the window, or nothing. */
  static DeskTab at(double x, double y) {
    for (DeskTab tab : values()) {
      if (ScreenTabStrip.contains(x, y, tab.top)) {
        return tab;
      }
    }
    return null;
  }

  static void renderIcons(GuiGraphics graphics, int left, int windowTop) {
    int x = left + ScreenTabStrip.ICON_X + ITEM_SHIFT;
    int y = windowTop + ScreenTabStrip.ICON_Y + ITEM_SHIFT;
    graphics.renderItem(new ItemStack(CubitItems.ENGINEERING_DESK.get()), x, y + PROJECT.top);
    graphics.renderItem(new ItemStack(Items.CHEST), x, y + STORAGE.top);
    graphics.renderItem(new ItemStack(CubitItems.CIRCUIT_BOARD.get()), x, y + CASCADE.top);
  }
}
