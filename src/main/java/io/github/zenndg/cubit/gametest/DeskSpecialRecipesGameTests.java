package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskGrid;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The three special recipes of the desk without a shape give their two ingredients and their item,
 * which the grid of the desk and JEI show (answer 1531).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskSpecialRecipesGameTests {
  private static final String ARENA = "arena";
  private static final String REPAIR = "repair_general_robot";
  private static final String RECHARGE = "recharge_general_robot";
  private static final int INGREDIENTS = 2;
  private static final String WRONG_INGREDIENTS = "Wrong ingredients shown for ";
  private static final String WRONG_RESULT = "Wrong item shown for ";

  private static final Map<String, Supplier<Item>> RESULTS =
      Map.of(
          REPAIR, CubitItems.GENERAL_ROBOT,
          RECHARGE, CubitItems.GENERAL_ROBOT);

  private DeskSpecialRecipesGameTests() {}

  @GameTest(template = ARENA)
  public static void theSpecialRecipesShowTheirIngredientsAndItem(GameTestHelper helper) {
    RESULTS.forEach((path, result) -> {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
      Recipe<?> recipe = helper.getLevel().getRecipeManager().byKey(id).orElseThrow().value();
      long shown = DeskGrid.of(recipe).stream().filter(cell -> !cell.isEmpty()).count();
      helper.assertTrue(shown == INGREDIENTS, WRONG_INGREDIENTS + path);
      helper.assertTrue(
          recipe.getResultItem(helper.getLevel().registryAccess()).is(result.get()),
          WRONG_RESULT + path);
    });
    helper.succeed();
  }
}
