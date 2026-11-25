package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.CascadeLaunch;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.block.SavedCascade;
import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.menu.CascadeMenuPart;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import io.github.zenndg.cubit.registry.CubitBlocks;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The cascade of an Ultimate desk: it reserves, makes every part and loses nothing (2006-2028). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CascadeGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 600;
  private static final long ENERGY = 1_000_000;
  private static final long COST_OF_THE_CHAIN = 27_500;
  private static final int CHAIN_TICKS = 400;
  private static final int SOME_TICKS = 20;
  private static final int MANY = 64;
  private static final int BESIDE = 2;
  private static final int IRON = 17;
  private static final int THREE = 3;
  private static final int FOUR = 4;
  private static final int SIX = 6;
  private static final int NINE = 9;
  private static final String CASCADE = "Cascade";
  private static final String STEPS = "steps";
  private static final String RECIPE = "recipe";
  private static final String ROBOT = "general_robot";
  private static final String WRONG_CHOICE = "Wrong answer to the choice";
  private static final String GONE = "cubit:gone";
  private static final String WRONG_START = "Wrong answer to the start";
  private static final String NOT_MADE = "The robot should be in the output";
  private static final String NOT_RESERVED = "The materials should leave the input";
  private static final String MOVED = "Nothing should leave the input";
  private static final String WRONG_ENERGY = "Wrong energy after the plan";
  private static final String NOT_RUNNING = "The plan should be running";
  private static final String REFUSED = "The menu should take the click";
  private static final String RUNNING = "The plan should have ended";
  private static final String NOT_PAUSED = "Without energy the plan should pause";
  private static final String LOST = "Items were lost";
  private static final int BASE_ITEMS = 58;
  private static final String WRONG_ITEM = "The desk should show the item of the task";
  private static final String WRONG_PLAN = "The item should tell the plan it keeps";
  private static final String HOLDS = "The desk should keep no items of the plan";

  private CascadeGameTests() {}

  private static EngineeringDeskBlockEntity place(
      GameTestHelper helper, RobotTier tier, long energy) {
    ItemStack item = new ItemStack(CubitBlocks.ENGINEERING_DESK.get());
    item.set(CubitDataComponents.DESK_ENERGY.get(), energy);
    item.set(CubitDataComponents.DESK_TIER.get(), tier);
    GameTestRobots.use(
        helper, GameTestRobots.onlinePlayer(helper), item, EngineeringDeskGameTests.DESK.below());
    return helper.getBlockEntity(EngineeringDeskGameTests.DESK);
  }

  /** A player with an empty recipe book, which never holds a chain (answer 2400). */
  private static ServerPlayer player(GameTestHelper helper) {
    ServerPlayer player = GameTestRobots.onlinePlayer(helper);
    player.resetRecipes(helper.getLevel().getRecipeManager().getRecipes());
    return player;
  }

  /** The base materials of one robot, with the ones of its shared parts (answer 2146). */
  private static void fillRobot(EngineeringDeskBlockEntity desk) {
    List<ItemStack> materials =
        List.of(
            new ItemStack(Items.IRON_INGOT, IRON),
            new ItemStack(Items.OBSIDIAN, THREE),
            new ItemStack(Items.REDSTONE_TORCH, 2),
            new ItemStack(Items.DIAMOND, THREE),
            new ItemStack(Items.PISTON, 2),
            new ItemStack(Items.REDSTONE, NINE),
            new ItemStack(Items.COPPER_INGOT, SIX),
            new ItemStack(Items.GLASS_PANE, SIX),
            new ItemStack(Items.GLASS, FOUR),
            new ItemStack(Items.LAPIS_LAZULI, THREE),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.CHEST),
            new ItemStack(Items.BUCKET));
    for (int slot = 0; slot < materials.size(); slot++) {
      desk.input().setItem(slot, materials.get(slot).copy());
    }
  }

  private static CascadeStart startRobot(GameTestHelper helper, EngineeringDeskBlockEntity desk) {
    return CascadeLaunch.start(desk, player(helper), CubitItems.GENERAL_ROBOT.get(), 1);
  }

  private static int count(Container container, Item item) {
    return container.countItem(item);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aPlanMakesTheWholeChain(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, ENERGY);
    fillRobot(desk);
    helper.assertValueEqual(startRobot(helper, desk), CascadeStart.READY, WRONG_START);
    helper.assertTrue(desk.input().isEmpty(), NOT_RESERVED);
    helper.assertTrue(desk.cascade().isRunning(), NOT_RUNNING);

    helper.runAfterDelay(
        CHAIN_TICKS,
        () -> {
          helper.assertValueEqual(
              count(desk.output(), CubitItems.GENERAL_ROBOT.get()), 1, NOT_MADE);
          helper.assertFalse(desk.cascade().isRunning(), RUNNING);
          helper.assertFalse(desk.cascade().holdsItems(), HOLDS);
          helper.assertValueEqual(desk.energy().amount(), ENERGY - COST_OF_THE_CHAIN, WRONG_ENERGY);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA)
  public static void withoutTheMaterialsNothingLeaves(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, ENERGY);
    fillRobot(desk);
    desk.input().setItem(0, new ItemStack(Items.IRON_INGOT));

    helper.assertValueEqual(startRobot(helper, desk), CascadeStart.MISSING, WRONG_START);
    helper.assertValueEqual(count(desk.input(), Items.OBSIDIAN), THREE, MOVED);
    helper.assertFalse(desk.cascade().holdsItems(), MOVED);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void onlyAnUltimateDeskStartsAPlan(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ELITE, ENERGY);
    fillRobot(desk);

    helper.assertValueEqual(startRobot(helper, desk), CascadeStart.TIER, WRONG_START);
    helper.succeed();
  }

  /** Nothing in the recipe book is needed to choose an item (answer 2400). */
  @GameTest(template = ARENA)
  public static void anEmptyBookChoosesAnItem(GameTestHelper helper) {
    var recipes = helper.getLevel().getRecipeManager();
    var robot = DeskRecipes.find(recipes, id(ROBOT)).orElseThrow();

    helper.assertTrue(CascadeLaunch.mayChoose(player(helper), robot), WRONG_CHOICE);
    helper.succeed();
  }

  private static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  /** Parts ready in the input and in the output are used (answer 2008). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void readyPartsAreReserved(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, ENERGY);
    desk.input().setItem(0, new ItemStack(CubitItems.GENERAL_ROBOT_CASING.get()));
    desk.input().setItem(1, new ItemStack(CubitItems.XP_TANK.get()));
    desk.input().setItem(2, new ItemStack(CubitItems.HARD_DRIVE.get()));
    desk.input().setItem(THREE, new ItemStack(CubitItems.SOLAR_PANEL.get()));
    desk.output().setItem(0, new ItemStack(CubitItems.GENERAL_ROBOT_VISOR.get()));
    desk.output().setItem(1, new ItemStack(CubitItems.GENERAL_ROBOT_TREADS.get()));
    desk.output().setItem(2, new ItemStack(CubitItems.NEURAL_CORE.get()));

    helper.assertValueEqual(startRobot(helper, desk), CascadeStart.READY, WRONG_START);
    helper.assertTrue(desk.input().isEmpty() && desk.output().isEmpty(), NOT_RESERVED);
    helper.assertValueEqual(desk.cascade().run().orElseThrow().totalCrafts(), 1, WRONG_START);
    helper.succeed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void withoutEnergyThePlanPauses(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, 0);
    fillRobot(desk);
    startRobot(helper, desk);

    helper.runAfterDelay(
        SOME_TICKS,
        () -> {
          helper.assertValueEqual(desk.status(), DeskStatus.NO_ENERGY, NOT_PAUSED);
          helper.assertTrue(desk.cascade().isRunning(), NOT_RUNNING);
          helper.succeed();
        });
  }

  /** A full output does not stop the plan: the robot waits for room (answer 2028). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void whatDoesNotFitWaits(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, ENERGY);
    fillRobot(desk);
    startRobot(helper, desk);
    for (int slot = 0; slot < desk.output().getContainerSize(); slot++) {
      desk.output().setItem(slot, new ItemStack(Items.DIRT, MANY));
    }

    helper
        .startSequence()
        .thenExecuteAfter(
            CHAIN_TICKS,
            () -> {
              helper.assertFalse(desk.cascade().isRunning(), RUNNING);
              helper.assertTrue(desk.cascade().holdsItems(), LOST);
              desk.output().setItem(0, ItemStack.EMPTY);
            })
        .thenExecuteAfter(
            SOME_TICKS,
            () -> {
              helper.assertValueEqual(
                  count(desk.output(), CubitItems.GENERAL_ROBOT.get()), 1, NOT_MADE);
              helper.assertFalse(desk.cascade().holdsItems(), HOLDS);
            })
        .thenSucceed();
  }

  /** The item of a broken desk keeps the plan and its items (answer 2010). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theItemKeepsThePlan(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, ENERGY);
    fillRobot(desk);
    startRobot(helper, desk);
    DataComponentMap kept = desk.collectComponents();
    helper.destroyBlock(EngineeringDeskGameTests.DESK);

    ItemStack item = new ItemStack(CubitBlocks.ENGINEERING_DESK.get());
    item.applyComponents(kept);
    GameTestRobots.use(
        helper, GameTestRobots.onlinePlayer(helper), item, EngineeringDeskGameTests.DESK.below());
    EngineeringDeskBlockEntity placed = helper.getBlockEntity(EngineeringDeskGameTests.DESK);
    helper.assertTrue(placed.cascade().isRunning(), NOT_RUNNING);
    helper.runAfterDelay(
        CHAIN_TICKS,
        () -> {
          helper.assertValueEqual(
              count(placed.output(), CubitItems.GENERAL_ROBOT.get()), 1, NOT_MADE);
          helper.succeed();
        });
  }

  /** In Jade and in the hologram, the item of the task, not of the Project tab (answer 2022). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aRunningPlanShowsItsTask(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, 0);
    fillRobot(desk);
    helper.assertTrue(desk.shownResult().isEmpty(), WRONG_ITEM);

    startRobot(helper, desk);

    helper.assertTrue(desk.shownResult().is(CubitItems.GENERAL_ROBOT_CASING.get()), WRONG_ITEM);
    helper.succeed();
  }

  /** What the tooltip of the item reads: the progress and the items it keeps (answer 2010). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theItemTellsItsPlan(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, 0);
    fillRobot(desk);
    startRobot(helper, desk);

    SavedCascade saved = desk.collectComponents().get(CubitDataComponents.DESK_CASCADE.get());

    helper.assertTrue(saved != null && saved.running().isPresent(), WRONG_PLAN);
    helper.assertTrue(saved.item() == CubitItems.GENERAL_ROBOT.get(), WRONG_PLAN);
    helper.assertValueEqual(saved.itemCount(), BASE_ITEMS, WRONG_PLAN);
    helper.succeed();
  }

  /** By the buttons of the screen: zero does nothing, and one makes one (answer 2053). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theButtonsStartAtZero(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, ENERGY);
    fillRobot(desk);
    ServerPlayer player = player(helper);
    player.moveTo(
        helper.absolutePos(EngineeringDeskGameTests.DESK).south(BESIDE).getBottomCenter());
    EngineeringDeskMenu menu = EngineeringDeskMenu.onServer(1, player.getInventory(), desk);
    List<ResourceLocation> catalog =
        DeskRecipes.all(helper.getLevel().getRecipeManager()).stream()
            .map(RecipeHolder::id)
            .toList();
    helper.assertTrue(
        menu.clickMenuButton(player, CascadeMenuPart.choose(catalog.indexOf(id(ROBOT)))), REFUSED);
    helper.assertTrue(menu.clickMenuButton(player, CascadeMenuPart.START), REFUSED);
    helper.assertFalse(desk.cascade().isRunning(), RUNNING);
    helper.assertValueEqual(count(desk.input(), Items.DIAMOND), THREE, MOVED);

    menu.clickMenuButton(player, CascadeMenuPart.MINUS);
    menu.clickMenuButton(player, CascadeMenuPart.PLUS);
    menu.clickMenuButton(player, CascadeMenuPart.START);

    helper.assertTrue(desk.cascade().isRunning(), NOT_RUNNING);
    helper.assertValueEqual(desk.cascade().quantity(), 1, WRONG_START);
    helper.succeed();
  }

  /** A recipe a pack took away ends the plan and gives everything back (answer 2010). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anImpossiblePlanGivesEverythingBack(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ULTIMATE, ENERGY);
    fillRobot(desk);
    startRobot(helper, desk);
    CompoundTag saved = desk.saveWithoutMetadata(helper.getLevel().registryAccess());
    saved
        .getCompound(CASCADE)
        .getList(STEPS, Tag.TAG_COMPOUND)
        .forEach(step -> ((CompoundTag) step).putString(RECIPE, GONE));
    desk.loadWithComponents(saved, helper.getLevel().registryAccess());

    helper.runAfterDelay(
        SOME_TICKS,
        () -> {
          helper.assertFalse(desk.cascade().isRunning(), RUNNING);
          helper.assertValueEqual(count(desk.input(), Items.IRON_INGOT), IRON, LOST);
          helper.assertValueEqual(count(desk.input(), Items.DIAMOND), THREE, LOST);
          helper.assertFalse(desk.cascade().holdsItems(), HOLDS);
          helper.succeed();
        });
  }
}
