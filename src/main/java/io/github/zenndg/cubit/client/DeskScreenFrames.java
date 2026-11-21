package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The slot frames of the desk screen: the inventories of its tier and the one of the player. */
final class DeskScreenFrames {
  private static final int PLAYER_X = 47;
  private static final int ROWS = 3;
  private static final int COLUMNS = 9;
  private static final int HOTBAR_GAP = 58;

  private DeskScreenFrames() {}

  static void player(GuiGraphics graphics, int left, int top) {
    int firstRow = top + EngineeringDeskMenu.PLAYER_INVENTORY_Y - 1;
    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        BlueprintPainter.slot(
            graphics,
            left + PLAYER_X + column * ScreenPainter.SLOT_SIZE,
            firstRow + row * ScreenPainter.SLOT_SIZE);
      }
    }
    for (int column = 0; column < COLUMNS; column++) {
      BlueprintPainter.slot(
          graphics, left + PLAYER_X + column * ScreenPainter.SLOT_SIZE, firstRow + HOTBAR_GAP);
    }
  }

  /** The input on the left and the output on the right, as on the desk (answer 1473). */
  static void storage(GuiGraphics graphics, int left, int top, int slots) {
    for (int index = 0; index < slots; index++) {
      int column = index % EngineeringDeskMenu.STORAGE_COLUMNS;
      int row = index / EngineeringDeskMenu.STORAGE_COLUMNS;
      int y = top + EngineeringDeskMenu.STORAGE_Y - 1 + row * ScreenPainter.SLOT_SIZE;
      int offset = column * ScreenPainter.SLOT_SIZE - 1;
      BlueprintPainter.slot(graphics, left + EngineeringDeskMenu.INPUT_X + offset, y);
      BlueprintPainter.slot(graphics, left + EngineeringDeskMenu.OUTPUT_X + offset, y);
    }
  }

  /** Under the tallest storage, six rows, and over the inventory of the player. */
  private static final int HINT_Y = 144;

  private static final int HINT_GAP = 10;
  private static final int ARROW_Y = 66;
  private static final int ARROW_LEFT = 118;
  private static final int ARROW_WIDTH = 20;
  private static final int ARROW_LABEL_GAP = 4;

  /** The dashed arrow from the input to the output, with the desk between them (answer 1473). */
  static void storageArrow(GuiGraphics graphics, int left, int top) {
    BlueprintPainter.arrow(graphics, left + ARROW_LEFT, top + ARROW_Y, ARROW_WIDTH);
  }

  static void storageLabels(GuiGraphics graphics, Font font, int y) {
    graphics.drawString(
        font,
        Component.translatable(DeskScreenKeys.INPUT),
        EngineeringDeskMenu.INPUT_X,
        y,
        BlueprintPainter.TEXT,
        false);
    graphics.drawString(
        font,
        Component.translatable(DeskScreenKeys.OUTPUT),
        EngineeringDeskMenu.OUTPUT_X,
        y,
        BlueprintPainter.TEXT,
        false);
    graphics.drawCenteredString(
        font,
        Component.translatable(DeskScreenKeys.STORAGE_ARROW),
        ARROW_LEFT + ARROW_WIDTH / 2,
        ARROW_Y + ARROW_LABEL_GAP,
        BlueprintPainter.TEXT_SOFT);
    storageHints(graphics, font);
  }

  /** What the two inventories do, under the tallest storage (answer 1473). */
  private static void storageHints(GuiGraphics graphics, Font font) {
    int x = EngineeringDeskMenu.INPUT_X;
    graphics.drawString(
        font,
        Component.translatable(DeskScreenKeys.STORAGE_FEEDS),
        x,
        HINT_Y,
        BlueprintPainter.TEXT_SOFT,
        false);
    graphics.drawString(
        font,
        Component.translatable(DeskScreenKeys.STORAGE_FINISHED),
        x,
        HINT_Y + HINT_GAP,
        BlueprintPainter.TEXT_SOFT,
        false);
  }
}
