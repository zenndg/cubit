package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.DeskUnlocks;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Any recipe can be seen, and an order starts only with the recipe released and the materials of
 * one in the desk. A release is never lost (answers 1969, 1974 and 1982 to 1991).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskOrderGateGameTests {
  private static final String ARENA = "arena";
  private static final long ENERGY = 50_000;
  private static final int MENU_ID = 1;
  private static final int BESIDE = 2;
  private static final int PLENTY = 8;
  private static final String TABLET = "tablet";
  private static final String ROBOT = "general_robot";
  private static final String BASE_POWER_BANK = "base_power_bank";
  private static final String PACK_RECIPE = "recipe_of_a_pack";
  private static final String TABLET_RELEASE = "recipes/tools/tablet";
  private static final String HAS_ROBOT = "has_robot";
  private static final String STARTED = "The order should not start";
  private static final String NOT_STARTED = "The order should start";
  private static final String NOT_CHOSEN = "Any recipe can be chosen";
  private static final String NOT_RELEASED = "The recipe should be released";
  private static final String RELEASED = "The recipe should not be released yet";
  private static final String NOT_OPENED = "The recipe should tell what releases it";

  private DeskOrderGateGameTests() {}

  private static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  private static RecipeHolder<CraftingRecipe> recipe(GameTestHelper helper, ResourceLocation id) {
    return DeskRecipes.find(helper.getLevel().getRecipeManager(), id).orElseThrow();
  }

  private static ServerPlayer player(GameTestHelper helper) {
    ServerPlayer player = GameTestRobots.onlinePlayer(helper);
    player.moveTo(
        helper.absolutePos(EngineeringDeskGameTests.DESK).south(BESIDE).getBottomCenter());
    player.resetRecipes(helper.getLevel().getRecipeManager().getRecipes());
    return player;
  }

  private static EngineeringDeskMenu open(ServerPlayer player, EngineeringDeskBlockEntity desk) {
    return EngineeringDeskMenu.onServer(MENU_ID, player.getInventory(), desk);
  }

  private static int indexOf(GameTestHelper helper, String path) {
    return DeskRecipes.all(helper.getLevel().getRecipeManager()).stream()
        .map(RecipeHolder::id)
        .toList()
        .indexOf(id(path));
  }

  private static void fillTablet(EngineeringDeskBlockEntity desk) {
    List<ItemStack> materials =
        List.of(
            new ItemStack(CubitItems.DISPLAY_PANEL.get(), PLENTY),
            new ItemStack(Items.REDSTONE, PLENTY),
            new ItemStack(Items.QUARTZ, PLENTY));
    for (int slot = 0; slot < materials.size(); slot++) {
      desk.input().setItem(slot, materials.get(slot));
    }
  }

  @GameTest(template = ARENA)
  public static void withoutMaterialsTheOrderDoesNotStart(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    player.awardRecipes(List.of(recipe(helper, EngineeringDeskGameTests.TOOLBOX)));
    EngineeringDeskMenu menu = open(player, desk);
    desk.choose(EngineeringDeskGameTests.TOOLBOX);

    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertFalse(desk.order().isActive(), STARTED);
    menu.clickMenuButton(player, DeskControl.PLUS_SHIFT);
    helper.assertFalse(desk.order().isActive(), STARTED);

    EngineeringDeskGameTests.fillToolbox(desk, 1);
    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertTrue(desk.order().isActive(), NOT_STARTED);
    helper.succeed();
  }

  /** The order on its way grows without materials, because only its start is held (1984, 1985). */
  @GameTest(template = ARENA)
  public static void anActiveOrderGrowsWithoutMaterials(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    player.awardRecipes(List.of(recipe(helper, EngineeringDeskGameTests.TOOLBOX)));
    EngineeringDeskMenu menu = open(player, desk);
    desk.choose(EngineeringDeskGameTests.TOOLBOX);
    EngineeringDeskGameTests.fillToolbox(desk, 1);
    menu.clickMenuButton(player, DeskControl.PLUS);
    desk.input().clearContent();

    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertValueEqual(desk.order().total(), 2, NOT_STARTED);
    helper.succeed();
  }

  /** The parts ready in the output count, as in the cascade (answers 1502 and 1984). */
  @GameTest(template = ARENA)
  public static void thePartsInTheOutputCount(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    player.awardRecipes(List.of(recipe(helper, id(ROBOT))));
    EngineeringDeskMenu menu = open(player, desk);
    desk.choose(id(ROBOT));
    desk.input().setItem(0, new ItemStack(CubitItems.GENERAL_ROBOT_CASING.get()));
    desk.input().setItem(1, new ItemStack(CubitItems.GENERAL_ROBOT_VISOR.get()));
    desk.input().setItem(2, new ItemStack(CubitItems.XP_TANK.get()));
    EngineeringDeskGameTests.addDriveAndPanel(desk);
    desk.output().setItem(0, new ItemStack(CubitItems.GENERAL_ROBOT_TREADS.get()));
    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertFalse(desk.order().isActive(), STARTED);

    desk.output().setItem(1, new ItemStack(CubitItems.NEURAL_CORE.get()));
    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertTrue(desk.order().isActive(), NOT_STARTED);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void aLockedRecipeIsChosenButNotAsked(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    fillTablet(desk);

    menu.clickMenuButton(player, DeskControl.choose(indexOf(helper, TABLET)));
    helper.assertTrue(desk.recipeId().map(id(TABLET)::equals).orElse(false), NOT_CHOSEN);
    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertFalse(desk.order().isActive(), STARTED);

    player.awardRecipes(List.of(recipe(helper, id(TABLET))));
    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertTrue(desk.order().isActive(), NOT_STARTED);
    helper.succeed();
  }

  /**
   * The advancement is done and the book lost the recipe: opening the desk gives it back (1974).
   */
  @GameTest(template = ARENA)
  public static void openingTheDeskRepairsTheBook(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    AdvancementHolder release =
        helper.getLevel().getServer().getAdvancements().get(id(TABLET_RELEASE));
    player.getAdvancements().award(release, HAS_ROBOT);
    RecipeHolder<CraftingRecipe> tablet = recipe(helper, id(TABLET));
    player.resetRecipes(List.of(tablet));
    helper.assertFalse(DeskRecipes.unlocked(player, tablet), RELEASED);

    open(player, desk);
    helper.assertTrue(DeskRecipes.unlocked(player, tablet), NOT_RELEASED);
    helper.succeed();
  }

  /** An item inside the desk counts for who opens it, without leaving it (answer 1969). */
  @GameTest(template = ARENA)
  public static void anItemInsideTheDeskReleases(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    RecipeHolder<CraftingRecipe> bank = recipe(helper, id(BASE_POWER_BANK));
    open(player, desk);
    helper.assertFalse(DeskRecipes.unlocked(player, bank), RELEASED);

    desk.output().setItem(0, new ItemStack(CubitItems.BASE_POWER_CELL.get()));
    open(player, desk);
    helper.assertTrue(DeskRecipes.unlocked(player, bank), NOT_RELEASED);
    helper.succeed();
  }

  /** A recipe that nothing releases, as one of a pack with a new id, is never held (1974). */
  @GameTest(template = ARENA)
  public static void aRecipeWithoutAReleaseIsNeverHeld(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    RecipeHolder<CraftingRecipe> tablet = recipe(helper, id(TABLET));
    RecipeHolder<CraftingRecipe> ofThePack = new RecipeHolder<>(id(PACK_RECIPE), tablet.value());

    DeskUnlocks unlocks = DeskUnlocks.of(player, List.of(ofThePack, tablet));
    unlocks.check(desk.input(), desk.output());
    helper.assertTrue(DeskRecipes.unlocked(player, ofThePack), NOT_RELEASED);
    helper.assertFalse(DeskRecipes.unlocked(player, tablet), RELEASED);
    helper.assertTrue(
        unlocks
            .openers()
            .getOrDefault(id(TABLET), List.of())
            .contains(CubitItems.GENERAL_ROBOT.get()),
        NOT_OPENED);
    helper.succeed();
  }
}
