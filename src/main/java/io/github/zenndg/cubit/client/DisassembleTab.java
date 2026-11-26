package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.desk.CascadeCost;
import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.item.ModuleItem;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.DisassemblyMenuPart;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;

/**
 * The Disassemble tab, as the accepted preview: the slot of the item, the arrow of the progress,
 * what comes back, the cost of a unit, the warning or the reason of a refusal and the two buttons
 * (answers 2533 to 2535). It only shows and asks: the server decides (rule 19).
 */
final class DisassembleTab {
  private static final int LABEL_Y = 24;
  private static final int ITEM_X = 8;
  private static final int SLOT_Y = 36;
  private static final int PARTS_X = 62;
  private static final int PARTS_PER_ROW = 7;
  private static final int PART_STEP = 19;
  private static final int MAX_PARTS = PARTS_PER_ROW * 2;
  private static final int ARROW_X = 31;
  private static final int ARROW_Y = 45;
  private static final int ARROW_WIDTH = 24;
  private static final int COST_Y = 80;
  private static final int MESSAGE_Y = 96;
  private static final int LEFT_Y = 110;
  private static final int BUTTON_Y = 108;
  private static final int BUTTON_WIDTH = 53;
  private static final int BUTTON_HEIGHT = 12;
  private static final int START_X = 138;
  private static final int CANCEL_X = 195;
  private static final int ROOM = 240;
  private static final int ITEM_INSET = 1;
  private static final int PERCENT = 100;
  private static final int ORANGE = 0xFFFFB35C;
  private static final int ONE_UNIT = 1;

  private final EngineeringDeskMenu menu;
  private final IntConsumer send;
  private boolean open;
  private BlueprintButton start;
  private BlueprintButton cancel;

  DisassembleTab(EngineeringDeskMenu menu, IntConsumer send) {
    this.menu = menu;
    this.send = send;
  }

  void init(int left, int top, Consumer<BlueprintButton> add) {
    start =
        button(
            left + START_X,
            top,
            DeskScreenKeys.DISASSEMBLE_START,
            BlueprintPainter.PRIMARY,
            DisassemblyMenuPart.START);
    cancel =
        button(
            left + CANCEL_X,
            top,
            DeskScreenKeys.CANCEL,
            BlueprintPainter.CELL,
            DisassemblyMenuPart.CANCEL);
    add.accept(start);
    add.accept(cancel);
    update();
  }

  private BlueprintButton button(int x, int top, String key, int fill, int id) {
    return new BlueprintButton(
        x,
        top + BUTTON_Y,
        BUTTON_WIDTH,
        BUTTON_HEIGHT,
        Component.translatable(key),
        fill,
        () -> send.accept(id));
  }

  void show(boolean shown) {
    open = shown;
    update();
  }

  void tick() {
    update();
  }

  private void update() {
    DisassemblyMenuPart part = menu.disassembly();
    start.visible = open;
    cancel.visible = open;
    start.active = !part.running() && part.refusal().allows();
    cancel.active = part.running();
  }

  void render(GuiGraphics graphics, int left, int top) {
    BlueprintPainter.slot(graphics, left + ITEM_X, top + SLOT_Y);
    BlueprintPainter.arrow(graphics, left + ARROW_X, top + ARROW_Y, ARROW_WIDTH);
    if (menu.disassembly().running()) {
      int done = ARROW_WIDTH * menu.progressPercent() / PERCENT;
      graphics.fill(
          left + ARROW_X,
          top + ARROW_Y - 1,
          left + ARROW_X + done,
          top + ARROW_Y + 2,
          BlueprintPainter.CRAFTING);
    }
    List<ItemStack> parts = parts();
    for (int index = 0; index < parts.size(); index++) {
      renderPart(graphics, left + partX(index), top + partY(index), parts.get(index));
    }
  }

  private void renderPart(GuiGraphics graphics, int x, int y, ItemStack part) {
    BlueprintPainter.slot(graphics, x, y);
    if (part.getItem() instanceof ModuleItem module) {
      int colour = FastColor.ARGB32.opaque(TierLook.color(module.tier()));
      graphics.renderOutline(x, y, ScreenPainter.SLOT_SIZE, ScreenPainter.SLOT_SIZE, colour);
    }
    Font font = Minecraft.getInstance().font;
    graphics.renderItem(part, x + ITEM_INSET, y + ITEM_INSET);
    graphics.renderItemDecorations(font, part, x + ITEM_INSET, y + ITEM_INSET);
  }

  private static int partX(int index) {
    return PARTS_X + index % PARTS_PER_ROW * PART_STEP;
  }

  private static int partY(int index) {
    return SLOT_Y + index / PARTS_PER_ROW * PART_STEP;
  }

  private List<ItemStack> parts() {
    Minecraft game = Minecraft.getInstance();
    List<ItemStack> parts = game.level == null ? List.of() : menu.disassembly().parts(game.level);
    return parts.subList(0, Math.min(parts.size(), MAX_PARTS));
  }

  void renderLabels(GuiGraphics graphics, Font font) {
    DisassemblyMenuPart part = menu.disassembly();
    graphics.drawString(
        font,
        Component.translatable(DeskScreenKeys.DISASSEMBLE_ITEM),
        ITEM_X,
        LABEL_Y,
        BlueprintPainter.TEXT,
        false);
    graphics.drawString(
        font,
        Component.translatable(DeskScreenKeys.DISASSEMBLE_RETURNS),
        PARTS_X,
        LABEL_Y,
        BlueprintPainter.TEXT,
        false);
    if (!part.item().isEmpty()) {
      CascadeCost cost = menu.cascade().cost(ONE_UNIT);
      Component each =
          Component.translatable(
              DeskScreenKeys.DISASSEMBLE_EACH, EnergyFormat.rate(cost.energy()), cost.seconds());
      graphics.drawString(font, each, ITEM_X, COST_Y, BlueprintPainter.TEXT_SOFT, false);
    }
    renderMessage(graphics, font, part);
    if (part.running()) {
      Component left = Component.translatable(DeskScreenKeys.DISASSEMBLE_LEFT, part.left());
      graphics.drawString(font, left, ITEM_X, LEFT_Y, BlueprintPainter.TEXT, false);
    }
  }

  /** The warning of what is lost, the reason of a refusal, or how to start (2524, 2534). */
  private void renderMessage(GuiGraphics graphics, Font font, DisassemblyMenuPart part) {
    DisassemblyRefusal refusal = part.refusal();
    Component text;
    int colour;
    if (part.running() || refusal.allows()) {
      text = Component.translatable(DeskScreenKeys.DISASSEMBLE_LOST);
      colour = ORANGE;
    } else if (refusal == DisassemblyRefusal.EMPTY) {
      text = Component.translatable(DeskScreenKeys.DISASSEMBLE_EMPTY);
      colour = BlueprintPainter.TEXT_SOFT;
    } else {
      text = Component.translatable(DeskScreenKeys.disassemblyRefusal(refusal));
      colour = BlueprintPainter.WARNING;
    }
    BlueprintPainter.fromLeft(
        graphics, font, text, ITEM_X, MESSAGE_Y, new BlueprintPainter.Look(colour, ROOM, 1f));
  }

  /** The name of a part under the mouse, with the tooltip of the game. */
  void tooltips(GuiGraphics graphics, Font font, int x, int y, int left, int top) {
    List<ItemStack> parts = parts();
    for (int index = 0; index < parts.size(); index++) {
      int partX = left + partX(index);
      int partY = top + partY(index);
      if (x >= partX
          && x < partX + ScreenPainter.SLOT_SIZE
          && y >= partY
          && y < partY + ScreenPainter.SLOT_SIZE) {
        graphics.renderTooltip(font, parts.get(index), x, y);
      }
    }
  }
}
