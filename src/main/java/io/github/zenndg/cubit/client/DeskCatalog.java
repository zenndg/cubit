package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.domain.desk.CatalogSearch;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.item.DeskCategoryCodecs;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The catalog of the desk: every recipe of the mod, one category at a time, or every category that
 * matches a search, each one shown by the rule of the tab (answers 1474, 1523, 1524 and 2400). A
 * click sends the place of the recipe in the whole catalog.
 */
final class DeskCatalog {
  static final int X = 8;
  static final int ICONS_Y = 22;
  static final int SEARCH_Y = 36;
  static final int Y = 50;
  static final int COLUMNS = 4;
  static final int ROWS = 4;
  static final int WIDTH = COLUMNS * ScreenPainter.SLOT_SIZE;
  private static final int CELL = ScreenPainter.SLOT_SIZE;
  private static final int ICON_INSET = 1;
  private static final int LOCKED_VEIL = 0x701D3C8F;
  private static final float LOCKED_SHADE = 0.3f;
  private static final int ICON_SIZE = 16;
  private static final int CHOSEN = 0xFF4FC3F7;
  private static final int BAR_GAP = 1;
  private static final int BAR_WIDTH = 3;

  private final DeskCategoryBar categories = new DeskCategoryBar(X, ICONS_Y);
  private DeskCategory chosenCategory = DeskCategory.ROBOT;
  private CatalogSearch search = CatalogSearch.of(null);
  private int firstRow;
  private final DeskCatalogRules rules;

  DeskCatalog(DeskCatalogRules rules) {
    this.rules = rules;
  }

  List<RecipeHolder<CraftingRecipe>> recipes() {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft.level == null
        ? List.of()
        : DeskRecipes.all(minecraft.level.getRecipeManager());
  }

  static ItemStack resultOf(RecipeHolder<CraftingRecipe> recipe) {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft.level == null
        ? ItemStack.EMPTY
        : recipe.value().getResultItem(minecraft.level.registryAccess());
  }

  /**
   * The places in the whole catalog that show now: by category, in the order of the catalog, which
   * never moves with the materials (answers 1499 and 1987).
   */
  List<Integer> view() {
    List<RecipeHolder<CraftingRecipe>> all = recipes();
    List<Integer> shown = new ArrayList<>();
    for (int index = 0; index < all.size(); index++) {
      RecipeHolder<CraftingRecipe> recipe = all.get(index);
      String name = resultOf(recipe).getHoverName().getString();
      if (rules.shows(recipe)
          && search.shows(DeskCategoryCodecs.of(recipe.value()), name, chosenCategory)) {
        shown.add(index);
      }
    }
    shown.sort(
        Comparator.comparingInt(
            (Integer index) -> DeskCategoryCodecs.of(all.get(index).value()).ordinal()));
    return shown;
  }

  void search(String typed) {
    search = CatalogSearch.of(typed);
    firstRow = 0;
  }

  /** Opens on the category of the chosen recipe, so the player sees what the desk makes. */
  void showCategoryOf(int chosen) {
    List<RecipeHolder<CraftingRecipe>> all = recipes();
    if (chosen >= 0 && chosen < all.size()) {
      chosenCategory = DeskCategoryCodecs.of(all.get(chosen).value());
    }
  }

  void render(GuiGraphics graphics, int left, int top) {
    categories.render(graphics, left, top, chosenCategory, !search.isEmpty());
    List<RecipeHolder<CraftingRecipe>> all = recipes();
    List<Integer> shown = view();
    for (int cell = 0; cell < COLUMNS * ROWS; cell++) {
      int place = firstRow * COLUMNS + cell;
      int x = left + X + (cell % COLUMNS) * CELL;
      int y = top + Y + (cell / COLUMNS) * CELL;
      BlueprintPainter.slot(graphics, x, y);
      if (place >= shown.size()) {
        continue;
      }
      int index = shown.get(place);
      if (index == rules.chosen()) {
        graphics.renderOutline(x, y, CELL, CELL, CHOSEN);
      }
      renderItem(graphics, all.get(index), x + ICON_INSET, y + ICON_INSET);
    }
    renderScrollBar(graphics, left, top, shown.size());
  }

  /** An item in a cycle shows dark with a padlock, by the rule of the tab (answer 2034). */
  private void renderItem(GuiGraphics graphics, RecipeHolder<CraftingRecipe> recipe, int x, int y) {
    DeskCatalogRules.Look look = rules.look(recipe);
    if (look == DeskCatalogRules.Look.READY) {
      graphics.renderFakeItem(resultOf(recipe), x, y);
      return;
    }
    renderDark(graphics, resultOf(recipe), x, y);
    if (look == DeskCatalogRules.Look.LOCKED) {
      int corner = ICON_SIZE - BlueprintPainter.LOCK_SIZE;
      BlueprintPainter.padlock(graphics, x + corner, y + corner, BlueprintPainter.LINE);
    }
  }

  private static void renderDark(GuiGraphics graphics, ItemStack item, int x, int y) {
    RenderSystem.setShaderColor(LOCKED_SHADE, LOCKED_SHADE, LOCKED_SHADE, 1f);
    graphics.renderFakeItem(item, x, y);
    graphics.flush();
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    graphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, LOCKED_VEIL);
  }

  private void renderScrollBar(GuiGraphics graphics, int left, int top, int shown) {
    int rows = Math.max(1, (shown + COLUMNS - 1) / COLUMNS);
    int x = left + X + WIDTH + BAR_GAP;
    int y = top + Y;
    int height = ROWS * CELL;
    graphics.fill(x, y, x + BAR_WIDTH, y + height, BlueprintPainter.CELL);
    int thumb = Math.max(BAR_WIDTH, height * Math.min(ROWS, rows) / rows);
    int last = Math.max(1, rows - ROWS);
    int thumbY = rows <= ROWS ? y : y + (height - thumb) * firstRow / last;
    graphics.fill(x, thumbY, x + BAR_WIDTH, thumbY + thumb, BlueprintPainter.LINE_SOFT);
  }

  /** The recipe under the mouse, as its place in the whole catalog. */
  Optional<Integer> at(double mouseX, double mouseY, int left, int top) {
    int column = (int) Math.floor((mouseX - left - X) / CELL);
    int row = (int) Math.floor((mouseY - top - Y) / CELL);
    if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) {
      return Optional.empty();
    }
    int place = (firstRow + row) * COLUMNS + column;
    List<Integer> shown = view();
    return place < shown.size() ? Optional.of(shown.get(place)) : Optional.empty();
  }

  /** A click on an icon shows its category. True when the click was on the row of icons. */
  boolean clickCategory(double mouseX, double mouseY, int left, int top) {
    Optional<DeskCategory> category = categories.at(mouseX, mouseY, left, top);
    category.ifPresent(found -> {
      chosenCategory = found;
      firstRow = 0;
    });
    return category.isPresent();
  }

  boolean isOver(double mouseX, double mouseY, int left, int top) {
    return mouseX >= left + X
        && mouseX < left + X + WIDTH + BAR_GAP + BAR_WIDTH
        && mouseY >= top + Y
        && mouseY < top + Y + ROWS * CELL;
  }

  void scroll(double delta) {
    int rows = (view().size() + COLUMNS - 1) / COLUMNS;
    int last = Math.max(0, rows - ROWS);
    firstRow = Math.max(0, Math.min(last, firstRow - (int) Math.signum(delta)));
  }

  /** The name of the item and, when it has a padlock, why (answer 2034). */
  void tooltip(GuiGraphics graphics, Font font, int index, int mouseX, int mouseY) {
    RecipeHolder<CraftingRecipe> recipe = recipes().get(index);
    List<Component> lines = new ArrayList<>(List.of(resultOf(recipe).getHoverName()));
    lines.addAll(rules.hints(recipe));
    graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
  }

  /** The tooltip of the icon of a category, when the mouse is over one. */
  void categoryTooltip(GuiGraphics graphics, Font font, int left, int top, int mouseX, int mouseY) {
    categories
        .at(mouseX, mouseY, left, top)
        .ifPresent(category -> DeskCategoryBar.tooltip(graphics, font, category, mouseX, mouseY));
  }
}
