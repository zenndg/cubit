package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.xp.PlayerXp;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.menu.RobotMenu;
import io.github.zenndg.cubit.menu.XpKeys;
import io.github.zenndg.cubit.menu.XpMenuPart;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * The XP tab of the Cubit: the tank, the trade with the owner, the modes and the switch of the
 * excess, which only the store uses (answers 1830, 2076 and 2082).
 */
final class XpPanel {
  private static final int LEFT = 7;
  private static final int TITLE_X = 8;
  private static final int LINE = 9;
  private static final int DETAIL_LINE = 7;
  private static final int TANK_TITLE_Y = 18;
  private static final int MODES_TITLE_Y = 131;
  private static final int RULE = 9;
  private static final int TRADE_Y = 108;
  private static final int ROW_HEIGHT = 18;
  private static final int TEXT_X = 11;
  private static final int TEXT_TOP = 6;
  private static final int BUTTON = 12;
  private static final int BUTTON_GAP = 3;
  private static final int NAME_WIDTH = 30;
  private static final int FIRST_MODE = 144;
  private static final int MODE_HEIGHT = 20;
  private static final int MARK_X = 12;
  private static final int MARK_TOP = 5;
  private static final int MARK_SIZE = 10;
  private static final int MARK_DOT = 3;
  private static final int NAME_X = 30;
  private static final int NAME_TOP = 3;
  private static final int DETAIL_TOP = 12;
  private static final int NAME_INSET = 3;
  private static final int EXCESS_Y = 208;
  private static final int TOGGLE_GAP = 4;
  private static final int TOGGLE_TOP = 3;
  private static final String TAKE = "−";
  private static final String SEND = "+";

  private final ScreenPainter painter;
  private final RobotMenu menu;
  private final int right;
  private final XpTankView tank;

  XpPanel(ScreenPainter painter, RobotMenu menu, int right) {
    this.painter = painter;
    this.menu = menu;
    this.right = right;
    this.tank = new XpTankView(painter, right);
  }

  private XpMenuPart xp() {
    return menu.xp();
  }

  /** Drawn in the coordinates of the window, with the mouse in the same ones. */
  void render(GuiGraphics graphics, double mouseX, double mouseY) {
    heading(graphics, XpKeys.TANK, TANK_TITLE_Y);
    tank.render(graphics, xp().tank(), xp().rate());
    renderTrade(graphics, mouseX, mouseY);
    heading(graphics, XpKeys.MODES, MODES_TITLE_Y);
    for (XpMode mode : XpMode.values()) {
      renderMode(graphics, mode);
    }
    renderExcess(graphics);
  }

  private void heading(GuiGraphics graphics, String key, int y) {
    painter.text(
        graphics,
        Component.translatable(key).withStyle(ChatFormatting.BOLD),
        new Box(TITLE_X, y, right - TITLE_X, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    graphics.fill(LEFT, y + RULE, right, y + RULE + 1, ScreenPainter.SHADE);
    graphics.fill(LEFT, y + RULE + 1, right, y + RULE + 2, ScreenPainter.WHITE);
  }

  private void renderTrade(GuiGraphics graphics, double mouseX, double mouseY) {
    graphics.fill(LEFT, TRADE_Y, right, TRADE_Y + ROW_HEIGHT, ScreenPainter.ROW);
    Player player = Minecraft.getInstance().player;
    int level = player == null ? 0 : player.experienceLevel;
    painter.text(
        graphics,
        Component.translatable(XpKeys.YOUR_LEVEL, level),
        new Box(TEXT_X, TRADE_Y + TEXT_TOP, take().x() - TEXT_X - BUTTON_GAP, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    Component name = Component.translatable(XpKeys.TANK).withStyle(ChatFormatting.BOLD);
    int nameX =
        take().right()
            + BUTTON_GAP
            + (NAME_WIDTH - Math.min(NAME_WIDTH, painter.width(name, ScreenPainter.FULL_SCALE)))
                / 2;
    painter.text(
        graphics,
        name,
        new Box(nameX, TRADE_Y + TEXT_TOP, send().x() - nameX, LINE),
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

  private void renderMode(GuiGraphics graphics, XpMode mode) {
    int top = FIRST_MODE + mode.ordinal() * MODE_HEIGHT;
    if (mode.ordinal() % 2 == 0) {
      graphics.fill(LEFT, top, right, top + MODE_HEIGHT, ScreenPainter.ROW);
    }
    boolean current = mode == xp().mode();
    ScreenPainter.sunken(
        graphics, MARK_X, top + MARK_TOP, MARK_SIZE, MARK_SIZE, ScreenPainter.WHITE);
    if (current) {
      graphics.fill(
          MARK_X + MARK_DOT,
          top + MARK_TOP + MARK_DOT,
          MARK_X + MARK_SIZE - MARK_DOT,
          top + MARK_TOP + MARK_SIZE - MARK_DOT,
          ScreenPainter.GREEN);
    }
    Component name = Component.translatable(XpKeys.mode(mode));
    painter.text(
        graphics,
        current ? name.copy().withStyle(ChatFormatting.BOLD) : name,
        new Box(NAME_X, top + NAME_TOP, right - NAME_X - NAME_INSET, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    painter.text(
        graphics,
        Component.translatable(XpKeys.modeDetail(mode)),
        new Box(NAME_X, top + DETAIL_TOP, right - NAME_X - NAME_INSET, DETAIL_LINE),
        ScreenPainter.DETAIL,
        ScreenPainter.DETAIL_SCALE);
  }

  private void renderExcess(GuiGraphics graphics) {
    graphics.fill(LEFT, EXCESS_Y, right, EXCESS_Y + ROW_HEIGHT, ScreenPainter.ROW);
    painter.text(
        graphics,
        Component.translatable(XpKeys.DELETE_EXCESS),
        new Box(TEXT_X, EXCESS_Y + TEXT_TOP, toggleX() - TEXT_X - BUTTON_GAP, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    ScreenToggle.draw(
        graphics, painter, toggleX(), EXCESS_Y + TOGGLE_TOP, xp().deleteExcess(), storing());
    if (!storing()) {
      graphics.fill(LEFT, EXCESS_Y, right, EXCESS_Y + ROW_HEIGHT, ScreenToggle.DISABLED);
    }
  }

  private boolean storing() {
    return xp().mode() == XpMode.STORE;
  }

  /** Why − does nothing now, or null. */
  private String takeReason() {
    if (!xp().active()) {
      return XpKeys.NOT_ACTIVE;
    }
    return xp().tank().isEmpty(xp().rate()) ? XpKeys.TANK_EMPTY : null;
  }

  /** Why + does nothing now, or null. */
  private String sendReason() {
    if (!xp().active()) {
      return XpKeys.NOT_ACTIVE;
    }
    if (xp().tank().isFull(xp().rate())) {
      return XpKeys.TANK_FULL;
    }
    Player player = Minecraft.getInstance().player;
    boolean none =
        player == null
            || PlayerXp.of(player.experienceLevel, player.experienceProgress).toLevelDown() == 0;
    return none ? XpKeys.NO_XP : null;
  }

  /** The button under the point, as the server numbers them. */
  OptionalInt buttonAt(double x, double y) {
    if (take().contains((int) x, (int) y)) {
      return pressed(takeReason() == null, XpMenuPart.TAKE_BUTTON);
    }
    if (send().contains((int) x, (int) y)) {
      return pressed(sendReason() == null, XpMenuPart.SEND_BUTTON);
    }
    if (ScreenToggle.contains(toggleX(), EXCESS_Y + TOGGLE_TOP, x, y)) {
      return pressed(storing(), XpMenuPart.EXCESS_BUTTON);
    }
    for (XpMode mode : XpMode.values()) {
      int top = FIRST_MODE + mode.ordinal() * MODE_HEIGHT;
      if (x >= LEFT && x < right && y >= top && y < top + MODE_HEIGHT) {
        return pressed(mode != xp().mode(), XpMenuPart.FIRST_MODE_BUTTON + mode.ordinal());
      }
    }
    return OptionalInt.empty();
  }

  private static OptionalInt pressed(boolean open, int button) {
    return open ? OptionalInt.of(button) : OptionalInt.empty();
  }

  /** What a button does and, when it does nothing now, why. */
  boolean renderDetail(GuiGraphics graphics, Font font, int mouseX, int mouseY, int left, int top) {
    int x = mouseX - left;
    int y = mouseY - top;
    if (take().contains(x, y)) {
      return tooltip(graphics, font, XpKeys.TAKE_DETAIL, takeReason(), mouseX, mouseY);
    }
    if (send().contains(x, y)) {
      return tooltip(graphics, font, XpKeys.SEND_DETAIL, sendReason(), mouseX, mouseY);
    }
    if (x >= LEFT && x < right && y >= EXCESS_Y && y < EXCESS_Y + ROW_HEIGHT) {
      String reason = storing() ? null : XpKeys.ONLY_STORE;
      return tooltip(graphics, font, XpKeys.DELETE_EXCESS_DETAIL, reason, mouseX, mouseY);
    }
    return false;
  }

  private static boolean tooltip(
      GuiGraphics graphics, Font font, String what, String reason, int x, int y) {
    Component detail = Component.translatable(what);
    List<Component> lines =
        reason == null
            ? List.of(detail)
            : List.of(detail, Component.translatable(reason).withStyle(ChatFormatting.GRAY));
    graphics.renderComponentTooltip(font, lines, x, y);
    return true;
  }

  private Box take() {
    int x = send().x() - BUTTON_GAP - NAME_WIDTH - BUTTON_GAP - BUTTON;
    return new Box(x, TRADE_Y + BUTTON_GAP, BUTTON, BUTTON);
  }

  private Box send() {
    return new Box(right - BUTTON_GAP - BUTTON, TRADE_Y + BUTTON_GAP, BUTTON, BUTTON);
  }

  private int toggleX() {
    return right - TOGGLE_GAP - ScreenToggle.WIDTH;
  }
}
