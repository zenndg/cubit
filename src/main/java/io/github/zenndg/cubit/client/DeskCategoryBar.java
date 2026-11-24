package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The row of category icons over the catalog. Each icon is an item of the mod, and its name shows
 * in the tooltip (answers 1474 and 1524).
 */
final class DeskCategoryBar {
  static final int ICON_CELL = 12;
  private static final float ICON_SCALE = 0.75f;
  private static final int CHOSEN_FILL = 0x50DCE6FF;
  private static final Map<DeskCategory, Supplier<Item>> ICONS =
      Map.of(
          DeskCategory.ROBOT, CubitItems.GENERAL_ROBOT,
          DeskCategory.PARTS, CubitItems.SERVO_MOTOR,
          DeskCategory.ENERGY, CubitItems.BASE_POWER_CELL,
          DeskCategory.DOCK, CubitItems.INDUCTION_DOCK,
          DeskCategory.MODULES, CubitItems.ADVANCED_MODULE,
          DeskCategory.CARDS, CubitItems.FISHER_CARD,
          DeskCategory.TOOLS, CubitItems.TOOLBOX);

  private final int x;
  private final int y;

  DeskCategoryBar(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** The chosen icon is lit, unless a search shows every category. */
  void render(GuiGraphics graphics, int left, int top, DeskCategory chosen, boolean searching) {
    for (DeskCategory category : DeskCategory.values()) {
      int cellX = left + x + category.ordinal() * ICON_CELL;
      int cellY = top + y;
      if (category == chosen && !searching) {
        graphics.fill(cellX, cellY, cellX + ICON_CELL, cellY + ICON_CELL, CHOSEN_FILL);
        graphics.renderOutline(cellX, cellY, ICON_CELL, ICON_CELL, BlueprintPainter.LINE);
      }
      graphics.pose().pushPose();
      graphics.pose().translate(cellX, cellY, 0);
      graphics.pose().scale(ICON_SCALE, ICON_SCALE, 1f);
      graphics.renderFakeItem(new ItemStack(ICONS.get(category).get()), 0, 0);
      graphics.pose().popPose();
    }
  }

  Optional<DeskCategory> at(double mouseX, double mouseY, int left, int top) {
    int column = (int) Math.floor((mouseX - left - x) / ICON_CELL);
    boolean inRow = mouseY >= top + y && mouseY < top + y + ICON_CELL;
    if (!inRow || column < 0 || column >= DeskCategory.values().length) {
      return Optional.empty();
    }
    return Optional.of(DeskCategory.values()[column]);
  }

  static void tooltip(
      GuiGraphics graphics, Font font, DeskCategory category, int mouseX, int mouseY) {
    graphics.renderTooltip(
        font, Component.translatable(DeskScreenKeys.category(category)), mouseX, mouseY);
  }
}
