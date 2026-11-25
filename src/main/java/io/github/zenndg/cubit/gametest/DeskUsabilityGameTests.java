package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * An order holds its recipe, by every way of choosing another one, and what is ready is taken
 * beside the grid (answers 2470 to 2481).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskUsabilityGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 200;
  private static final int SOME_TICKS = 20;
  private static final long ENERGY = 50_000;
  private static final long CASCADE_ENERGY = 1_000_000;
  private static final int MENU_ID = 1;
  private static final int OTHER_MENU_ID = 2;
  private static final int BESIDE = 2;
  private static final int PLENTY = 8;
  private static final String TABLET = "tablet";
  private static final String ROBOT = "general_robot";
  private static final String DRIVE = "hard_drive";
  private static final String NAME = "Bolt";
  private static final String CHANGED = "An order should hold its recipe";
  private static final String NOT_FREE = "Without an order, any recipe can be chosen";
  private static final String NO_PROGRESS = "The craft should go on";
  private static final String WRONG_STATUS = "Wrong status of the desk";
  private static final String WRONG_CURSOR = "Wrong items in the cursor";
  private static final String WRONG_OUTPUT = "Wrong items in the output";
  private static final String WRONG_INVENTORY = "Wrong items in the inventory";
  private static final String WRONG_START = "Wrong answer to the start";

  private DeskUsabilityGameTests() {}

  private static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  private static ServerPlayer player(GameTestHelper helper) {
    ServerPlayer player = GameTestRobots.onlinePlayer(helper);
    player.moveTo(
        helper.absolutePos(EngineeringDeskGameTests.DESK).south(BESIDE).getBottomCenter());
    return player;
  }

  private static EngineeringDeskMenu open(
      int menuId, ServerPlayer player, EngineeringDeskBlockEntity desk) {
    EngineeringDeskMenu menu = EngineeringDeskMenu.onServer(menuId, player.getInventory(), desk);
    player.containerMenu = menu;
    return menu;
  }

  private static EngineeringDeskMenu open(ServerPlayer player, EngineeringDeskBlockEntity desk) {
    return open(MENU_ID, player, desk);
  }

  private static int choose(GameTestHelper helper, String path) {
    return DeskControl.choose(
        DeskRecipes.all(helper.getLevel().getRecipeManager()).stream()
            .map(RecipeHolder::id)
            .toList()
            .indexOf(id(path)));
  }

  private static void orderToolbox(EngineeringDeskBlockEntity desk) {
    desk.choose(EngineeringDeskGameTests.TOOLBOX);
    EngineeringDeskGameTests.fillToolbox(desk, 1);
    desk.adjustOrder(1);
  }

  private static boolean makes(EngineeringDeskBlockEntity desk, ResourceLocation recipe) {
    return desk.recipeId().map(recipe::equals).orElse(false);
  }

  /** The catalog and JEI send the same click. The tab is only a choice of the screen (2480). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theCatalogAndTheTabsNeverCancelAnOrder(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    orderToolbox(desk);
    helper.runAfterDelay(
        SOME_TICKS,
        () -> {
          int before = desk.progressPercent();
          menu.showStorage(true);
          menu.showStorage(false);
          menu.clickMenuButton(player, choose(helper, TABLET));

          helper.assertTrue(makes(desk, EngineeringDeskGameTests.TOOLBOX), CHANGED);
          helper.assertTrue(desk.order().isActive(), CHANGED);
          helper.assertTrue(before > 0 && desk.progressPercent() == before, NO_PROGRESS);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA)
  public static void marksNeverChangeAnOrder(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    orderToolbox(desk);
    menu.setCarried(new ItemStack(CubitItems.DISPLAY_PANEL.get()));
    menu.clickMenuButton(player, DeskControl.mark(0));

    helper.assertTrue(makes(desk, EngineeringDeskGameTests.TOOLBOX), CHANGED);
    helper.assertTrue(menu.marks().get(0).isEmpty(), CHANGED);
    helper.succeed();
  }

  /** A paused order holds too, and canceling it frees the catalog at once (answer 2476). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aPausedOrderHoldsUntilItIsCanceled(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, 0);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    orderToolbox(desk);
    helper.runAfterDelay(
        SOME_TICKS,
        () -> {
          helper.assertTrue(desk.status() == DeskStatus.NO_ENERGY, WRONG_STATUS);
          menu.clickMenuButton(player, choose(helper, TABLET));
          helper.assertTrue(makes(desk, EngineeringDeskGameTests.TOOLBOX), CHANGED);

          menu.clickMenuButton(player, DeskControl.CANCEL);
          menu.clickMenuButton(player, choose(helper, TABLET));
          helper.assertTrue(makes(desk, id(TABLET)), NOT_FREE);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA)
  public static void theAmountStillChangesWithAnOrder(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    orderToolbox(desk);
    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertValueEqual(desk.order().total(), 2, CHANGED);
    menu.clickMenuButton(player, DeskControl.MINUS);
    helper.assertValueEqual(desk.order().total(), 1, CHANGED);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void aClickTakesUpToOneStack(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    ItemStack drives = new ItemStack(CubitItems.HARD_DRIVE.get());
    int stack = drives.getMaxStackSize();
    desk.choose(id(DRIVE));
    desk.output().setItem(0, drives.copyWithCount(stack));
    desk.output().setItem(2, drives.copyWithCount(2));
    desk.output().setItem(1, new ItemStack(Items.BUCKET));

    menu.clickMenuButton(player, DeskControl.TAKE);
    helper.assertValueEqual(menu.getCarried().getCount(), stack, WRONG_CURSOR);
    menu.clickMenuButton(player, DeskControl.TAKE);
    helper.assertValueEqual(menu.getCarried().getCount(), stack, WRONG_CURSOR);
    helper.assertValueEqual(desk.output().countItem(drives.getItem()), 2, WRONG_OUTPUT);
    helper.assertValueEqual(desk.output().countItem(Items.BUCKET), 1, WRONG_OUTPUT);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void aClickAddsToTheSameItemAndLeavesAnother(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    desk.choose(id(DRIVE));
    desk.output().setItem(0, new ItemStack(CubitItems.HARD_DRIVE.get(), 2));

    menu.setCarried(new ItemStack(Items.BUCKET));
    menu.clickMenuButton(player, DeskControl.TAKE);
    helper.assertTrue(menu.getCarried().is(Items.BUCKET), WRONG_CURSOR);
    helper.assertValueEqual(desk.output().countItem(CubitItems.HARD_DRIVE.get()), 2, WRONG_OUTPUT);

    menu.setCarried(new ItemStack(CubitItems.HARD_DRIVE.get()));
    menu.clickMenuButton(player, DeskControl.TAKE);
    helper.assertValueEqual(menu.getCarried().getCount(), 2 + 1, WRONG_CURSOR);
    helper.assertTrue(desk.output().isEmpty(), WRONG_OUTPUT);
    helper.succeed();
  }

  /** A repaired Cubit comes out with its own data, because it is the item of the output. */
  @GameTest(template = ARENA)
  public static void theItemKeepsItsData(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    ItemStack robot = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    robot.set(DataComponents.CUSTOM_NAME, Component.literal(NAME));
    desk.choose(id(ROBOT));
    desk.output().setItem(0, robot.copy());
    desk.output().setItem(1, new ItemStack(CubitItems.GENERAL_ROBOT.get()));

    menu.clickMenuButton(player, DeskControl.TAKE);
    helper.assertTrue(ItemStack.matches(menu.getCarried(), robot), WRONG_CURSOR);
    helper.assertValueEqual(desk.output().countItem(robot.getItem()), 1, WRONG_OUTPUT);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void shiftSendsAllThatFitsToTheInventory(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    desk.choose(id(ROBOT));
    for (int slot = 0; slot < player.getInventory().items.size() - 1; slot++) {
      player.getInventory().setItem(slot, new ItemStack(Items.DIRT));
    }
    desk.output().setItem(0, new ItemStack(CubitItems.GENERAL_ROBOT.get()));
    desk.output().setItem(1, new ItemStack(CubitItems.GENERAL_ROBOT.get()));
    desk.output().setItem(2, new ItemStack(Items.BUCKET));

    menu.clickMenuButton(player, DeskControl.TAKE_ALL);
    helper.assertValueEqual(
        player.getInventory().countItem(CubitItems.GENERAL_ROBOT.get()), 1, WRONG_INVENTORY);
    helper.assertValueEqual(
        desk.output().countItem(CubitItems.GENERAL_ROBOT.get()), 1, WRONG_OUTPUT);
    helper.assertValueEqual(desk.output().countItem(Items.BUCKET), 1, WRONG_OUTPUT);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void twoPlayersNeverTakeTheSameItem(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer first = player(helper);
    ServerPlayer second = player(helper);
    EngineeringDeskMenu firstMenu = open(first, desk);
    EngineeringDeskMenu secondMenu = open(OTHER_MENU_ID, second, desk);
    desk.choose(id(ROBOT));
    desk.output().setItem(0, new ItemStack(CubitItems.GENERAL_ROBOT.get()));

    firstMenu.clickMenuButton(first, DeskControl.TAKE);
    secondMenu.clickMenuButton(second, DeskControl.TAKE);
    secondMenu.clickMenuButton(second, DeskControl.TAKE_ALL);
    helper.assertValueEqual(firstMenu.getCarried().getCount(), 1, WRONG_CURSOR);
    helper.assertTrue(secondMenu.getCarried().isEmpty(), WRONG_CURSOR);
    helper.assertTrue(desk.output().isEmpty(), WRONG_OUTPUT);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void withoutARecipeNothingIsTaken(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    desk.output().setItem(0, new ItemStack(CubitItems.TOOLBOX.get(), 1));

    menu.clickMenuButton(player, DeskControl.TAKE);
    menu.clickMenuButton(player, DeskControl.TAKE_ALL);
    helper.assertTrue(menu.getCarried().isEmpty(), WRONG_CURSOR);
    helper.assertValueEqual(desk.output().countItem(CubitItems.TOOLBOX.get()), 1, WRONG_OUTPUT);
    helper.succeed();
  }

  /** A plan on its way locks the result, as it locks the storage (answers 2015 and 2479). */
  @GameTest(template = ARENA)
  public static void aPlanLocksTheResult(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk =
        CascadeGameTests.place(helper, RobotTier.ULTIMATE, CASCADE_ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    CascadeGameTests.fillRobot(desk);
    desk.choose(EngineeringDeskGameTests.TOOLBOX);
    desk.output().setItem(PLENTY, new ItemStack(CubitItems.TOOLBOX.get()));
    helper.assertValueEqual(
        CascadeGameTests.startRobot(helper, desk), CascadeStart.READY, WRONG_START);

    menu.clickMenuButton(player, DeskControl.TAKE);
    menu.clickMenuButton(player, DeskControl.TAKE_ALL);
    helper.assertTrue(menu.getCarried().isEmpty(), WRONG_CURSOR);
    helper.assertValueEqual(desk.output().countItem(CubitItems.TOOLBOX.get()), 1, WRONG_OUTPUT);
    helper.succeed();
  }
}
