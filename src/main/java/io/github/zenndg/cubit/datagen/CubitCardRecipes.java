package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

/**
 * The cards of Machine Learning, made as a USB stick: a plastic casing and a circuit board, which
 * every card shares, and the item of the job, in a cascade of the desk (answers 1540 to 1545). The
 * Advanced Module opens the two parts, and each card opens with the Module of the lowest tier that
 * takes its job (answers 1145 and 1546).
 */
final class CubitCardRecipes {
  private static final String CASING_TOP_ROW = "QLQ";
  private static final String CASING_MIDDLE_ROW = "Q Q";
  private static final String CASING_BOTTOM_ROW = " L ";
  private static final String BOARD_TOP_ROW = "EEE";
  private static final String BOARD_MIDDLE_ROW = "ECE";
  private static final String BOARD_BOTTOM_ROW = "OOO";
  private static final char QUARTZ = 'Q';
  private static final char LAPIS = 'L';
  private static final char EMERALD = 'E';
  private static final char COMPARATOR = 'C';
  private static final char GOLD = 'O';
  private static final String HAS_PREFIX = "has_";

  private CubitCardRecipes() {}

  static void save(RecipeOutput output) {
    casing(output);
    board(output);
    card(output, CubitItems.FISHER_CARD.get(), Items.FISHING_ROD, CubitItems.ADVANCED_MODULE.get());
    card(output, CubitItems.FARMER_CARD.get(), Items.IRON_HOE, CubitItems.ADVANCED_MODULE.get());
    card(output, CubitItems.ORGANIZER_CARD.get(), Items.HOPPER, CubitItems.ELITE_MODULE.get());
    card(output, CubitItems.COOK_CARD.get(), Items.SMOKER, CubitItems.ELITE_MODULE.get());
  }

  /** The plastic shell: quartz with a stripe of lapis (answer 1542). */
  private static void casing(RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CubitItems.CARD_CASING.get())
        .pattern(CASING_TOP_ROW)
        .pattern(CASING_MIDDLE_ROW)
        .pattern(CASING_BOTTOM_ROW)
        .define(QUARTZ, Items.QUARTZ)
        .define(LAPIS, Items.LAPIS_LAZULI)
        .unlockedBy(criterion(advanced()), has(advanced()))
        .save(output);
  }

  /**
   * The green board: emerald for the board, the comparator as the chip and gold contacts (1547).
   */
  private static void board(RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CubitItems.CIRCUIT_BOARD.get())
        .pattern(BOARD_TOP_ROW)
        .pattern(BOARD_MIDDLE_ROW)
        .pattern(BOARD_BOTTOM_ROW)
        .define(EMERALD, Items.EMERALD)
        .define(COMPARATOR, Items.COMPARATOR)
        .define(GOLD, Items.GOLD_INGOT)
        .unlockedBy(criterion(advanced()), has(advanced()))
        .save(output);
  }

  /** The casing, the board and the item of the job, in any place of the grid (answer 1544). */
  private static void card(RecipeOutput output, ItemLike card, ItemLike job, ItemLike module) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, card)
        .requires(CubitItems.CARD_CASING.get())
        .requires(CubitItems.CIRCUIT_BOARD.get())
        .requires(job)
        .unlockedBy(criterion(module), has(module))
        .save(output);
  }

  private static ItemLike advanced() {
    return CubitItems.ADVANCED_MODULE.get();
  }

  private static String criterion(ItemLike item) {
    return HAS_PREFIX + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
  }

  private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(item);
  }
}
