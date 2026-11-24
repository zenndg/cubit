package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.menu.RobotMenu;
import io.github.zenndg.cubit.menu.XpKeys;
import io.github.zenndg.cubit.menu.XpMenuPart;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * The XP tab of the Cubit: the tank, the trade with the owner, the modes and the switch of the
 * excess, which only the store uses (answers 1830, 2076 and 2082).
 */
final class XpPanel {
  static final Box GLASS = new Box(12, 33, 92, 60);
  static final int TANK_TITLE_Y = 18;
  private static final int LEFT = 7;
  private static final int TRADE_Y = 108;
  private static final int TRADE_HEIGHT = 18;
  private static final int MODES_TITLE_Y = 131;
  private static final int FIRST_MODE = 144;
  private static final int EXCESS_Y = 208;

  private final ScreenPainter painter;
  private final RobotMenu menu;
  private final int right;
  private final XpTankView tank;
  private final XpTradeRow trade;
  private final XpModeRows modes;
  private final XpExcessRow excess;

  XpPanel(ScreenPainter painter, RobotMenu menu, int right) {
    this.painter = painter;
    this.menu = menu;
    this.right = right;
    this.tank = new XpTankView(painter, GLASS, right);
    Box row = new Box(LEFT, TRADE_Y, right - LEFT, TRADE_HEIGHT);
    this.trade = new XpTradeRow(painter, row, this::reading);
    this.modes = new XpModeRows(painter, modeRows(), XpModeRows.WIDE, FIRST_MODE, right);
    this.excess = new XpExcessRow(painter, menu.xp(), EXCESS_Y, right);
  }

  static List<XpModeRows.Row> modeRows() {
    return Arrays.stream(XpMode.values())
        .map(mode -> new XpModeRows.Row(XpKeys.mode(mode), XpKeys.modeDetail(mode)))
        .toList();
  }

  private XpMenuPart xp() {
    return menu.xp();
  }

  private XpTradeRow.Reading reading() {
    String blocked = xp().active() ? null : XpKeys.NOT_ACTIVE;
    return new XpTradeRow.Reading(xp().tank(), xp().rate(), blocked, null);
  }

  /** Drawn in the coordinates of the window, with the mouse in the same ones. */
  void render(GuiGraphics graphics, double mouseX, double mouseY) {
    XpHeading.render(graphics, painter, XpKeys.TANK, TANK_TITLE_Y, right);
    tank.render(graphics, xp().tank(), xp().rate());
    trade.render(graphics, mouseX, mouseY);
    XpHeading.render(graphics, painter, XpKeys.MODES, MODES_TITLE_Y, right);
    modes.render(graphics, xp().mode().ordinal());
    excess.render(graphics);
  }

  /** The button under the point, as the server numbers them. */
  OptionalInt buttonAt(double x, double y) {
    OptionalInt traded = trade.buttonAt(x, y, XpMenuPart.TAKE_BUTTON, XpMenuPart.SEND_BUTTON);
    if (traded.isPresent()) {
      return traded;
    }
    OptionalInt toggled = excess.buttonAt(x, y);
    return toggled.isPresent() ? toggled : modeAt(modes, xp(), x, y);
  }

  /** The button of the mode under the point, unless it is already the one. */
  static OptionalInt modeAt(XpModeRows modes, XpMenuPart xp, double x, double y) {
    OptionalInt mode = modes.at(x, y);
    return mode.isPresent() && mode.getAsInt() != xp.mode().ordinal()
        ? OptionalInt.of(XpMenuPart.FIRST_MODE_BUTTON + mode.getAsInt())
        : OptionalInt.empty();
  }

  /** What a button does and, when it does nothing now, why. */
  boolean renderDetail(GuiGraphics graphics, Font font, int mouseX, int mouseY, int left, int top) {
    return trade.renderDetail(graphics, font, mouseX, mouseY, left, top)
        || excess.renderDetail(graphics, font, mouseX, mouseY, left, top);
  }
}
