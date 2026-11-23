package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The energy of the desk, with its slot, in the Project and Cascade tabs (answer 2013). */
final class DeskEnergyBar {
  private static final int MARGIN = 8;
  private static final int LABEL_Y = 128;
  private static final int BAR_Y = 139;
  private static final int BAR_WIDTH = 204;
  private static final int BAR_HEIGHT = 8;

  private DeskEnergyBar() {}

  static void render(GuiGraphics graphics, EngineeringDeskMenu menu, int left, int top) {
    BlueprintPainter.bar(
        graphics,
        new ScreenPainter.Box(left + MARGIN, top + BAR_Y, BAR_WIDTH, BAR_HEIGHT),
        menu.stock().amount(),
        Math.max(1, menu.capacity().amount()),
        BlueprintPainter.ENERGY);
    BlueprintPainter.slot(
        graphics,
        left + EngineeringDeskMenu.ENERGY_SLOT_X - 1,
        top + EngineeringDeskMenu.ENERGY_SLOT_Y - 1);
  }

  static void renderLabels(GuiGraphics graphics, Font font, EngineeringDeskMenu menu) {
    Component label = Component.translatable(DeskScreenKeys.STORED_ENERGY);
    graphics.drawString(font, label, MARGIN, LABEL_Y, BlueprintPainter.TEXT, false);
    Component amount =
        Component.translatable(
            ScreenKeys.ENERGY_AMOUNT,
            EnergyFormat.compact(menu.stock()),
            EnergyFormat.compact(menu.capacity()));
    int amountX = MARGIN + BAR_WIDTH - font.width(amount);
    graphics.drawString(font, amount, amountX, LABEL_Y, BlueprintPainter.TEXT_SOFT, false);
  }
}
