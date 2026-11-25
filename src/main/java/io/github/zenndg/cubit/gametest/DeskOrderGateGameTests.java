package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.menu.DeskControl;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Any recipe can be seen, and an order starts with the materials of one in the desk, also with an
 * empty recipe book (answers 1982 to 1988 and 2400).
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
  private static final String STARTED = "The order should not start";
  private static final String NOT_STARTED = "The order should start";
  private static final String NOT_CHOSEN = "Any recipe can be chosen";

  private DeskOrderGateGameTests() {}

  private static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  /** A player with an empty recipe book, as some mods leave it. */
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

  /** Nothing in the recipe book is needed: the materials are enough (answer 2400). */
  @GameTest(template = ARENA)
  public static void anEmptyBookStartsAnOrder(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    ServerPlayer player = player(helper);
    EngineeringDeskMenu menu = open(player, desk);
    fillTablet(desk);

    menu.clickMenuButton(player, DeskControl.choose(indexOf(helper, TABLET)));
    helper.assertTrue(desk.recipeId().map(id(TABLET)::equals).orElse(false), NOT_CHOSEN);
    menu.clickMenuButton(player, DeskControl.PLUS);
    helper.assertTrue(desk.order().isActive(), NOT_STARTED);
    helper.succeed();
  }
}
