package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.CascadeRecipes;
import io.github.zenndg.cubit.domain.desk.CascadeNeed;
import io.github.zenndg.cubit.domain.desk.CascadePlan;
import io.github.zenndg.cubit.domain.desk.MaterialLevel;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The first step of the Cascade tab: the base materials in slots, green with enough, yellow with
 * some and red with none, and in blue the ready parts the plan uses (answers 2006, 2045 and 2052).
 */
final class CascadeMaterials {
  private static final int X = 96;
  private static final int Y = 36;
  private static final int WIDTH = 152;
  private static final int TITLE_Y = 23;

  /** The title starts past the bar of the categories (answer 2177). */
  private static final int TITLE_SHIFT = 10;

  private static final int COLUMNS = 8;
  private static final int ROWS = 3;
  private static final int PITCH = 19;
  private static final int SLOT = ScreenPainter.SLOT_SIZE;
  private static final int COUNT_RIGHT = 18;
  private static final int COUNT_BOTTOM = 18;
  private static final int COUNT_ROOM = 16;
  private static final int GREEN = 0xB03FC46A;
  private static final int GREEN_FRAME = 0xFF7CF29A;
  private static final int YELLOW = 0xCCE8B520;
  private static final int YELLOW_FRAME = 0xFFFFE26A;
  private static final int BLUE = 0xB02E9BD6;
  private static final int BLUE_FRAME = 0xFF8ADCFF;
  private static final int RED = 0xB8DD3F45;
  private static final int RED_FRAME = 0xFFFF7A7A;

  private final CascadeReading reading;
  private final ScrollRows scroll = new ScrollRows(ROWS);

  CascadeMaterials(CascadeReading reading) {
    this.reading = reading;
  }

  /** Over the labels of the screen, so from the left and the top of the window. */
  void render(GuiGraphics graphics, Font font) {
    if (reading.plan().isEmpty()) {
      renderHint(graphics, font);
      return;
    }
    renderTitle(graphics, font);
    List<CascadeNeed> needs = needs();
    int first = firstPlace(needs);
    for (int place = first; place < Math.min(needs.size(), first + COLUMNS * ROWS); place++) {
      int cell = place - first;
      int x = X + cell % COLUMNS * PITCH;
      int y = Y + cell / COLUMNS * PITCH;
      renderSlot(graphics, font, needs.get(place), x, y);
    }
  }

  /** Nothing chosen, or a chosen item still at zero (answer 2053). */
  private void renderHint(GuiGraphics graphics, Font font) {
    Optional<Item> chosen = reading.chosen();
    if (chosen.isEmpty()) {
      Component pick = Component.translatable(DeskScreenKeys.CASCADE_PICK);
      title(graphics, font, pick, BlueprintPainter.TEXT_SOFT);
      return;
    }
    title(graphics, font, chosen.get().getDescription(), BlueprintPainter.TEXT);
    text(graphics, font, reading.reason(), Y, BlueprintPainter.TEXT_SOFT);
  }

  private void renderTitle(GuiGraphics graphics, Font font) {
    reading
        .chosen()
        .ifPresent(item ->
            title(
                graphics,
                font,
                Component.translatable(
                    DeskScreenKeys.CASCADE_MATERIALS, reading.quantity(), item.getDescription()),
                BlueprintPainter.TEXT));
  }

  private static void title(GuiGraphics graphics, Font font, Component text, int color) {
    BlueprintPainter.Look look = new BlueprintPainter.Look(color, WIDTH - TITLE_SHIFT, 1f);
    BlueprintPainter.fromLeft(graphics, font, text, X + TITLE_SHIFT, TITLE_Y, look);
  }

  private static void text(GuiGraphics graphics, Font font, Component text, int y, int color) {
    BlueprintPainter.Look look = new BlueprintPainter.Look(color, WIDTH, 1f);
    BlueprintPainter.fromLeft(graphics, font, text, X, y, look);
  }

  /** In a fixed order, so the slots never move while the player fills the input. */
  private List<CascadeNeed> needs() {
    return reading.plan().map(CascadePlan::needs).orElse(List.of());
  }

  private int firstPlace(List<CascadeNeed> needs) {
    return scroll.first(rows(needs.size())) * COLUMNS;
  }

  private static int rows(int size) {
    return Math.ceilDiv(size, COLUMNS);
  }

  private void renderSlot(GuiGraphics graphics, Font font, CascadeNeed need, int x, int y) {
    BlueprintPainter.slot(graphics, x, y);
    graphics.fill(x + 1, y + 1, x + SLOT - 1, y + SLOT - 1, veil(need));
    graphics.renderOutline(x, y, SLOT, SLOT, frame(need));
    graphics.renderFakeItem(new ItemStack(CascadeRecipes.itemOf(need.item())), x + 1, y + 1);
    renderCount(graphics, font, String.valueOf(need.count()), x, y);
  }

  private MaterialLevel level(CascadeNeed need) {
    return MaterialLevel.of(reading.inInput(need.item()), need.count());
  }

  private int veil(CascadeNeed need) {
    if (need.ready()) {
      return BLUE;
    }
    return switch (level(need)) {
      case READY -> GREEN;
      case PARTIAL -> YELLOW;
      case MISSING -> RED;
    };
  }

  private int frame(CascadeNeed need) {
    if (need.ready()) {
      return BLUE_FRAME;
    }
    return switch (level(need)) {
      case READY -> GREEN_FRAME;
      case PARTIAL -> YELLOW_FRAME;
      case MISSING -> RED_FRAME;
    };
  }

  /** As the count of a slot, smaller when four digits would not fit. */
  private static void renderCount(GuiGraphics graphics, Font font, String count, int x, int y) {
    float scale = Math.min(1f, (float) COUNT_ROOM / font.width(count));
    graphics.pose().pushPose();
    graphics
        .pose()
        .translate(
            x + COUNT_RIGHT - font.width(count) * scale,
            y + COUNT_BOTTOM - (font.lineHeight - 1) * scale,
            BlueprintPainter.OVER_ITEMS);
    graphics.pose().scale(scale, scale, 1);
    graphics.drawString(font, count, 0, 0, BlueprintPainter.TEXT, true);
    graphics.pose().popPose();
  }

  static Component amount(int count, String item) {
    return Component.translatable(
        DeskScreenKeys.CASCADE_AMOUNT, count, CascadeRecipes.itemOf(item).getDescription());
  }

  /** The need under a point of the window. */
  private Optional<CascadeNeed> at(double x, double y) {
    int column = (int) Math.floor((x - X) / PITCH);
    int row = (int) Math.floor((y - Y) / PITCH);
    if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) {
      return Optional.empty();
    }
    List<CascadeNeed> needs = needs();
    int place = firstPlace(needs) + row * COLUMNS + column;
    return place < needs.size() ? Optional.of(needs.get(place)) : Optional.empty();
  }

  /** The name and how many the input has, or that the part is ready (answers 2006 and 2052). */
  List<Component> hint(double x, double y) {
    return at(x, y)
        .map(need ->
            List.<Component>of(CascadeRecipes.itemOf(need.item()).getDescription(), detail(need)))
        .orElse(List.of());
  }

  private Component detail(CascadeNeed need) {
    MutableComponent text =
        need.ready()
            ? Component.translatable(DeskScreenKeys.CASCADE_READY_PART, need.count())
            : Component.translatable(
                DeskScreenKeys.CASCADE_HAS, reading.inInput(need.item()), need.count());
    return text.withStyle(ChatFormatting.GRAY);
  }

  /** True when the wheel was over the slots, which only a pack fills past three rows. */
  boolean scroll(double x, double y, double delta) {
    boolean over = x >= X && x < X + WIDTH && y >= Y && y < Y + ROWS * PITCH;
    if (over) {
      scroll.scroll(delta, rows(needs().size()));
    }
    return over;
  }
}
