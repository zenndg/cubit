package io.github.zenndg.cubit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/** A button drawn on the board: a filled box with a white frame (answer 1524). */
final class BlueprintButton extends AbstractButton {
  private static final int HOVER = 0x30FFFFFF;
  private static final int TEXT_OFFSET = 2;

  private final Runnable action;
  private final int fill;

  BlueprintButton(
      int x, int y, int width, int height, Component message, int fill, Runnable action) {
    super(x, y, width, height, message);
    this.fill = fill;
    this.action = action;
  }

  @Override
  public void onPress() {
    action.run();
  }

  @Override
  protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    graphics.fill(getX(), getY(), getX() + width, getY() + height, fill);
    if (isHoveredOrFocused()) {
      graphics.fill(getX(), getY(), getX() + width, getY() + height, HOVER);
    }
    graphics.renderOutline(getX(), getY(), width, height, BlueprintPainter.LINE);
    graphics.drawCenteredString(
        Minecraft.getInstance().font,
        getMessage(),
        getX() + width / 2,
        getY() + TEXT_OFFSET,
        BlueprintPainter.TEXT);
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput output) {
    defaultButtonNarrationText(output);
  }
}
