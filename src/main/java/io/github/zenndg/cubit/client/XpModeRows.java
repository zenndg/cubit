package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The modes of an XP tab, one below the other, each with what it does (answers 1827 and 2100). */
final class XpModeRows {
  /** The rows of the Cubit, with more room than the other ones. */
  static final Size WIDE = new Size(20, 3, 12);

  static final Size NARROW = new Size(18, 2, 10);
  static final Size TIGHT = new Size(17, 2, 10);

  private static final int LEFT = 7;
  private static final int LINE = 9;
  private static final int DETAIL_LINE = 7;
  private static final int MARK_X = 12;
  private static final int MARK_SIZE = 10;
  private static final int MARK_DOT = 3;
  private static final int NAME_X = 30;
  private static final int NAME_INSET = 3;

  private final ScreenPainter painter;
  private final List<Row> rows;
  private final Size size;
  private final int top;
  private final int right;

  XpModeRows(ScreenPainter painter, List<Row> rows, Size size, int top, int right) {
    this.painter = painter;
    this.rows = rows;
    this.size = size;
    this.top = top;
    this.right = right;
  }

  /** The keys of the name and of what the mode does. */
  record Row(String name, String detail) {}

  /** The height of a row, and where its two lines start. */
  record Size(int height, int nameTop, int detailTop) {}

  void render(GuiGraphics graphics, int current) {
    for (int index = 0; index < rows.size(); index++) {
      renderRow(graphics, index, index == current);
    }
  }

  private void renderRow(GuiGraphics graphics, int index, boolean current) {
    int y = top + index * size.height();
    if (index % 2 == 0) {
      graphics.fill(LEFT, y, right, y + size.height(), ScreenPainter.ROW);
    }
    int mark = y + (size.height() - MARK_SIZE) / 2;
    ScreenPainter.sunken(graphics, MARK_X, mark, MARK_SIZE, MARK_SIZE, ScreenPainter.WHITE);
    if (current) {
      graphics.fill(
          MARK_X + MARK_DOT,
          mark + MARK_DOT,
          MARK_X + MARK_SIZE - MARK_DOT,
          mark + MARK_SIZE - MARK_DOT,
          ScreenPainter.GREEN);
    }
    int width = right - NAME_X - NAME_INSET;
    Component name = Component.translatable(rows.get(index).name());
    painter.text(
        graphics,
        current ? name.copy().withStyle(ChatFormatting.BOLD) : name,
        new Box(NAME_X, y + size.nameTop(), width, LINE),
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    painter.text(
        graphics,
        Component.translatable(rows.get(index).detail()),
        new Box(NAME_X, y + size.detailTop(), width, DETAIL_LINE),
        ScreenPainter.DETAIL,
        ScreenPainter.DETAIL_SCALE);
  }

  /** The mode under the point, from the top. */
  OptionalInt at(double x, double y) {
    int bottom = top + rows.size() * size.height();
    return x >= LEFT && x < right && y >= top && y < bottom
        ? OptionalInt.of((int) (y - top) / size.height())
        : OptionalInt.empty();
  }
}
