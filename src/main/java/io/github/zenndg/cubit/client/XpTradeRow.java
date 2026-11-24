package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.xp.PlayerXp;
import io.github.zenndg.cubit.domain.xp.XpRate;
import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.menu.XpKeys;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** The row with the level of the player and the − and + of the tank (answers 2072 and 2076). */
final class XpTradeRow {
  private static final int TEXT_INSET = 4;
  private static final int TEXT_TOP = 6;
  private static final int LINE = 9;
  private static final int BUTTON = 12;
  private static final int GAP = 3;
  private static final int NAME_WIDTH = 30;
  private static final String TAKE = "−";
  private static final String SEND = "+";

  private final ScreenPainter painter;
  private final Box row;
  private final Supplier<Reading> reading;

  XpTradeRow(ScreenPainter painter, Box row, Supplier<Reading> reading) {
    this.painter = painter;
    this.row = row;
    this.reading = reading;
  }

  /** What the row reads, with why nothing moves now and what + also says, or null for neither. */
  record Reading(XpTank tank, XpRate rate, String blocked, String sendNote) {}

  void render(GuiGraphics graphics, double mouseX, double mouseY) {
    graphics.fill(row.x(), row.y(), row.right(), row.bottom(), ScreenPainter.ROW);
    Player player = Minecraft.getInstance().player;
    int textX = row.x() + TEXT_INSET;
    painter.text(
        graphics,
        Component.translatable(XpKeys.YOUR_LEVEL, player == null ? 0 : player.experienceLevel),
        new Box(textX, textY(), take().x() - textX - GAP, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    Component name = Component.translatable(XpKeys.TANK).withStyle(ChatFormatting.BOLD);
    int used = Math.min(NAME_WIDTH, painter.width(name, ScreenPainter.FULL_SCALE));
    int nameX = take().right() + GAP + (NAME_WIDTH - used) / 2;
    painter.text(
        graphics,
        name,
        new Box(nameX, textY(), send().x() - nameX, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    button(graphics, take(), TAKE, takeReason() != null, mouseX, mouseY);
    button(graphics, send(), SEND, sendReason() != null, mouseX, mouseY);
  }

  private static void button(
      GuiGraphics graphics, Box box, String label, boolean muted, double x, double y) {
    FlatButton.paint(
        graphics, box, Component.literal(label), muted, box.contains((int) x, (int) y));
  }

  /** Why − does nothing now, or null. */
  private String takeReason() {
    Reading now = reading.get();
    if (now.blocked() != null) {
      return now.blocked();
    }
    return now.tank().isEmpty(now.rate()) ? XpKeys.TANK_EMPTY : null;
  }

  /** Why + does nothing now, or null. */
  private String sendReason() {
    Reading now = reading.get();
    if (now.blocked() != null) {
      return now.blocked();
    }
    if (now.tank().isFull(now.rate())) {
      return XpKeys.TANK_FULL;
    }
    Player player = Minecraft.getInstance().player;
    boolean none =
        player == null
            || PlayerXp.of(player.experienceLevel, player.experienceProgress).toLevelDown() == 0;
    return none ? XpKeys.NO_XP : null;
  }

  /** The button under the point, as the server numbers it, if it does something now. */
  OptionalInt buttonAt(double x, double y, int takeButton, int sendButton) {
    if (take().contains((int) x, (int) y) && takeReason() == null) {
      return OptionalInt.of(takeButton);
    }
    if (send().contains((int) x, (int) y) && sendReason() == null) {
      return OptionalInt.of(sendButton);
    }
    return OptionalInt.empty();
  }

  /** What the button under the point does and, when it does nothing now, why. */
  boolean renderDetail(GuiGraphics graphics, Font font, int mouseX, int mouseY, int left, int top) {
    int x = mouseX - left;
    int y = mouseY - top;
    if (take().contains(x, y)) {
      XpTips.show(graphics, font, XpKeys.TAKE_DETAIL, takeReason(), mouseX, mouseY);
      return true;
    }
    if (send().contains(x, y)) {
      String reason = sendReason();
      String note = reason == null ? reading.get().sendNote() : reason;
      XpTips.show(graphics, font, XpKeys.SEND_DETAIL, note, mouseX, mouseY);
      return true;
    }
    return false;
  }

  /** In the middle of the row, whatever its height. */
  private int textY() {
    return row.y() + (row.height() - TEXT_TOP) / 2;
  }

  private int buttonY() {
    return row.y() + (row.height() - BUTTON) / 2;
  }

  private Box take() {
    return new Box(send().x() - GAP - NAME_WIDTH - GAP - BUTTON, buttonY(), BUTTON, BUTTON);
  }

  private Box send() {
    return new Box(row.right() - GAP - BUTTON, buttonY(), BUTTON, BUTTON);
  }
}
