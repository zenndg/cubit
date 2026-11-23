package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskGrid;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.DeskShortage;
import java.util.List;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * What the grid of the desk shows as missing, by the quantity: a Toolbox takes 7 iron, one redstone
 * and one obsidian (answers 1996 to 1999).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskShortageGameTests {
  private static final String ARENA = "arena";
  private static final int SLOTS = 12;
  private static final int IRON_FOR_ONE = 7;
  private static final int ONE = 1;
  private static final int TWO = 2;
  private static final int FIVE = 5;
  private static final String MISSING = "Nothing should be missing";
  private static final String WRONG_CELLS = "Wrong number of missing cells";
  private static final String WRONG_COUNT = "Wrong number of missing items";

  private DeskShortageGameTests() {}

  private static List<Ingredient> toolbox(GameTestHelper helper) {
    return DeskGrid.of(
        DeskRecipes.find(helper.getLevel().getRecipeManager(), EngineeringDeskGameTests.TOOLBOX)
            .orElseThrow()
            .value());
  }

  private static Container with(int iron, int others) {
    Container items = new SimpleContainer(SLOTS);
    items.setItem(0, new ItemStack(Items.IRON_INGOT, iron));
    items.setItem(1, new ItemStack(Items.REDSTONE, others));
    items.setItem(2, new ItemStack(Items.OBSIDIAN, others));
    return items;
  }

  private static DeskShortage of(
      GameTestHelper helper, Container input, Container output, int units) {
    return DeskShortage.of(toolbox(helper), input, output, SLOTS, units);
  }

  private static int count(DeskShortage shortage) {
    return shortage.lacks().values().stream().mapToInt(DeskShortage.Lack::count).sum();
  }

  /** With items for one, one unit is ready and two miss each mark once. */
  @GameTest(template = ARENA)
  public static void theQuantityDecidesWhatIsMissing(GameTestHelper helper) {
    Container items = with(IRON_FOR_ONE, ONE);
    Container empty = new SimpleContainer(SLOTS);

    helper.assertTrue(of(helper, items, empty, ONE).isEmpty(), MISSING);
    DeskShortage two = of(helper, items, empty, TWO);
    helper.assertValueEqual(count(two), IRON_FOR_ONE + TWO, WRONG_COUNT);
    helper.assertTrue(of(helper, with(IRON_FOR_ONE * FIVE, FIVE), empty, FIVE).isEmpty(), MISSING);
    helper.succeed();
  }

  /** Twelve iron for two Toolboxes fill six marks, and only the last one misses (answer 1998). */
  @GameTest(template = ARENA)
  public static void onlyTheMarksWithoutItemsAreMissing(GameTestHelper helper) {
    DeskShortage shortage =
        of(helper, with((IRON_FOR_ONE - ONE) * TWO, TWO), new SimpleContainer(SLOTS), TWO);

    helper.assertValueEqual(shortage.lacks().size(), ONE, WRONG_CELLS);
    helper.assertValueEqual(count(shortage), TWO, WRONG_COUNT);
    helper.succeed();
  }

  /** The output counts with the input, as in the cascade (answer 1984). */
  @GameTest(template = ARENA)
  public static void theOutputCountsWithTheInput(GameTestHelper helper) {
    DeskShortage shortage = of(helper, with(IRON_FOR_ONE, ONE), with(IRON_FOR_ONE, ONE), TWO);

    helper.assertTrue(shortage.isEmpty(), MISSING);
    helper.succeed();
  }
}
