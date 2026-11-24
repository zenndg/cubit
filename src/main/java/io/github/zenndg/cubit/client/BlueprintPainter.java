package io.github.zenndg.cubit.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The look of a designer's board for the screen of the Engineering Desk: a blue sheet with a light
 * grid, frames and slots in the white lines of a technical drawing (answers 1467 and 1524).
 */
final class BlueprintPainter {
  static final int SHEET = 0xFF1D3C8F;
  static final int LINE = 0xFFDCE6FF;
  static final int LINE_SOFT = 0x80DCE6FF;
  static final int CELL = 0xFF16306F;
  static final int TEXT = 0xFFFFFFFF;
  static final int TEXT_SOFT = 0xFFB8C8F0;
  static final int CRAFTING = 0xFF4FC3F7;
  static final int PAUSED = 0xFFFFB300;
  static final int IDLE = 0xFF9AA7C7;
  static final int ENERGY = 0xFFF2A14A;
  static final int CANCEL = 0xFF8B2635;
  static final int PRIMARY = 0xFF3F6BD0;
  static final int DONE = 0xFF8BE9A0;
  static final int WARNING = 0xFFFF8A80;
  static final int LOCK_SIZE = 5;
  static final int OVER_ITEMS = 300;
  static final float SMALL_SCALE = 0.75f;
  private static final int GOLD = 0xFFFFCA28;
  private static final int LOCK_PATCH = 0xB0101830;
  private static final int LOCK_BODY = 3;
  private static final int MINOR_LINE = 0xFF24469C;
  private static final int MAJOR_LINE = 0xFF2C52AA;
  private static final int MINOR_STEP = 8;
  private static final int MAJOR_STEP = 32;
  private static final int DASH = 2;
  private static final int SLOT_SIZE = ScreenPainter.SLOT_SIZE;

  private BlueprintPainter() {}

  /** The sheet with its grid and a white border. */
  static void sheet(GuiGraphics graphics, int x, int y, int width, int height) {
    graphics.fill(x, y, x + width, y + height, SHEET);
    for (int column = MINOR_STEP; column < width; column += MINOR_STEP) {
      int colour = column % MAJOR_STEP == 0 ? MAJOR_LINE : MINOR_LINE;
      graphics.fill(x + column, y + 1, x + column + 1, y + height - 1, colour);
    }
    for (int row = MINOR_STEP; row < height; row += MINOR_STEP) {
      int colour = row % MAJOR_STEP == 0 ? MAJOR_LINE : MINOR_LINE;
      graphics.fill(x + 1, y + row, x + width - 1, y + row + 1, colour);
    }
    graphics.renderOutline(x, y, width, height, LINE);
  }

  static void slot(GuiGraphics graphics, int x, int y) {
    graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, CELL);
    graphics.renderOutline(x, y, SLOT_SIZE, SLOT_SIZE, LINE_SOFT);
  }

  /** A panel with a dashed white frame, as a part drawn on the board. */
  static void dashed(GuiGraphics graphics, int x, int y, int width, int height) {
    for (int step = 0; step < width; step += DASH * 2) {
      int end = Math.min(x + width, x + step + DASH);
      graphics.fill(x + step, y, end, y + 1, LINE_SOFT);
      graphics.fill(x + step, y + height - 1, end, y + height, LINE_SOFT);
    }
    for (int step = 0; step < height; step += DASH * 2) {
      int end = Math.min(y + height, y + step + DASH);
      graphics.fill(x, y + step, x + 1, end, LINE_SOFT);
      graphics.fill(x + width - 1, y + step, x + width, end, LINE_SOFT);
    }
  }

  static void line(GuiGraphics graphics, int x, int y, int width) {
    graphics.fill(x, y, x + width, y + 1, LINE_SOFT);
  }

  /** A bar with a white frame, at least one pixel while anything is left. */
  static void bar(GuiGraphics graphics, ScreenPainter.Box box, long value, long max, int colour) {
    graphics.fill(box.x(), box.y(), box.right(), box.bottom(), CELL);
    graphics.renderOutline(box.x(), box.y(), box.width(), box.height(), LINE);
    int inner = box.width() - 2;
    int filled =
        max <= 0 || value <= 0 ? 0 : (int) Math.min(inner, (value * inner + max - 1) / max);
    graphics.fill(box.x() + 1, box.y() + 1, box.x() + 1 + filled, box.bottom() - 1, colour);
  }

  /** A dashed arrow to the right, as the one between the input and the output. */
  static void arrow(GuiGraphics graphics, int x, int y, int width) {
    for (int step = 0; step < width - DASH; step += DASH * 2) {
      graphics.fill(x + step, y, x + step + DASH, y + 1, LINE_SOFT);
    }
    int tip = x + width;
    for (int side = 0; side <= DASH; side++) {
      graphics.fill(tip - side - 1, y - side, tip - side, y + side + 1, LINE_SOFT);
    }
  }

  /** A padlock over an item, small enough for its corner. */
  static void padlock(GuiGraphics graphics, int x, int y, int color) {
    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, OVER_ITEMS);
    graphics.renderOutline(x + 1, y, LOCK_BODY, LOCK_SIZE - LOCK_BODY + 1, color);
    graphics.fill(x, y + LOCK_SIZE - LOCK_BODY, x + LOCK_SIZE, y + LOCK_SIZE, color);
    graphics.pose().popPose();
  }

  /** A golden padlock on a dark patch, for what a running plan locks (answer 2044). */
  static void lockBadge(GuiGraphics graphics, int x, int y) {
    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, OVER_ITEMS);
    graphics.fill(x - 1, y - 1, x + LOCK_SIZE + 1, y + LOCK_SIZE + 1, LOCK_PATCH);
    graphics.pose().popPose();
    padlock(graphics, x, y, GOLD);
  }

  /** A text from the left, which shrinks to the room it has. */
  static void fromLeft(GuiGraphics graphics, Font font, Component text, int x, int y, Look look) {
    float scale = Math.min(look.scale(), (float) look.room() / Math.max(1, font.width(text)));
    graphics.pose().pushPose();
    graphics.pose().translate(x, y + font.lineHeight * (look.scale() - scale) / 2, 0);
    graphics.pose().scale(scale, scale, 1);
    graphics.drawString(font, text, 0, 0, look.color(), false);
    graphics.pose().popPose();
  }

  /** The color of a text, the width it may use and its scale when it fits. */
  record Look(int color, int room, float scale) {}

  /** A centered text that shrinks to the room it has, as a large order (answer 2040). */
  static void fitted(
      GuiGraphics graphics, Font font, Component text, int x, int y, int room, int color) {
    int width = font.width(text);
    float scale = Math.min(1f, (float) room / Math.max(1, width));
    graphics.pose().pushPose();
    graphics.pose().translate(x, y + font.lineHeight * (1 - scale) / 2, 0);
    graphics.pose().scale(scale, scale, 1);
    graphics.drawCenteredString(font, text, 0, 0, color);
    graphics.pose().popPose();
  }
}
