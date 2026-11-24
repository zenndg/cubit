package io.github.zenndg.cubit.client;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The tooltips of the XP tabs: what something does and, in gray, a note, when there is one. */
final class XpTips {
  private XpTips() {}

  static void show(GuiGraphics graphics, Font font, String what, String note, int x, int y) {
    Component detail = Component.translatable(what);
    List<Component> lines =
        note == null
            ? List.of(detail)
            : List.of(detail, Component.translatable(note).withStyle(ChatFormatting.GRAY));
    graphics.renderComponentTooltip(font, lines, x, y);
  }
}
