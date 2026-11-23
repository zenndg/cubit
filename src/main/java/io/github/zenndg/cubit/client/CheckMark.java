package io.github.zenndg.cubit.client;

import net.minecraft.client.gui.GuiGraphics;

/** The small check beside a finished task, drawn as the font has no sharp one. */
final class CheckMark {
  private static final int[] HEIGHTS = {3, 4, 5, 4, 3, 2};
  private static final int STROKE = 2;

  private CheckMark() {}

  static void render(GuiGraphics graphics, int x, int y, int color) {
    graphics.pose().pushPose();
    graphics.pose().translate(x, y, 0);
    graphics.pose().scale(BlueprintPainter.SMALL_SCALE, BlueprintPainter.SMALL_SCALE, 1);
    for (int column = 0; column < HEIGHTS.length; column++) {
      int top = HEIGHTS[column];
      graphics.fill(column, top, column + 1, top + STROKE, color);
    }
    graphics.pose().popPose();
  }
}
