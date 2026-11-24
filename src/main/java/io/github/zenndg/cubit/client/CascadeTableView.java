package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.CascadeRecipes;
import io.github.zenndg.cubit.domain.desk.CascadeCost;
import io.github.zenndg.cubit.domain.desk.CascadeLine;
import io.github.zenndg.cubit.domain.desk.CascadeTaskStatus;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * The table of a plan, over the whole width of the board: the order, the item, what it takes, what
 * it waits for and its status (answers 2012, 2024 and 2044).
 */
final class CascadeTableView {
  private static final int X = 8;
  private static final int Y = 21;
  private static final int WIDTH = 240;
  private static final int ROWS = 6;
  private static final int ROW = 12;
  private static final int FIRST_ROW = Y + ROW;
  private static final int HEIGHT = ROW * (ROWS + 1) + 1;
  private static final int ORDER_X = X + 4;
  private static final int ITEM_X = X + 13;
  private static final int NAME_X = ITEM_X + 12;
  private static final int RESERVED_X = X + 116;
  private static final int NEEDS_X = X + 156;
  private static final int STATUS_X = X + 192;
  private static final int STATUS_ROOM = 46;
  private static final int GAP = 3;
  private static final int TEXT_Y = 3;
  private static final int PROGRESS_TEXT_Y = 1;
  private static final int PROGRESS_Y = 8;
  private static final int PROGRESS_HEIGHT = 2;
  private static final int COST_Y = 110;
  private static final int SCROLL_WIDTH = 2;
  private static final float ICON_SCALE = 10f / 16f;
  private static final int BACK = 0xE016306F;
  private static final int STRIPE = 0x401D3C8F;
  private static final int CURRENT = 0x304FC3F7;
  private static final int PROGRESS_BACK = 0xFF0C1C48;
  private static final String NO_NEEDS = "-";
  private static final String COMMA = ", ";

  private final CascadeReading reading;
  private final ScrollRows scroll = new ScrollRows(ROWS);

  CascadeTableView(CascadeReading reading) {
    this.reading = reading;
  }

  private List<CascadeLine> lines() {
    return reading.table().lines();
  }

  void render(GuiGraphics graphics, Font font) {
    graphics.fill(X, Y, X + WIDTH, Y + HEIGHT, BACK);
    renderHeader(graphics, font);
    List<CascadeLine> lines = lines();
    int first = scroll.first(lines.size());
    for (int place = first; place < Math.min(lines.size(), first + ROWS); place++) {
      renderLine(graphics, font, place, lines.get(place), FIRST_ROW + (place - first) * ROW);
    }
    renderScrollBar(graphics, first, lines.size());
    graphics.renderOutline(X, Y, WIDTH, HEIGHT, BlueprintPainter.LINE);
    renderCost(graphics, font);
  }

  private static void renderHeader(GuiGraphics graphics, Font font) {
    header(graphics, font, DeskScreenKeys.CASCADE_ORDER_COLUMN, ORDER_X, ITEM_X);
    header(graphics, font, DeskScreenKeys.CASCADE_ITEM_COLUMN, ITEM_X, RESERVED_X);
    header(graphics, font, DeskScreenKeys.CASCADE_RESERVED_COLUMN, RESERVED_X, NEEDS_X);
    header(graphics, font, DeskScreenKeys.CASCADE_NEEDS_COLUMN, NEEDS_X, STATUS_X);
    header(graphics, font, DeskScreenKeys.CASCADE_STATUS_COLUMN, STATUS_X, X + WIDTH);
    graphics.fill(X + 1, FIRST_ROW - 1, X + WIDTH - 1, FIRST_ROW, BlueprintPainter.LINE);
  }

  private static void header(GuiGraphics graphics, Font font, String key, int x, int end) {
    Component text = Component.translatable(key);
    cell(graphics, font, text, x, Y + TEXT_Y, end, BlueprintPainter.CRAFTING);
  }

  private static void cell(
      GuiGraphics graphics, Font font, Component text, int x, int y, int end, int color) {
    BlueprintPainter.Look look =
        new BlueprintPainter.Look(color, end - x - GAP, BlueprintPainter.SMALL_SCALE);
    BlueprintPainter.fromLeft(graphics, font, text, x, y, look);
  }

  private static void renderLine(
      GuiGraphics graphics, Font font, int place, CascadeLine line, int y) {
    if (place % 2 != 0) {
      graphics.fill(X + 1, y, X + WIDTH - 1, y + ROW, STRIPE);
    }
    if (line.status() == CascadeTaskStatus.CRAFTING) {
      graphics.fill(X + 1, y, X + WIDTH - 1, y + ROW, CURRENT);
    }
    int color =
        line.status() == CascadeTaskStatus.DONE
            ? BlueprintPainter.TEXT_SOFT
            : BlueprintPainter.TEXT;
    int textY = y + TEXT_Y;
    cell(graphics, font, number(place + 1), ORDER_X, textY, ITEM_X, color);
    if (!line.item().isEmpty()) {
      renderIcon(graphics, line.item(), y);
      Component name = CascadeMaterials.amount(line.made(), line.item());
      cell(graphics, font, name, NAME_X, textY, RESERVED_X, color);
    }
    cell(graphics, font, number(line.reserved()), RESERVED_X, textY, NEEDS_X, color);
    cell(graphics, font, needs(line), NEEDS_X, textY, STATUS_X, color);
    renderStatus(graphics, font, line, y);
  }

  private static Component number(int value) {
    return Component.literal(String.valueOf(value));
  }

  private static Component needs(CascadeLine line) {
    String numbers =
        line.needs().stream()
            .sorted()
            .map(need -> String.valueOf(need + 1))
            .collect(Collectors.joining(COMMA));
    return Component.literal(numbers.isEmpty() ? NO_NEEDS : numbers);
  }

  private static void renderIcon(GuiGraphics graphics, String item, int y) {
    graphics.pose().pushPose();
    graphics.pose().translate(ITEM_X, y + 1, 0);
    graphics.pose().scale(ICON_SCALE, ICON_SCALE, 1);
    graphics.renderFakeItem(new ItemStack(CascadeRecipes.itemOf(item)), 0, 0);
    graphics.pose().popPose();
  }

  private static void renderStatus(GuiGraphics graphics, Font font, CascadeLine line, int y) {
    String key = DeskScreenKeys.cascadeStatus(line.status());
    int end = X + WIDTH;
    switch (line.status()) {
      case WAITING -> {
        Component text = Component.translatable(key);
        cell(graphics, font, text, STATUS_X, y + TEXT_Y, end, BlueprintPainter.TEXT_SOFT);
      }
      case CRAFTING -> {
        Component text = Component.translatable(key, line.done(), line.total());
        int color = BlueprintPainter.CRAFTING;
        cell(graphics, font, text, STATUS_X, y + PROGRESS_TEXT_Y, end, color);
        renderProgress(graphics, line, y + PROGRESS_Y);
      }
      case DONE -> {
        Component text = Component.translatable(key);
        cell(graphics, font, text, STATUS_X, y + TEXT_Y, end, BlueprintPainter.DONE);
        int width = Math.round(font.width(text) * BlueprintPainter.SMALL_SCALE);
        CheckMark.render(graphics, STATUS_X + width + 2, y + TEXT_Y, BlueprintPainter.DONE);
      }
    }
  }

  private static void renderProgress(GuiGraphics graphics, CascadeLine line, int y) {
    int filled = STATUS_ROOM * line.done() / line.total();
    int bottom = y + PROGRESS_HEIGHT;
    graphics.fill(STATUS_X, y, STATUS_X + STATUS_ROOM, bottom, PROGRESS_BACK);
    graphics.fill(STATUS_X, y, STATUS_X + filled, bottom, BlueprintPainter.CRAFTING);
  }

  /** Only a pack has a plan with more lines than the table (answer 2031). */
  private void renderScrollBar(GuiGraphics graphics, int first, int total) {
    if (!scroll.overflows(total)) {
      return;
    }
    int height = ROW * ROWS;
    int x = X + WIDTH - 1 - SCROLL_WIDTH;
    int top = FIRST_ROW + height * first / total;
    int bottom = FIRST_ROW + height * (first + ROWS) / total;
    graphics.fill(x, top, x + SCROLL_WIDTH, bottom, BlueprintPainter.LINE_SOFT);
  }

  private void renderCost(GuiGraphics graphics, Font font) {
    if (lines().isEmpty()) {
      return;
    }
    CascadeCost cost = reading.cost();
    Component text =
        Component.translatable(
            DeskScreenKeys.CASCADE_COST, EnergyFormat.rate(cost.energy()), cost.seconds());
    graphics.drawString(font, text, X, COST_Y, BlueprintPainter.TEXT, false);
  }

  private boolean isOver(double x, double y) {
    return x >= X && x < X + WIDTH && y >= FIRST_ROW && y < FIRST_ROW + ROW * ROWS;
  }

  /** The name of the item, or what the task takes over the reserved column (answer 2024). */
  List<Component> hint(double x, double y) {
    List<CascadeLine> lines = lines();
    int place = scroll.first(lines.size()) + (int) Math.floor((y - FIRST_ROW) / ROW);
    if (!isOver(x, y) || place >= lines.size() || lines.get(place).item().isEmpty()) {
      return List.of();
    }
    CascadeLine line = lines.get(place);
    if (x >= RESERVED_X && x < NEEDS_X) {
      return line.takes().entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .map(take -> CascadeMaterials.amount(take.getValue(), take.getKey()))
          .toList();
    }
    return x >= ITEM_X && x < RESERVED_X
        ? List.of(CascadeMaterials.amount(line.made(), line.item()))
        : List.of();
  }

  /** True when the wheel was over the table. */
  boolean scroll(double x, double y, double delta) {
    boolean over = isOver(x, y);
    if (over) {
      scroll.scroll(delta, lines().size());
    }
    return over;
  }
}
