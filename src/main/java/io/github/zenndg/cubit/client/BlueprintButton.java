package io.github.zenndg.cubit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/** A button drawn on the board: a filled box with a white frame, gray when off (1524, 1988). */
final class BlueprintButton extends AbstractButton {
  private static final int HOVER = 0x30FFFFFF;
  private static final int OFF_VEIL = 0xA0506080;
  private static final int TEXT_OFFSET = 2;

  private static final int PADDING = 2;
  private static final int LOCK_ROOM = 8;
  private static final int LOCK_TOP = 3;

  private final Runnable action;
  private final int fill;
  private boolean locked;
  private Drawing drawing;

  BlueprintButton(
      int x, int y, int width, int height, Component message, int fill, Runnable action) {
    super(x, y, width, height, message);
    this.fill = fill;
    this.action = action;
  }

  /** What a button shows in place of its text, from its corner. */
  @FunctionalInterface
  interface Drawing {
    void draw(GuiGraphics graphics, int x, int y);
  }

  /** The text stays for the narration. */
  void drawInstead(Drawing shown) {
    drawing = shown;
  }

  /** Off with a padlock, as a running plan leaves it (answer 2015). */
  void lock(boolean now) {
    locked = now;
  }

  @Override
  public void onPress() {
    action.run();
  }

  @Override
  protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    graphics.fill(getX(), getY(), getX() + width, getY() + height, fill);
    if (!active) {
      graphics.fill(getX(), getY(), getX() + width, getY() + height, OFF_VEIL);
    } else if (isHoveredOrFocused()) {
      graphics.fill(getX(), getY(), getX() + width, getY() + height, HOVER);
    }
    graphics.renderOutline(getX(), getY(), width, height, BlueprintPainter.LINE);
    if (drawing != null) {
      drawing.draw(graphics, getX(), getY());
      return;
    }
    renderText(graphics);
  }

  private void renderText(GuiGraphics graphics) {
    int room = locked ? width - LOCK_ROOM : width;
    BlueprintPainter.fitted(
        graphics,
        Minecraft.getInstance().font,
        getMessage(),
        getX() + room / 2,
        getY() + TEXT_OFFSET,
        room - PADDING * 2,
        active ? BlueprintPainter.TEXT : BlueprintPainter.IDLE);
    if (locked) {
      BlueprintPainter.lockBadge(graphics, getX() + room, getY() + LOCK_TOP);
    }
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput output) {
    defaultButtonNarrationText(output);
  }
}
