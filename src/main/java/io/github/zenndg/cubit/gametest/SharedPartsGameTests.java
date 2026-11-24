package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.item.DeskCategoryCodecs;
import io.github.zenndg.cubit.registry.CubitItems;
import io.github.zenndg.cubit.registry.CubitRecipes;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The parts many items share, with the grids of _docs/itens.md (2136, 2150 and 2168). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SharedPartsGameTests {
  private static final String EMPTY_TEMPLATE = "empty";
  private static final int GRID_SIZE = 3;
  private static final int PART_STACK_SIZE = 64;
  private static final String WRONG_RESULT = "Wrong recipe result";
  private static final String WRONG_STACK = "A part should stack up to 64";
  private static final String WRONG_CATEGORY = "Not in the category of the shared parts: ";
  private static final String LEFT_OVER = "The bucket should be used up";

  private SharedPartsGameTests() {}

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theXpTankRecipeMakesATank(GameTestHelper helper) {
    assertMakes(helper, CubitItems.XP_TANK.get(), tankGrid());
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theHardDriveRecipeMakesADrive(GameTestHelper helper) {
    assertMakes(
        helper,
        CubitItems.HARD_DRIVE.get(),
        grid(
            Items.IRON_INGOT,
            Items.CHEST,
            Items.IRON_INGOT,
            Items.REDSTONE,
            Items.GOLD_INGOT,
            Items.COPPER_INGOT,
            Items.AIR,
            Items.AIR,
            Items.AIR));
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theSolarPanelRecipeMakesAPanel(GameTestHelper helper) {
    assertMakes(
        helper,
        CubitItems.SOLAR_PANEL.get(),
        grid(
            Items.GLASS_PANE,
            Items.GLASS_PANE,
            Items.GLASS_PANE,
            Items.LAPIS_LAZULI,
            Items.LAPIS_LAZULI,
            Items.LAPIS_LAZULI,
            Items.COPPER_INGOT,
            Items.REDSTONE,
            Items.COPPER_INGOT));
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theDisplayRecipeMakesADisplay(GameTestHelper helper) {
    assertMakes(
        helper,
        CubitItems.DISPLAY_PANEL.get(),
        grid(
            Items.GLASS_PANE,
            Items.GLASS_PANE,
            Items.GLASS_PANE,
            Items.IRON_INGOT,
            Items.DIAMOND,
            Items.IRON_INGOT,
            Items.REDSTONE,
            Items.OBSIDIAN,
            Items.COPPER_INGOT));
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theServoRecipeMakesAServo(GameTestHelper helper) {
    assertMakes(
        helper,
        CubitItems.SERVO_MOTOR.get(),
        grid(
            Items.AIR,
            Items.COPPER_INGOT,
            Items.AIR,
            Items.IRON_INGOT,
            Items.PISTON,
            Items.IRON_INGOT,
            Items.AIR,
            Items.REDSTONE,
            Items.AIR));
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theCoilRecipeMakesACoil(GameTestHelper helper) {
    assertMakes(
        helper,
        CubitItems.INDUCTION_COIL.get(),
        grid(
            Items.PISTON,
            Items.COPPER_INGOT,
            Items.PISTON,
            Items.IRON_INGOT,
            Items.REDSTONE,
            Items.IRON_INGOT,
            Items.AIR,
            Items.AIR,
            Items.AIR));
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theBoardRecipeMakesABoard(GameTestHelper helper) {
    assertMakes(
        helper,
        CubitItems.CIRCUIT_BOARD.get(),
        grid(
            Items.EMERALD,
            Items.COMPARATOR,
            Items.EMERALD,
            Items.GOLD_INGOT,
            Items.GOLD_INGOT,
            Items.GOLD_INGOT,
            Items.AIR,
            Items.AIR,
            Items.AIR));
  }

  /** The core and the board serve many items too, by the IDs of their names (2166 and 2167). */
  @GameTest(template = EMPTY_TEMPLATE)
  public static void theSharedPartsAreInTheirCategory(GameTestHelper helper) {
    for (Item part : parts()) {
      ResourceLocation id = BuiltInRegistries.ITEM.getKey(part);
      RecipeHolder<?> recipe = helper.getLevel().getRecipeManager().byKey(id).orElseThrow();
      helper.assertTrue(
          DeskCategoryCodecs.of(recipe.value()) == DeskCategory.PARTS, WRONG_CATEGORY + id);
    }
    helper.succeed();
  }

  /** Nothing comes back from a part, not even an empty bucket (detail 4 of the concept). */
  @GameTest(template = EMPTY_TEMPLATE)
  public static void theBucketOfTheTankIsUsedUp(GameTestHelper helper) {
    CraftingInput input = CraftingInput.of(GRID_SIZE, GRID_SIZE, tankGrid());
    CraftingRecipe recipe =
        helper
            .getLevel()
            .getRecipeManager()
            .getRecipeFor(CubitRecipes.ENGINEERING_TYPE.get(), input, helper.getLevel())
            .orElseThrow()
            .value();
    helper.assertTrue(
        recipe.getRemainingItems(input).stream().allMatch(ItemStack::isEmpty), LEFT_OVER);
    helper.succeed();
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theSharedPartsStackUpTo64(GameTestHelper helper) {
    for (Item part : parts()) {
      helper.assertValueEqual(new ItemStack(part).getMaxStackSize(), PART_STACK_SIZE, WRONG_STACK);
    }
    helper.succeed();
  }

  private static List<Item> parts() {
    return List.of(
        CubitItems.XP_TANK.get(),
        CubitItems.HARD_DRIVE.get(),
        CubitItems.SOLAR_PANEL.get(),
        CubitItems.DISPLAY_PANEL.get(),
        CubitItems.SERVO_MOTOR.get(),
        CubitItems.INDUCTION_COIL.get(),
        CubitItems.NEURAL_CORE.get(),
        CubitItems.CIRCUIT_BOARD.get());
  }

  private static List<ItemStack> tankGrid() {
    return grid(
        Items.GLASS,
        Items.IRON_INGOT,
        Items.GLASS,
        Items.GLASS,
        Items.BUCKET,
        Items.GLASS,
        Items.AIR,
        Items.IRON_INGOT,
        Items.AIR);
  }

  private static void assertMakes(GameTestHelper helper, Item expected, List<ItemStack> grid) {
    ServerLevel level = helper.getLevel();
    CraftingInput input = CraftingInput.of(GRID_SIZE, GRID_SIZE, grid);
    ItemStack result =
        level
            .getRecipeManager()
            .getRecipeFor(CubitRecipes.ENGINEERING_TYPE.get(), input, level)
            .map(recipe -> recipe.value().assemble(input, level.registryAccess()))
            .orElse(ItemStack.EMPTY);
    helper.assertTrue(result.is(expected), WRONG_RESULT);
    helper.succeed();
  }

  private static List<ItemStack> grid(Item... items) {
    return List.of(items).stream().map(ItemStack::new).toList();
  }
}
