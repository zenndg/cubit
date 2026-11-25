package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * An order holds its recipe, by every way of choosing another one (answers 2472, 2476 and 2480).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskUsabilityGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 200;
  private static final int SOME_TICKS = 20;
  private static final long ENERGY = 50_000;
  private static final int MENU_ID = 1;
  private static final int BESIDE = 2;
  private static final String TABLET = "tablet";
  private static final String CHANGED = "An order should hold its recipe";
  private static final String NOT_FREE = "Without an order, any recipe can be chosen";
  private static final String NO_PROGRESS = "The craft should go on";
  private static final String WRONG_STATUS = "Wrong status of the desk";

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
}
