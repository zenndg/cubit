package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.dock.DockXpMode;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.menu.DockXpMenuPart;
import io.github.zenndg.cubit.menu.XpKeys;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The XP tab of the Dock, in the order of the one of the Cubit: the tank, the trade with the owner
 * and the modes, with the energy the XP made in the title (answers 1837, 2109 and 2114).
 */
final class DockXpPanel {
  private static final int LEFT = 7;
  private static final int RIGHT = 169;
  private static final int LINE = 9;
  private static final Box TITLE = new Box(8, 6, 30, LINE);
  private static final Box MADE = new Box(40, 6, 128, LINE);
  private static final Box GLASS = new Box(12, 18, 92, 34);
  private static final Box TRADE = new Box(LEFT, 66, RIGHT - LEFT, 16);
  private static final int MODES_TITLE_Y = 85;
  private static final int FIRST_MODE = 97;

  private final ScreenPainter painter;
  private final DockXpMenuPart xp;
  private final XpTankView tank;
  private final XpTradeRow trade;
  private final XpModeRows modes;

  DockXpPanel(ScreenPainter painter, DockXpMenuPart xp) {
    this.painter = painter;
    this.xp = xp;
    this.tank = new XpTankView(painter, GLASS, RIGHT);
    this.trade = new XpTradeRow(painter, TRADE, this::reading);
    this.modes = new XpModeRows(painter, modeRows(), XpModeRows.TIGHT, FIRST_MODE, RIGHT);
  }

  private static List<XpModeRows.Row> modeRows() {
    return Arrays.stream(DockXpMode.values())
        .map(mode -> new XpModeRows.Row(XpKeys.dockMode(mode), XpKeys.dockModeDetail(mode)))
        .toList();
  }

  /** In Convert all, + says that the XP becomes energy, with no way back (answer 2110). */
  private XpTradeRow.Reading reading() {
    String note = xp.mode() == DockXpMode.CONVERT_ALL ? XpKeys.SEND_CONVERTS : null;
    return new XpTradeRow.Reading(xp.tank(), xp.rate(), null, note);
  }

  /** Drawn in the coordinates of the window, with the mouse in the same ones. */
  void render(GuiGraphics graphics, double mouseX, double mouseY) {
    painter.text(
        graphics,
        Component.translatable(XpKeys.TAB).withStyle(ChatFormatting.BOLD),
        TITLE,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    painter.textRight(
        graphics,
        Component.translatable(XpKeys.ENERGY_MADE, EnergyFormat.compact(xp.energyMade())),
        MADE,
        ScreenPainter.DETAIL,
        ScreenPainter.FULL_SCALE);
    tank.render(graphics, xp.tank(), xp.rate());
    trade.render(graphics, mouseX, mouseY);
    XpHeading.render(graphics, painter, XpKeys.MODES, MODES_TITLE_Y, RIGHT);
    modes.render(graphics, xp.mode().ordinal());
  }

  /** The button under the point, as the server numbers them. */
  OptionalInt buttonAt(double x, double y) {
    OptionalInt traded =
        trade.buttonAt(x, y, DockXpMenuPart.TAKE_BUTTON, DockXpMenuPart.SEND_BUTTON);
    if (traded.isPresent()) {
      return traded;
    }
    OptionalInt mode = modes.at(x, y);
    return mode.isPresent() && mode.getAsInt() != xp.mode().ordinal()
        ? OptionalInt.of(DockXpMenuPart.FIRST_MODE_BUTTON + mode.getAsInt())
        : OptionalInt.empty();
  }

  boolean renderDetail(GuiGraphics graphics, Font font, int mouseX, int mouseY, int left, int top) {
    return trade.renderDetail(graphics, font, mouseX, mouseY, left, top);
  }
}
