package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.CascadeRecipes;
import io.github.zenndg.cubit.domain.desk.CascadePlan;
import io.github.zenndg.cubit.domain.desk.MaterialLevel;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * The first step of the Cascade tab: the base materials in slots, green with enough, yellow with
 * some and red with none, and the parts already made the plan uses (answers 2006, 2008 and 2045).
 */
final class CascadeMaterials {
  private static final int X = 96;
  private static final int Y = 36;
  private static final int WIDTH = 152;
  private static final int TITLE_Y = 23;
  private static final int USING_Y = 96;
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
  private static final int RED = 0xB8DD3F45;
  private static final int RED_FRAME = 0xFFFF7A7A;
  private static final String COMMA = ", ";

  private final CascadeReading reading;
  private final ScrollRows scroll = new ScrollRows(ROWS);

  CascadeMaterials(CascadeReading reading) {
    this.reading = reading;
  }

  /** Over the labels of the screen, so from the left and the top of the window. */
  void render(GuiGraphics graphics, Font font) {
    Optional<CascadePlan> plan = reading.plan();
    if (plan.isEmpty()) {
      Component hint =
          reading.chosen().isPresent()
              ? reading.reason()
              : Component.translatable(DeskScreenKeys.CASCADE_PICK);
      text(graphics, font, hint, TITLE_Y, look(BlueprintPainter.TEXT_SOFT, 1f));
      return;
    }
    renderTitle(graphics, font);
    List<Map.Entry<String, Integer>> needs = needs();
    int first = firstPlace(needs);
    for (int place = first; place < Math.min(needs.size(), first + COLUMNS * ROWS); place++) {
      int cell = place - first;
      int x = X + cell % COLUMNS * PITCH;
      int y = Y + cell / COLUMNS * PITCH;
      renderSlot(graphics, font, needs.get(place), x, y);
    }
    renderUsing(graphics, font, plan.get());
  }

  private void renderTitle(GuiGraphics graphics, Font font) {
    reading
        .chosen()
        .ifPresent(item ->
            text(
                graphics,
                font,
                Component.translatable(
                    DeskScreenKeys.CASCADE_MATERIALS, reading.quantity(), item.getDescription()),
                TITLE_Y,
                look(BlueprintPainter.TEXT, 1f)));
  }

  private static BlueprintPainter.Look look(int color, float scale) {
    return new BlueprintPainter.Look(color, WIDTH, scale);
  }

  private static void text(
      GuiGraphics graphics, Font font, Component text, int y, BlueprintPainter.Look look) {
    BlueprintPainter.fromLeft(graphics, font, text, X, y, look);
  }

  /** In a fixed order, so the slots never move while the player fills the input. */
  private List<Map.Entry<String, Integer>> needs() {
    return reading.plan().map(CascadePlan::base).orElse(Map.of()).entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .toList();
  }

  private int firstPlace(List<Map.Entry<String, Integer>> needs) {
    return scroll.first(rows(needs.size())) * COLUMNS;
  }

  private static int rows(int size) {
    return Math.ceilDiv(size, COLUMNS);
  }

  private void renderSlot(
      GuiGraphics graphics, Font font, Map.Entry<String, Integer> need, int x, int y) {
    MaterialLevel level = MaterialLevel.of(reading.inInput(need.getKey()), need.getValue());
    BlueprintPainter.slot(graphics, x, y);
    graphics.fill(x + 1, y + 1, x + SLOT - 1, y + SLOT - 1, veil(level));
    graphics.renderOutline(x, y, SLOT, SLOT, frame(level));
    ItemStack item = new ItemStack(CascadeRecipes.itemOf(need.getKey()));
    graphics.renderFakeItem(item, x + 1, y + 1);
    renderCount(graphics, font, String.valueOf(need.getValue()), x, y);
  }

  private static int veil(MaterialLevel level) {
    return switch (level) {
      case READY -> GREEN;
      case PARTIAL -> YELLOW;
      case MISSING -> RED;
    };
  }

  private static int frame(MaterialLevel level) {
    return switch (level) {
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

  private static void renderUsing(GuiGraphics graphics, Font font, CascadePlan plan) {
    if (plan.reused().isEmpty()) {
      return;
    }
    String parts =
        plan.reused().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(part -> amount(part.getValue(), part.getKey()).getString())
            .collect(Collectors.joining(COMMA));
    Component using = Component.translatable(DeskScreenKeys.CASCADE_USING, parts);
    text(
        graphics,
        font,
        using,
        USING_Y,
        look(BlueprintPainter.CRAFTING, BlueprintPainter.SMALL_SCALE));
  }

  static Component amount(int count, String item) {
    return Component.translatable(
        DeskScreenKeys.CASCADE_AMOUNT, count, CascadeRecipes.itemOf(item).getDescription());
  }

  /** The material under a point of the window. */
  private Optional<Map.Entry<String, Integer>> at(double x, double y) {
    int column = (int) Math.floor((x - X) / PITCH);
    int row = (int) Math.floor((y - Y) / PITCH);
    if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) {
      return Optional.empty();
    }
    List<Map.Entry<String, Integer>> needs = needs();
    int place = firstPlace(needs) + row * COLUMNS + column;
    return place < needs.size() ? Optional.of(needs.get(place)) : Optional.empty();
  }

  /** The name and how many the input has (answer 2006). */
  List<Component> hint(double x, double y) {
    return at(x, y)
        .map(need ->
            List.<Component>of(
                CascadeRecipes.itemOf(need.getKey()).getDescription(),
                Component.translatable(
                        DeskScreenKeys.CASCADE_HAS, reading.inInput(need.getKey()), need.getValue())
                    .withStyle(ChatFormatting.GRAY)))
        .orElse(List.of());
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
