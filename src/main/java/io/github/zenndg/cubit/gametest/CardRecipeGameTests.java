package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.item.DeskCategoryCodecs;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The cards made as a USB stick: the casing in the category of the cards, and a card made in the
 * desk from it, the board, the core and the item of the job, also from the output, in a cascade
 * (answers 1540 to 1545 and 2164).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CardRecipeGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int ONE_CRAFT = 130;
  private static final int CORE_SLOT = 3;
  private static final long ENERGY = 50_000;
  private static final String CASING = "ml_card_casing";
  private static final String BOARD = "circuit_board";
  private static final String FISHER = "ml_fisher";
  private static final String FARMER = "ml_farmer";
  private static final String ORGANIZER = "ml_organizer";
  private static final String COOK = "ml_cook";
  private static final String ADVANCEMENT_PREFIX = "recipes/misc/";
  private static final String WRONG_UNLOCK = "Wrong Module to open ";
  private static final String NOT_A_CARD = "Not in the category of the cards: ";
  private static final String NO_CARD = "The desk made no card";
  private static final String PARTS_LEFT = "The desk did not use the parts";

  /** The Module that opens each recipe: the tier of the job, and the casing with the Advanced. */
  private static final Map<String, Supplier<Item>> UNLOCKS =
      Map.of(
          CASING, CubitItems.ADVANCED_MODULE,
          FISHER, CubitItems.ADVANCED_MODULE,
          FARMER, CubitItems.ADVANCED_MODULE,
          ORGANIZER, CubitItems.ADVANCED_MODULE,
          COOK, CubitItems.ADVANCED_MODULE);

  private CardRecipeGameTests() {}

  private static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  @GameTest(template = ARENA)
  public static void theCasingIsInTheCategoryOfTheCards(GameTestHelper helper) {
    for (String path : List.of(CASING, FISHER)) {
      RecipeHolder<?> recipe = helper.getLevel().getRecipeManager().byKey(id(path)).orElseThrow();
      helper.assertTrue(
          DeskCategoryCodecs.of(recipe.value()) == DeskCategory.CARDS, NOT_A_CARD + path);
    }
    helper.succeed();
  }

  /** Each recipe opens with the Module of its tier, and not with a Cubit (answer 1546). */
  @GameTest(template = ARENA)
  public static void eachCardOpensWithTheModuleOfItsTier(GameTestHelper helper) {
    UNLOCKS.forEach((path, module) -> {
      AdvancementHolder unlock =
          helper.getLevel().getServer().getAdvancements().get(id(ADVANCEMENT_PREFIX + path));
      helper.assertTrue(
          unlock != null
              && opensWith(unlock, new ItemStack(module.get()))
              && !opensWith(unlock, new ItemStack(CubitItems.GENERAL_ROBOT.get())),
          WRONG_UNLOCK + path);
    });
    helper.succeed();
  }

  /** The board also goes in the Power Bank Casing, so it opens with a Cubit (answer 2165). */
  @GameTest(template = ARENA)
  public static void theBoardOpensWithACubit(GameTestHelper helper) {
    AdvancementHolder unlock =
        helper.getLevel().getServer().getAdvancements().get(id(ADVANCEMENT_PREFIX + BOARD));
    helper.assertTrue(
        unlock != null && opensWith(unlock, new ItemStack(CubitItems.GENERAL_ROBOT.get())),
        WRONG_UNLOCK + BOARD);
    helper.succeed();
  }

  private static boolean opensWith(AdvancementHolder unlock, ItemStack stack) {
    return unlock.value().criteria().values().stream()
        .map(Criterion::triggerInstance)
        .filter(InventoryChangeTrigger.TriggerInstance.class::isInstance)
        .map(InventoryChangeTrigger.TriggerInstance.class::cast)
        .flatMap(trigger -> trigger.items().stream())
        .anyMatch(predicate -> predicate.test(stack));
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theDeskMakesACardFromItsPartsAndTheRod(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    desk.input().setItem(0, new ItemStack(CubitItems.CARD_CASING.get()));
    desk.input().setItem(1, new ItemStack(CubitItems.CIRCUIT_BOARD.get()));
    desk.input().setItem(2, new ItemStack(Items.FISHING_ROD));
    desk.input().setItem(CORE_SLOT, new ItemStack(CubitItems.NEURAL_CORE.get()));
    order(desk);
    helper.runAfterDelay(ONE_CRAFT, () -> assertCard(helper, desk));
  }

  /** The parts made before wait in the output, and the desk pulls them (answer 1500). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theDeskPullsThePartsFromTheOutput(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, ENERGY);
    desk.output().setItem(0, new ItemStack(CubitItems.CARD_CASING.get()));
    desk.output().setItem(1, new ItemStack(CubitItems.CIRCUIT_BOARD.get()));
    desk.output().setItem(2, new ItemStack(CubitItems.NEURAL_CORE.get()));
    desk.input().setItem(0, new ItemStack(Items.FISHING_ROD));
    order(desk);
    helper.runAfterDelay(ONE_CRAFT, () -> assertCard(helper, desk));
  }

  private static void order(EngineeringDeskBlockEntity desk) {
    desk.choose(id(FISHER));
    desk.adjustOrder(1);
  }

  private static void assertCard(GameTestHelper helper, EngineeringDeskBlockEntity desk) {
    helper.assertTrue(desk.output().countItem(CubitItems.FISHER_CARD.get()) == 1, NO_CARD);
    helper.assertTrue(
        desk.output().countItem(CubitItems.CARD_CASING.get()) == 0
            && desk.output().countItem(CubitItems.CIRCUIT_BOARD.get()) == 0
            && desk.output().countItem(CubitItems.NEURAL_CORE.get()) == 0
            && desk.input().isEmpty(),
        PARTS_LEFT);
    helper.succeed();
  }
}
