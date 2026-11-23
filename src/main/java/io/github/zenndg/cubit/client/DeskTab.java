package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.menu.CascadeMenuPart;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The tabs of the Engineering Desk, in the order of the strip (answers 1473 and 2006). */
enum DeskTab {
  PROJECT(9),
  STORAGE(37),
  CASCADE(65);

  static final int[] TOPS = {PROJECT.top, STORAGE.top, CASCADE.top};
  private static final int ITEM_SHIFT = -2;
  private static final int ICON_SIZE = 16;
  private static final int LOCK_X = 12;
  private static final int LOCK_Y = 12;
  private static final float LOCKED_SHADE = 0.45f;
  private static final String CASCADE_PATH = "textures/gui/cascade_tab.png";
  private static final ResourceLocation CASCADE_ICON =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, CASCADE_PATH);

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

  /** A plan on its way locks the other tabs, and a tier without it, the Cascade (2015, 2020). */
  boolean isLocked(CascadeMenuPart cascade) {
    return this == CASCADE ? !cascade.allowed() && !cascade.locks() : cascade.locks();
  }

  static void renderIcons(GuiGraphics graphics, int left, int windowTop, CascadeMenuPart cascade) {
    int x = left + ScreenTabStrip.ICON_X + ITEM_SHIFT;
    for (DeskTab tab : values()) {
      tab.renderIcon(
          graphics,
          x,
          windowTop + ScreenTabStrip.ICON_Y + ITEM_SHIFT + tab.top,
          tab.isLocked(cascade));
    }
  }

  private void renderIcon(GuiGraphics graphics, int x, int y, boolean locked) {
    float shade = locked ? LOCKED_SHADE : 1f;
    RenderSystem.setShaderColor(shade, shade, shade, 1f);
    switch (this) {
      case PROJECT -> graphics.renderItem(new ItemStack(CubitItems.ENGINEERING_DESK.get()), x, y);
      case STORAGE -> graphics.renderItem(new ItemStack(Items.CHEST), x, y);
      case CASCADE ->
          graphics.blit(CASCADE_ICON, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }
    graphics.flush();
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    if (locked) {
      BlueprintPainter.lockBadge(graphics, x + LOCK_X, y + LOCK_Y);
    }
  }
}
