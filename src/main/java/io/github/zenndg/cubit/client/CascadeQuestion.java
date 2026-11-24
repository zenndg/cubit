package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The question before a plan starts, over the dimmed board: the item, the quantity, the time and
 * that it cannot be canceled (answer 2023).
 */
final class CascadeQuestion {
  private static final int X = 44;
  private static final int Y = 56;
  private static final int WIDTH = 168;
  private static final int HEIGHT = 74;
  private static final int INSET = 2;
  private static final int MARGIN = 8;
  private static final int ASK_Y = Y + 9;
  private static final int ITEM_Y = Y + 24;
  private static final int WARNING_Y = Y + 38;
  private static final int BUTTON_Y = Y + 54;
  private static final int YES_X = X + 18;
  private static final int NO_X = X + 90;
  private static final int BUTTON_WIDTH = 60;
  private static final int BUTTON_HEIGHT = 12;
  private static final int DIM = 0xA0000820;
  private static final int SHADOW = 0xFF050B20;

  /** Over the items and their counts, and under the tooltips. */
  private static final int DEPTH = 350;

  private final CascadeReading reading;
  private final Runnable onYes;
  private BlueprintButton yes;
  private BlueprintButton no;
  private boolean open;

  CascadeQuestion(CascadeReading reading, Runnable onYes) {
    this.reading = reading;
    this.onYes = onYes;
  }

  void init(int left, int top) {
    yes =
        button(
            left + YES_X,
            top,
            DeskScreenKeys.CASCADE_YES,
            BlueprintPainter.PRIMARY,
            () -> {
              open = false;
              onYes.run();
            });
    no =
        button(
            left + NO_X, top, DeskScreenKeys.CASCADE_NO, BlueprintPainter.CELL, () -> open = false);
  }

  private static BlueprintButton button(int x, int top, String key, int fill, Runnable action) {
    return new BlueprintButton(
        x, top + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable(key), fill, action);
  }

  boolean isOpen() {
    return open;
  }

  void ask(boolean now) {
    open = now;
  }

  void render(GuiGraphics graphics, Font font, int left, int top, int mouseX, int mouseY) {
    graphics.pose().pushPose();
    graphics.pose().translate(0, 0, DEPTH);
    graphics.fill(
        left, top, left + EngineeringDeskMenu.WIDTH, top + EngineeringDeskScreen.HEIGHT, DIM);
    int tabsLeft = left + EngineeringDeskMenu.WIDTH;
    ScreenTabStrip.dim(graphics, tabsLeft, top, DeskTab.TOPS, DIM);
    renderBox(graphics, left + X, top + Y);
    renderTexts(graphics, font, left + X + WIDTH / 2, top);
    yes.render(graphics, mouseX, mouseY, 0);
    no.render(graphics, mouseX, mouseY, 0);
    graphics.pose().popPose();
  }

  private static void renderBox(GuiGraphics graphics, int x, int y) {
    graphics.fill(x + INSET, y + INSET, x + WIDTH + INSET, y + HEIGHT + INSET, SHADOW);
    graphics.fill(x, y, x + WIDTH, y + HEIGHT, BlueprintPainter.SHEET);
    graphics.renderOutline(x, y, WIDTH, HEIGHT, BlueprintPainter.LINE);
    graphics.renderOutline(
        x + INSET, y + INSET, WIDTH - INSET * 2, HEIGHT - INSET * 2, BlueprintPainter.LINE_SOFT);
  }

  private void renderTexts(GuiGraphics graphics, Font font, int x, int top) {
    Component item =
        reading
            .chosen()
            .<Component>map(chosen ->
                Component.translatable(
                    DeskScreenKeys.CASCADE_ASKED,
                    reading.quantity(),
                    chosen.getDescription(),
                    reading.cost().seconds()))
            .orElse(Component.empty());
    Component ask = Component.translatable(DeskScreenKeys.CASCADE_ASK);
    Component warning = Component.translatable(DeskScreenKeys.CASCADE_NO_CANCEL);
    text(graphics, font, ask, x, top + ASK_Y, BlueprintPainter.TEXT);
    text(graphics, font, item, x, top + ITEM_Y, BlueprintPainter.CRAFTING);
    text(graphics, font, warning, x, top + WARNING_Y, BlueprintPainter.WARNING);
  }

  private static void text(
      GuiGraphics graphics, Font font, Component text, int x, int y, int color) {
    BlueprintPainter.fitted(graphics, font, text, x, y, WIDTH - MARGIN * 2, color);
  }

  /** While open, only its buttons take a click. */
  void click(double mouseX, double mouseY, int button) {
    if (!yes.mouseClicked(mouseX, mouseY, button)) {
      no.mouseClicked(mouseX, mouseY, button);
    }
  }
}
