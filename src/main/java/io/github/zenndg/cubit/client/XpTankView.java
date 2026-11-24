package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.xp.XpRate;
import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.menu.XpKeys;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;

/** The glass with the fluid, its marks and the line with the amount (answer 1830). */
final class XpTankView {
  private static final int X = 12;
  private static final int Y = 33;
  private static final int WIDTH = 92;
  private static final int HEIGHT = 60;
  private static final int LINE_Y = 98;
  private static final int LINE_HEIGHT = 9;
  private static final int MARKS = 4;
  private static final int LONG_MARK = 5;
  private static final int SHORT_MARK = 3;
  private static final int OUT_MARK = 4;
  private static final int LABEL_GAP = 6;
  private static final int LABEL_TOP = 2;
  private static final int LABEL_HEIGHT = 7;
  private static final int GLASS = 0xFF2A2A2A;
  private static final int MARK = 0x99FFFFFF;
  private static final int SURFACE = 0xFFD2FE86;
  private static final int TILE = 16;
  private static final String GROUPED = "%,d";

  private final ScreenPainter painter;
  private final int right;

  XpTankView(ScreenPainter painter, int right) {
    this.painter = painter;
    this.right = right;
  }

  void render(GuiGraphics graphics, XpTank tank, XpRate rate) {
    ScreenPainter.sunken(graphics, X, Y, WIDTH, HEIGHT, GLASS);
    renderFluid(graphics, tank);
    renderMarks(graphics, tank.capacity());
    painter.text(
        graphics,
        amount(tank, rate).copy().withStyle(ChatFormatting.BOLD),
        new Box(X, LINE_Y, right - X, LINE_HEIGHT),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
  }

  /** At least a pixel while anything is left, as the bars, and never over the glass. */
  private static void renderFluid(GuiGraphics graphics, XpTank tank) {
    int inner = HEIGHT - 2;
    if (tank.fluid() == 0 || tank.capacity() == 0) {
      return;
    }
    long wanted = ((long) tank.fluid() * inner + tank.capacity() - 1) / tank.capacity();
    int filled = (int) Math.min(inner, wanted);
    int left = X + 1;
    int bottom = Y + HEIGHT - 1;
    TextureAtlasSprite sprite =
        Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(XpFluidLook.STILL);
    graphics.flush();
    ScissorBox.run(
        graphics, left, bottom - filled, left + WIDTH - 2, bottom, () -> tile(graphics, sprite));
    graphics.fill(left, bottom - filled, left + WIDTH - 2, bottom - filled + 1, SURFACE);
  }

  private static void tile(GuiGraphics graphics, TextureAtlasSprite sprite) {
    for (int y = Y + HEIGHT - 1 - TILE; y > Y - TILE; y -= TILE) {
      for (int x = X + 1; x < X + WIDTH; x += TILE) {
        graphics.blit(x, y, 0, TILE, TILE, sprite);
      }
    }
  }

  private void renderMarks(GuiGraphics graphics, int capacity) {
    for (int mark = 0; mark <= MARKS; mark++) {
      int y = Y + 1 + Math.round((HEIGHT - SHORT_MARK) * (float) mark / MARKS);
      int inside = mark * 2 == MARKS ? LONG_MARK : SHORT_MARK;
      graphics.fill(X + WIDTH, y, X + WIDTH + OUT_MARK, y + 1, ScreenPainter.SHADE);
      if (mark > 0 && mark < MARKS) {
        graphics.fill(X + 1, y, X + 1 + inside, y + 1, MARK);
        graphics.fill(X + WIDTH - 1 - inside, y, X + WIDTH - 1, y + 1, MARK);
      }
      int shown = (int) ((long) capacity * (MARKS - mark) / MARKS);
      int labelX = X + WIDTH + LABEL_GAP;
      painter.text(
          graphics,
          Component.translatable(XpKeys.MARK, grouped(shown)),
          new Box(labelX, y - LABEL_TOP, right - labelX, LABEL_HEIGHT),
          ScreenPainter.DETAIL,
          ScreenPainter.DETAIL_SCALE);
    }
  }

  private static Component amount(XpTank tank, XpRate rate) {
    int points = tank.points(rate);
    String key = points == 1 ? XpKeys.AMOUNT_ONE : XpKeys.AMOUNT;
    return Component.translatable(
        key, grouped(tank.fluid()), grouped(tank.capacity()), grouped(points));
  }

  /** Always with a comma, whatever the language, as the energy of the screens. */
  private static String grouped(int number) {
    return String.format(Locale.ROOT, GROUPED, number);
  }
}
