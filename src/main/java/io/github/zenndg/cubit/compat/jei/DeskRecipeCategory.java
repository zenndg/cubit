package io.github.zenndg.cubit.compat.jei;

import io.github.zenndg.cubit.block.DeskGrid;
import io.github.zenndg.cubit.registry.CubitBlocks;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The recipes of the Engineering Desk in JEI: the 3x3 grid as the desk shows it, the arrow and the
 * item it makes, with the desk as the catalyst (answers 1461, 1531 and 1532). The special recipes
 * give their ingredients and their item as the others, and fill the grid in order.
 */
final class DeskRecipeCategory implements IRecipeCategory<RecipeHolder<CraftingRecipe>> {
  private static final int SLOT = 18;
  private static final int GRID = DeskGrid.SIDE * SLOT;
  private static final int ARROW_X = GRID + 6;
  private static final int ARROW_Y = 18;
  private static final int OUTPUT_X = ARROW_X + 30;
  private static final int OUTPUT_Y = 18;
  private static final int SLOT_INSET = 1;
  private static final int OUTPUT_INSET = 5;
  private static final int WIDTH = OUTPUT_X + 26;
  private static final int HEIGHT = GRID;

  private final IDrawable icon;
  private final IDrawableStatic arrow;

  DeskRecipeCategory(IGuiHelper guiHelper) {
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(CubitItems.ENGINEERING_DESK.get()));
    this.arrow = guiHelper.getRecipeArrow();
  }

  @Override
  public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
    return JeiDeskRecipes.TYPE;
  }

  @Override
  public Component getTitle() {
    return Component.translatable(CubitBlocks.ENGINEERING_DESK.get().getDescriptionId());
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public int getWidth() {
    return WIDTH;
  }

  @Override
  public int getHeight() {
    return HEIGHT;
  }

  @Override
  public void setRecipe(
      IRecipeLayoutBuilder builder, RecipeHolder<CraftingRecipe> recipe, IFocusGroup focuses) {
    List<Ingredient> cells = DeskGrid.of(recipe.value());
    for (int cell = 0; cell < DeskGrid.CELLS; cell++) {
      int x = (cell % DeskGrid.SIDE) * SLOT + SLOT_INSET;
      int y = (cell / DeskGrid.SIDE) * SLOT + SLOT_INSET;
      builder
          .addSlot(RecipeIngredientRole.INPUT, x, y)
          .setStandardSlotBackground()
          .addIngredients(cells.get(cell));
    }
    builder
        .addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + OUTPUT_INSET, OUTPUT_Y + SLOT_INSET)
        .setOutputSlotBackground()
        .addItemStack(resultOf(recipe));
  }

  @Override
  public void draw(
      RecipeHolder<CraftingRecipe> recipe,
      IRecipeSlotsView slots,
      GuiGraphics graphics,
      double mouseX,
      double mouseY) {
    arrow.draw(graphics, ARROW_X, ARROW_Y);
  }

  private static ItemStack resultOf(RecipeHolder<CraftingRecipe> recipe) {
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft.level == null
        ? ItemStack.EMPTY
        : recipe.value().getResultItem(minecraft.level.registryAccess());
  }
}
