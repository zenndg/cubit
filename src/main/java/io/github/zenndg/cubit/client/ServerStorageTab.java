package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.menu.NetworkKeys;
import io.github.zenndg.cubit.menu.NetworkServerMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The Storage tab of the Network Server: the rows of its tier and what the slots take (2397). */
final class ServerStorageTab {
  private static final Box TITLE = new Box(8, 6, 80, NetworkScreen.LINE);
  private static final Box USED = new Box(88, 6, 80, NetworkScreen.LINE);
  private static final int NOTE_GAP = 4;
  private static final int NOTE_HEIGHT = 22;
  private static final int RULE_TOP = 3;
  private static final int BANK_TOP = 12;
  private static final int NOTE_WIDTH = 156;

  private final NetworkServerScreen screen;
  private final NetworkServerMenu menu;

  ServerStorageTab(NetworkServerScreen screen, NetworkServerMenu menu) {
    this.screen = screen;
    this.menu = menu;
  }

  private int rows() {
    return menu.numbers().storageSlots() / ServerSpec.ROW;
  }

  private int noteTop() {
    return NetworkServerMenu.STORAGE_Y + rows() * ScreenPainter.SLOT_SIZE + NOTE_GAP;
  }

  void renderFrames(GuiGraphics graphics, int left, int top) {
    for (int row = 0; row < rows(); row++) {
      int y = top + NetworkServerMenu.STORAGE_Y - 1 + row * ScreenPainter.SLOT_SIZE;
      NetworkScreen.slotRow(graphics, left, y);
    }
    NetworkScreen.row(graphics, left, top + noteTop(), NOTE_HEIGHT);
  }

  void render(GuiGraphics graphics) {
    screen.bold(graphics, Component.translatable(ScreenKeys.DOCK_TAB_STORAGE), TITLE);
    screen.painter.textRight(
        graphics,
        Component.translatable(
            NetworkKeys.USED_SLOTS, menu.usedSlots(), menu.numbers().storageSlots()),
        USED,
        ScreenPainter.DETAIL,
        ScreenPainter.FULL_SCALE);
    Component rule = Component.translatable(NetworkKeys.SLOT_RULE, menu.numbers().slotLimit());
    screen.detail(graphics, rule, note(RULE_TOP), ScreenPainter.DETAIL);
    if (!menu.banks().isZero()) {
      Component charging =
          Component.translatable(NetworkKeys.BANK_CHARGING, EnergyFormat.rate(menu.banks()));
      screen.detail(graphics, charging, note(BANK_TOP), ScreenPainter.STATUS_GREEN);
    } else if (menu.hasBankToCharge()) {
      Component waiting = Component.translatable(NetworkKeys.BANK_WAITING);
      screen.detail(graphics, waiting, note(BANK_TOP), ScreenPainter.STATUS_ORANGE);
    }
  }

  private Box note(int top) {
    int left = NetworkScreen.ROW_LEFT + NetworkScreen.ROW_INSET;
    return new Box(left, noteTop() + top, NOTE_WIDTH, NetworkScreen.DETAIL_LINE);
  }
}
