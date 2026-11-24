package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The heading of a part of an XP tab, with its rule. */
final class XpHeading {
  private static final int LEFT = 7;
  private static final int TITLE_X = 8;
  private static final int LINE = 9;
  private static final int RULE = 9;

  private XpHeading() {}

  static void render(GuiGraphics graphics, ScreenPainter painter, String key, int y, int right) {
    painter.text(
        graphics,
        Component.translatable(key).withStyle(ChatFormatting.BOLD),
        new Box(TITLE_X, y, right - TITLE_X, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    graphics.fill(LEFT, y + RULE, right, y + RULE + 1, ScreenPainter.SHADE);
    graphics.fill(LEFT, y + RULE + 1, right, y + RULE + 2, ScreenPainter.WHITE);
  }
}
