package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.DeskGrid;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.DeskShortage;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskOrder;
import io.github.zenndg.cubit.domain.desk.DeskOrderGate;
import io.github.zenndg.cubit.registry.CubitRecipes;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * The buttons of the desk screen, read on the server (rules 19 and 20): the order, the recipe of
 * the catalog, and the marks the player puts by hand in the grid (answers 1426 and 1444). Any
 * recipe can be chosen, and an order only starts with it released and with its materials (answer
 * 1991).
 */
public final class DeskControl {
  public static final int MINUS = 0;
  public static final int PLUS = 1;
  public static final int MINUS_SHIFT = 2;
  public static final int PLUS_SHIFT = 3;
  public static final int CANCEL = 4;
  public static final int CHOOSE_FIRST = 1_000;
  public static final int MARK_FIRST = 2_000;

  private DeskControl() {}

  public static int choose(int recipeIndex) {
    return CHOOSE_FIRST + recipeIndex;
  }

  public static int mark(int cell) {
    return MARK_FIRST + cell;
  }

  static boolean click(
      EngineeringDeskBlockEntity desk, ServerPlayer player, NonNullList<ItemStack> marks, int id) {
    switch (id) {
      case MINUS -> desk.adjustOrder(-DeskOrder.STEP);
      case PLUS -> raise(desk, player, DeskOrder.STEP);
      case MINUS_SHIFT -> desk.adjustOrder(-DeskOrder.SHIFT_STEP);
      case PLUS_SHIFT -> raise(desk, player, DeskOrder.SHIFT_STEP);
      case CANCEL -> desk.cancelOrder();
      default -> {
        return chooseOrMark(desk, player, marks, id);
      }
    }
    return true;
  }

  private static void raise(EngineeringDeskBlockEntity desk, ServerPlayer player, int step) {
    boolean allowed =
        desk.recipeId()
            .flatMap(id -> DeskRecipes.find(player.serverLevel().getRecipeManager(), id))
            .map(recipe -> gate(desk, player, recipe).allows(desk.order().isActive()))
            .orElse(false);
    if (allowed) {
      desk.adjustOrder(step);
    }
  }

  private static DeskOrderGate gate(
      EngineeringDeskBlockEntity desk, ServerPlayer player, RecipeHolder<CraftingRecipe> recipe) {
    DeskShortage shortage =
        DeskShortage.of(DeskGrid.of(recipe.value()), desk.input(), desk.output(), desk.slots());
    return new DeskOrderGate(DeskRecipes.unlocked(player, recipe), shortage.isEmpty());
  }

  private static boolean chooseOrMark(
      EngineeringDeskBlockEntity desk, ServerPlayer player, NonNullList<ItemStack> marks, int id) {
    RecipeManager recipes = player.serverLevel().getRecipeManager();
    if (id >= MARK_FIRST && id < MARK_FIRST + DeskGrid.CELLS) {
      marks.set(id - MARK_FIRST, player.containerMenu.getCarried().copyWithCount(1));
      recognize(desk, player, marks, recipes);
      return true;
    }
    List<RecipeHolder<CraftingRecipe>> all = DeskRecipes.all(recipes);
    int index = id - CHOOSE_FIRST;
    if (index < 0 || index >= all.size()) {
      return false;
    }
    pick(desk, all.get(index));
    return true;
  }

  /** Marks that make a recipe choose it, as the crafting table would (answer 1426). */
  private static void recognize(
      EngineeringDeskBlockEntity desk,
      ServerPlayer player,
      NonNullList<ItemStack> marks,
      RecipeManager recipes) {
    CraftingInput input = CraftingInput.of(DeskGrid.SIDE, DeskGrid.SIDE, marks);
    recipes
        .getRecipeFor(CubitRecipes.ENGINEERING_TYPE.get(), input, player.serverLevel())
        .ifPresent(found -> pick(desk, found));
  }

  private static void pick(EngineeringDeskBlockEntity desk, RecipeHolder<CraftingRecipe> found) {
    if (!desk.recipeId().map(found.id()::equals).orElse(false)) {
      desk.choose(found.id());
    }
  }
}
