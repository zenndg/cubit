package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.menu.XpKeys;
import io.github.zenndg.cubit.menu.XpMenuPart;
import java.util.OptionalInt;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The switch Delete the excess, which only the store uses (answers 1827 and 2082). */
final class XpExcessRow {
  private static final int LEFT = 7;
  private static final int HEIGHT = 18;
  private static final int TEXT_X = 11;
  private static final int TEXT_TOP = 6;
  private static final int TEXT_GAP = 3;
  private static final int LINE = 9;
  private static final int TOGGLE_GAP = 4;
  private static final int TOGGLE_TOP = 3;

  private final ScreenPainter painter;
  private final XpMenuPart xp;
  private final int top;
  private final int right;

  XpExcessRow(ScreenPainter painter, XpMenuPart xp, int top, int right) {
    this.painter = painter;
    this.xp = xp;
    this.top = top;
    this.right = right;
  }

  void render(GuiGraphics graphics) {
    graphics.fill(LEFT, top, right, top + HEIGHT, ScreenPainter.ROW);
    painter.text(
        graphics,
        Component.translatable(XpKeys.DELETE_EXCESS),
        new Box(TEXT_X, top + TEXT_TOP, toggleX() - TEXT_X - TEXT_GAP, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    ScreenToggle.draw(graphics, painter, toggleX(), top + TOGGLE_TOP, xp.deleteExcess(), storing());
    if (!storing()) {
      graphics.fill(LEFT, top, right, top + HEIGHT, ScreenToggle.DISABLED);
    }
  }

  OptionalInt buttonAt(double x, double y) {
    return storing() && ScreenToggle.contains(toggleX(), top + TOGGLE_TOP, x, y)
        ? OptionalInt.of(XpMenuPart.EXCESS_BUTTON)
        : OptionalInt.empty();
  }

  boolean renderDetail(GuiGraphics graphics, Font font, int mouseX, int mouseY, int left, int y) {
    int x = mouseX - left;
    int inRow = mouseY - y - top;
    if (x < LEFT || x >= right || inRow < 0 || inRow >= HEIGHT) {
      return false;
    }
    String reason = storing() ? null : XpKeys.ONLY_STORE;
    XpTips.show(graphics, font, XpKeys.DELETE_EXCESS_DETAIL, reason, mouseX, mouseY);
    return true;
  }

  private boolean storing() {
    return xp.mode() == XpMode.STORE;
  }

  private int toggleX() {
    return right - TOGGLE_GAP - ScreenToggle.WIDTH;
  }
}
