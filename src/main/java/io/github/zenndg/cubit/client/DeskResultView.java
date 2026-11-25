package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.DeskResult;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

/**
 * The item beside the grid: what is ready of the recipe, to take from here, or a faded drawing of
 * it (answers 2473 and 2479). It only shows and asks: the items live in the output, on the server.
 */
final class DeskResultView {
  private static final int X = 194;
  private static final int Y = 44;
  private static final int ICON_INSET = 1;
  private static final int ICON_SIZE = 16;
  private static final int FADED_VEIL = 0xA016306F;

  private final EngineeringDeskMenu menu;
  private final IntConsumer send;

  DeskResultView(EngineeringDeskMenu menu, IntConsumer send) {
    this.menu = menu;
    this.send = send;
  }

  void render(GuiGraphics graphics, int left, int top, ItemStack result) {
    int x = left + X;
    int y = top + Y;
    BlueprintPainter.slot(graphics, x, y);
    DeskResult ready = menu.ready(result);
    if (ready.isEmpty()) {
      renderFaded(graphics, result, x + ICON_INSET, y + ICON_INSET);
      return;
    }
    graphics.renderItem(ready.first(), x + ICON_INSET, y + ICON_INSET);
    graphics.renderItemDecorations(
        Minecraft.getInstance().font,
        ready.first(),
        x + ICON_INSET,
        y + ICON_INSET,
        ready.count() > 1 ? Integer.toString(ready.count()) : null);
  }

  private static void renderFaded(GuiGraphics graphics, ItemStack result, int x, int y) {
    if (result.isEmpty()) {
      return;
    }
    graphics.renderFakeItem(result, x, y);
    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, BlueprintPainter.OVER_ITEMS);
    graphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, FADED_VEIL);
    graphics.pose().popPose();
  }

  boolean isOver(double mouseX, double mouseY, int left, int top) {
    int x = left + X;
    int y = top + Y;
    return mouseX >= x
        && mouseX < x + ScreenPainter.SLOT_SIZE
        && mouseY >= y
        && mouseY < y + ScreenPainter.SLOT_SIZE;
  }

  /** The item ready, with its own name and data, or the one the recipe makes. */
  ItemStack shown(ItemStack result) {
    DeskResult ready = menu.ready(result);
    return ready.isEmpty() ? result : ready.first();
  }

  void click() {
    send.accept(Screen.hasShiftDown() ? DeskControl.TAKE_ALL : DeskControl.TAKE);
  }
}
