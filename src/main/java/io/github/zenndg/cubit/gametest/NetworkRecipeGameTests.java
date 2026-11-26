package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.DeskCategoryCodecs;
import io.github.zenndg.cubit.item.SavedBigStack;
import io.github.zenndg.cubit.registry.CubitItems;
import io.github.zenndg.cubit.registry.CubitRecipes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The parts and the blocks of the Network on the desk, and their tooltips (2282, 2305, 2416). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkRecipeGameTests {
  private static final String EMPTY_TEMPLATE = "empty";
  private static final int GRID_SIZE = 3;
  private static final int AMOUNT = 128;
  private static final int FIRST = 0;
  private static final int FULL_LINES = 3;
  private static final Energy ENERGY = new Energy(400_000);
  private static final String WRONG_RESULT = "Wrong recipe result";
  private static final String WRONG_CATEGORY = "Wrong category of the desk";
  private static final String WRONG_LINES = "Wrong lines in the tooltip";

  private NetworkRecipeGameTests() {}

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theTransceiverIsMadeOfABoardAndAPearl(GameTestHelper helper) {
    List<ItemStack> grid =
        grid(
            Items.AIR,
            Items.ENDER_PEARL,
            Items.AIR,
            Items.COPPER_INGOT,
            CubitItems.CIRCUIT_BOARD.get(),
            Items.COPPER_INGOT,
            Items.AIR,
            Items.REDSTONE,
            Items.AIR);
    assertMakes(helper, CubitItems.TRANSCEIVER.get(), DeskCategory.PARTS, grid);
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theAnchorIsMadeOfObsidianPearlsAndADiamond(GameTestHelper helper) {
    List<ItemStack> grid =
        grid(
            Items.ENDER_PEARL,
            Items.ENDER_PEARL,
            Items.ENDER_PEARL,
            Items.GOLD_INGOT,
            Items.DIAMOND,
            Items.GOLD_INGOT,
            Items.OBSIDIAN,
            Items.OBSIDIAN,
            Items.OBSIDIAN);
    assertMakes(helper, CubitItems.CHUNK_ANCHOR.get(), DeskCategory.PARTS, grid);
  }

  /** Without a shape, so in any order (Delivery 3, detail 18). */
  @GameTest(template = EMPTY_TEMPLATE)
  public static void theServerIsMadeOfAnIronBlockAndTheParts(GameTestHelper helper) {
    List<ItemStack> grid =
        grid(
            CubitItems.DISPLAY_PANEL.get(),
            CubitItems.TRANSCEIVER.get(),
            CubitItems.CHUNK_ANCHOR.get(),
            CubitItems.NEURAL_CORE.get(),
            Items.IRON_BLOCK,
            CubitItems.BASE_POWER_CELL.get(),
            CubitItems.XP_TANK.get(),
            CubitItems.HARD_DRIVE.get(),
            Items.AIR);
    assertMakes(helper, CubitItems.NETWORK_SERVER.get(), DeskCategory.DOCK, grid);
  }

  @GameTest(template = EMPTY_TEMPLATE)
  public static void theRelayIsMadeOfAPanelAndARadio(GameTestHelper helper) {
    List<ItemStack> grid =
        grid(
            CubitItems.XP_TANK.get(),
            CubitItems.BASE_POWER_CELL.get(),
            Items.AIR,
            CubitItems.TRANSCEIVER.get(),
            CubitItems.SOLAR_PANEL.get(),
            Items.AIR,
            Items.AIR,
            Items.AIR,
            Items.AIR);
    assertMakes(helper, CubitItems.SOLAR_RELAY.get(), DeskCategory.DOCK, grid);
  }

  /** Lines only for what it keeps (answer 2416). */
  @GameTest(template = EMPTY_TEMPLATE)
  public static void theTooltipTellsWhatTheItemKeeps(GameTestHelper helper) {
    ItemStack casings = new ItemStack(CubitItems.GENERAL_ROBOT_CASING.get(), AMOUNT);
    ItemStack full =
        NetworkScene.serverItem(RobotTier.ELITE, ENERGY, SavedBigStack.of(FIRST, casings));

    helper.assertValueEqual(lines(helper, full), FULL_LINES, WRONG_LINES);
    helper.assertValueEqual(
        lines(helper, new ItemStack(CubitItems.NETWORK_SERVER.get())), 0, WRONG_LINES);
    helper.assertValueEqual(
        lines(helper, new ItemStack(CubitItems.SOLAR_RELAY.get())), 0, WRONG_LINES);
    helper.succeed();
  }

  private static int lines(GameTestHelper helper, ItemStack stack) {
    List<Component> lines = new ArrayList<>();
    stack
        .getItem()
        .appendHoverText(
            stack, Item.TooltipContext.of(helper.getLevel()), lines, TooltipFlag.NORMAL);
    return lines.size();
  }

  private static void assertMakes(
      GameTestHelper helper, Item expected, DeskCategory category, List<ItemStack> grid) {
    ServerLevel level = helper.getLevel();
    CraftingInput input = CraftingInput.of(GRID_SIZE, GRID_SIZE, grid);
    RecipeHolder<CraftingRecipe> recipe =
        level
            .getRecipeManager()
            .getRecipeFor(CubitRecipes.ENGINEERING_TYPE.get(), input, level)
            .orElseThrow();
    helper.assertTrue(
        recipe.value().assemble(input, level.registryAccess()).is(expected), WRONG_RESULT);
    helper.assertTrue(DeskCategoryCodecs.of(recipe.value()) == category, WRONG_CATEGORY);
    helper.succeed();
  }

  private static List<ItemStack> grid(Item... items) {
    return List.of(items).stream().map(ItemStack::new).toList();
  }
}
