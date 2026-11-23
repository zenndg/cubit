package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.CascadeLaunch;
import io.github.zenndg.cubit.block.CascadeRecipes;
import io.github.zenndg.cubit.domain.desk.CascadeBook;
import io.github.zenndg.cubit.domain.desk.CascadeCheck;
import io.github.zenndg.cubit.domain.desk.CascadePlan;
import io.github.zenndg.cubit.domain.desk.CascadePlanner;
import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.MaterialLevel;
import io.github.zenndg.cubit.menu.CascadeMenuPart;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The right side of the Cascade tab, still in text (answer 2035): the base materials of what was
 * chosen, the question before the start and the table of the plan. It only shows: the server
 * decides again.
 */
final class CascadePanel {
  private static final int X = 96;
  private static final int Y = 24;
  private static final int LINE = 7;
  private static final int MAX_LINES = 11;
  private static final float SCALE = 0.75f;
  private static final int GREEN = 0xFF8BE9A0;
  private static final int YELLOW = 0xFFFFE082;
  private static final int RED = 0xFFFF8A80;
  private static final int TICKS_PER_SECOND = 20;
  private static final int ITEM_ROW = 2;
  private static final int COST_ROW = 3;
  private static final int WARNING_ROW = 5;

  private final EngineeringDeskMenu menu;
  private final DeskCatalog catalog;
  private Optional<CascadePlan> plan = Optional.empty();
  private CascadeStart start = CascadeStart.NO_PLAN;
  private boolean asking;

  CascadePanel(EngineeringDeskMenu menu, DeskCatalog catalog) {
    this.menu = menu;
    this.catalog = catalog;
  }

  /** Once per tick, because the plan reads every recipe of the desk. */
  void tick() {
    plan = Optional.empty();
    start = CascadeStart.NO_PLAN;
    Minecraft minecraft = Minecraft.getInstance();
    Optional<RecipeHolder<CraftingRecipe>> chosen = chosen();
    if (menu.cascade().state() != CascadeMenuPart.State.NONE || minecraft.level == null) {
      asking = false;
      return;
    }
    if (chosen.isEmpty()) {
      return;
    }
    Map<String, Integer> input = menu.inputCounts();
    CascadeBook book = CascadeRecipes.book(minecraft.level, input);
    Item item = DeskCatalog.resultOf(chosen.get()).getItem();
    int quantity = menu.cascade().quantity();
    String id = CascadeRecipes.idOf(item);
    plan = CascadePlanner.plan(book, id, quantity, menu.readyCounts());
    start =
        CascadeStart.decide(
            new CascadeCheck(
                menu.cascade().allowed(),
                plan.isPresent(),
                released(book, id),
                plan.map(found -> CascadeLaunch.hasBase(found, input)).orElse(false),
                menu.total() > menu.done(),
                menu.cascade().holdsItems()));
    asking &= start == CascadeStart.READY;
  }

  private static boolean released(CascadeBook book, String item) {
    return CascadePlanner.order(book, item).stream()
        .flatMap(List::stream)
        .allMatch(each -> book.recipeOf(each).map(recipe -> known(recipe.id())).orElse(false));
  }

  private static boolean known(String recipe) {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft.player != null
        && minecraft.player.getRecipeBook().contains(ResourceLocation.parse(recipe));
  }

  private Optional<RecipeHolder<CraftingRecipe>> chosen() {
    List<RecipeHolder<CraftingRecipe>> all = catalog.recipes();
    int index = menu.cascade().recipe();
    return index < 0 || index >= all.size() ? Optional.empty() : Optional.of(all.get(index));
  }

  boolean choosing() {
    return menu.cascade().state() == CascadeMenuPart.State.NONE;
  }

  boolean mayStart() {
    return choosing() && start == CascadeStart.READY;
  }

  boolean isAsking() {
    return asking;
  }

  void ask(boolean now) {
    asking = now;
  }

  /** Why the plan cannot start, for the button. */
  Component reason() {
    return Component.translatable(DeskScreenKeys.cascadeStart(start));
  }

  void render(GuiGraphics graphics, Font font) {
    if (!choosing()) {
      renderTable(graphics, font);
    } else if (asking) {
      renderQuestion(graphics, font);
    } else {
      renderMaterials(graphics, font);
    }
  }

  private void renderMaterials(GuiGraphics graphics, Font font) {
    if (plan.isEmpty()) {
      Component hint =
          chosen().isPresent() ? reason() : Component.translatable(DeskScreenKeys.CASCADE_PICK);
      line(graphics, font, 0, hint, BlueprintPainter.TEXT_SOFT);
      return;
    }
    Map<String, Integer> input = menu.inputCounts();
    int row = 0;
    for (Map.Entry<String, Integer> need :
        plan.get().base().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .limit(MAX_LINES)
            .toList()) {
      int has = input.getOrDefault(need.getKey(), 0);
      Component name = CascadeRecipes.itemOf(need.getKey()).getDescription();
      Component text =
          Component.translatable(DeskScreenKeys.CASCADE_MATERIAL, has, need.getValue(), name);
      line(graphics, font, row++, text, color(MaterialLevel.of(has, need.getValue())));
    }
  }

  private static int color(MaterialLevel level) {
    return switch (level) {
      case READY -> GREEN;
      case PARTIAL -> YELLOW;
      case MISSING -> RED;
    };
  }

  private void renderQuestion(GuiGraphics graphics, Font font) {
    ItemStack item = chosen().map(DeskCatalog::resultOf).orElse(ItemStack.EMPTY);
    int crafts = plan.map(CascadePlan::totalCrafts).orElse(0);
    int seconds = Math.ceilDiv(crafts * menu.cascade().craftTicks(), TICKS_PER_SECOND);
    long energy = (long) crafts * menu.cascade().craftEnergy();
    line(graphics, font, 0, Component.translatable(DeskScreenKeys.CASCADE_ASK), GREEN);
    line(
        graphics,
        font,
        ITEM_ROW,
        Component.translatable(
            DeskScreenKeys.CASCADE_ASKED, menu.cascade().quantity(), item.getHoverName()),
        BlueprintPainter.TEXT);
    line(
        graphics,
        font,
        COST_ROW,
        Component.translatable(DeskScreenKeys.CASCADE_COST, energy, seconds),
        BlueprintPainter.TEXT_SOFT);
    line(
        graphics, font, WARNING_ROW, Component.translatable(DeskScreenKeys.CASCADE_NO_CANCEL), RED);
  }

  private void renderTable(GuiGraphics graphics, Font font) {
    List<RecipeHolder<CraftingRecipe>> all = catalog.recipes();
    List<CascadeMenuPart.StepView> steps = menu.cascade().steps();
    boolean before = true;
    for (int index = 0; index < Math.min(steps.size(), MAX_LINES); index++) {
      CascadeMenuPart.StepView step = steps.get(index);
      boolean done = step.done() >= step.total();
      ItemStack item =
          step.recipe() < 0 || step.recipe() >= all.size()
              ? ItemStack.EMPTY
              : DeskCatalog.resultOf(all.get(step.recipe()));
      Component text =
          Component.translatable(
              DeskScreenKeys.CASCADE_STEP,
              index + 1,
              step.total(),
              item.getHoverName(),
              step.done(),
              step.total());
      int color = done ? GREEN : before ? BlueprintPainter.CRAFTING : BlueprintPainter.TEXT_SOFT;
      line(graphics, font, index, text, color);
      before = before && done;
    }
  }

  private static void line(GuiGraphics graphics, Font font, int row, Component text, int color) {
    graphics.pose().pushPose();
    graphics.pose().translate(X, Y + row * LINE, 0);
    graphics.pose().scale(SCALE, SCALE, 1);
    graphics.drawString(font, text, 0, 0, color, false);
    graphics.pose().popPose();
  }
}
