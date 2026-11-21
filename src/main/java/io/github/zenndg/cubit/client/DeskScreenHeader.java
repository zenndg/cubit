package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.desk.DeskLook;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

/**
 * The top of the desk screen: the name, the tier in a badge of its color and what the desk does,
 * with a dot of the color of the laptop screen (answers 1454 and 1473).
 */
final class DeskScreenHeader {
  static final int LINE_Y = 17;
  private static final int MARGIN = 8;
  private static final int TITLE_Y = 6;
  private static final int BADGE_GAP = 4;
  private static final int BADGE_PADDING = 3;
  private static final int BADGE_TOP = 4;
  private static final int BADGE_HEIGHT = 11;
  private static final int BADGE_TEXT = 0xFF1A1A1A;
  private static final int DOT_SIZE = 4;
  private static final int DOT_GAP = 4;
  private static final int DOT_TOP = 8;

  private DeskScreenHeader() {}

  static void render(
      GuiGraphics graphics,
      Font font,
      Component title,
      RobotTier tier,
      DeskStatus status,
      int width) {
    graphics.drawString(font, title, MARGIN, TITLE_Y, BlueprintPainter.TEXT, false);
    Component tierName = TierLook.name(tier);
    int badgeX = MARGIN + font.width(title) + BADGE_GAP;
    int badgeWidth = font.width(tierName) + BADGE_PADDING * 2;
    graphics.fill(
        badgeX,
        BADGE_TOP,
        badgeX + badgeWidth,
        BADGE_TOP + BADGE_HEIGHT,
        FastColor.ARGB32.opaque(TierLook.color(tier)));
    graphics.drawString(font, tierName, badgeX + BADGE_PADDING, TITLE_Y, BADGE_TEXT, false);
    Component text = Component.translatable(DeskScreenKeys.status(status));
    int colour = colour(DeskLook.of(status));
    int textX = width - MARGIN - font.width(text);
    graphics.drawString(font, text, textX, TITLE_Y, colour, false);
    int dotX = textX - DOT_GAP - DOT_SIZE;
    graphics.fill(dotX, DOT_TOP, dotX + DOT_SIZE, DOT_TOP + DOT_SIZE, colour);
    BlueprintPainter.line(graphics, MARGIN, LINE_Y, width - MARGIN * 2);
  }

  private static int colour(DeskLook look) {
    return switch (look) {
      case CRAFTING -> BlueprintPainter.CRAFTING;
      case PAUSED -> BlueprintPainter.PAUSED;
      case IDLE -> BlueprintPainter.IDLE;
    };
  }
}
