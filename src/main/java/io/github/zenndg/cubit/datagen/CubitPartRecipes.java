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
 * The parts of the Cubit, only from the Overworld, and of the Power Banks, with the grids of
 * _docs/itens.md (answers 1343 to 1350).
 */
final class CubitPartRecipes {
  private static final String CASING_TOP_ROW = "TIT";
  private static final String CASING_MIDDLE_ROW = "I I";
  private static final String CASING_BOTTOM_ROW = "OIO";
  private static final String TREADS_TOP_ROW = "III";
  private static final String TREADS_BOTTOM_ROW = "M M";
  private static final String BANK_CASING_TOP_ROW = "QBQ";
  private static final String BANK_CASING_MIDDLE_ROW = "Q Q";
  private static final String BANK_CASING_BOTTOM_ROW = "QNQ";
  private static final String CROSS_EDGE_ROW = " E ";
  private static final String CROSS_MIDDLE_ROW = "SCS";
  private static final char IRON = 'I';
  private static final char OBSIDIAN = 'O';
  private static final char TORCH = 'T';
  private static final char MOTOR = 'M';
  private static final char QUARTZ = 'Q';
  private static final char BOARD = 'B';
  private static final char COIL = 'N';
  private static final char EDGE = 'E';
  private static final char SIDE = 'S';
  private static final char CENTER = 'C';
  static final String HAS_ROBOT = "has_robot";
  private static final String HAS_PREFIX = "has_";

  private CubitPartRecipes() {}

  static void save(RecipeOutput output) {
    robotParts(output);
    powerBankParts(output);
  }

  /** The parts only the Cubit has are open from the start, the first to make (answer 1496). */
  private static void robotParts(RecipeOutput output) {
    fromStart(CubitItems.GENERAL_ROBOT_CASING.get())
        .pattern(CASING_TOP_ROW)
        .pattern(CASING_MIDDLE_ROW)
        .pattern(CASING_BOTTOM_ROW)
        .define(TORCH, Items.REDSTONE_TORCH)
        .define(IRON, Items.IRON_INGOT)
        .define(OBSIDIAN, Items.OBSIDIAN)
        .save(output);
    visor(output);
    fromStart(CubitItems.GENERAL_ROBOT_TREADS.get())
        .pattern(TREADS_TOP_ROW)
        .pattern(TREADS_BOTTOM_ROW)
        .define(IRON, Items.IRON_INGOT)
        .define(MOTOR, CubitItems.SERVO_MOTOR.get())
        .save(output);
  }

  /** The display and a diamond, without a shape (answer 2142). */
  private static void visor(RecipeOutput output) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CubitItems.GENERAL_ROBOT_VISOR.get())
        .requires(CubitItems.DISPLAY_PANEL.get())
        .requires(Items.DIAMOND)
        .unlockedBy(DeskRecipeData.FROM_START, DeskRecipeData.fromStart())
        .save(output);
  }

  /**
   * The casing, with the board and the coil, and the Base cell unlock with a Cubit, and each next
   * cell with the previous bank (answers 1350 and 2169).
   */
  private static void powerBankParts(RecipeOutput output) {
    ItemLike robot = CubitItems.GENERAL_ROBOT.get();
    shaped(CubitItems.POWER_BANK_CASING.get(), HAS_ROBOT, robot)
        .pattern(BANK_CASING_TOP_ROW)
        .pattern(BANK_CASING_MIDDLE_ROW)
        .pattern(BANK_CASING_BOTTOM_ROW)
        .define(QUARTZ, Items.QUARTZ)
        .define(BOARD, CubitItems.CIRCUIT_BOARD.get())
        .define(COIL, CubitItems.INDUCTION_COIL.get())
        .save(output);
    cells(output, robot);
  }

  /** The cells, each one with the previous in the center (answer 1354). */
  private static void cells(RecipeOutput output, ItemLike robot) {
    cell(
            CubitItems.BASE_POWER_CELL.get(),
            Items.COPPER_INGOT,
            Items.REDSTONE,
            Items.REDSTONE_BLOCK,
            robot)
        .save(output);
    cell(
            CubitItems.ADVANCED_POWER_CELL.get(),
            Items.LAPIS_LAZULI,
            Items.GOLD_INGOT,
            CubitItems.BASE_POWER_CELL.get(),
            CubitItems.BASE_POWER_BANK.get())
        .save(output);
    cell(
            CubitItems.ELITE_POWER_CELL.get(),
            Items.PURPLE_DYE,
            Items.DIAMOND,
            CubitItems.ADVANCED_POWER_CELL.get(),
            CubitItems.ADVANCED_POWER_BANK.get())
        .save(output);
    cell(
            CubitItems.ULTIMATE_POWER_CELL.get(),
            Items.NETHERITE_INGOT,
            Items.BLAZE_ROD,
            CubitItems.ELITE_POWER_CELL.get(),
            CubitItems.ELITE_POWER_BANK.get())
        .save(output);
  }

  /** A cell is a cross, with the previous cell in the center (answer 1354). */
  private static ShapedRecipeBuilder cell(
      ItemLike result, ItemLike edge, ItemLike side, ItemLike center, ItemLike unlock) {
    String criterion = HAS_PREFIX + BuiltInRegistries.ITEM.getKey(unlock.asItem()).getPath();
    return cross(shaped(result, criterion, unlock), edge, side, center);
  }

  /** One material on top and below, another on the sides, and the center (answers 1354, 1355). */
  static ShapedRecipeBuilder cross(
      ShapedRecipeBuilder builder, ItemLike edge, ItemLike side, ItemLike center) {
    return builder
        .pattern(CROSS_EDGE_ROW)
        .pattern(CROSS_MIDDLE_ROW)
        .pattern(CROSS_EDGE_ROW)
        .define(EDGE, edge)
        .define(SIDE, side)
        .define(CENTER, center);
  }

  static ShapedRecipeBuilder fromStart(ItemLike result) {
    return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
        .unlockedBy(DeskRecipeData.FROM_START, DeskRecipeData.fromStart());
  }

  static ShapedRecipeBuilder shaped(ItemLike result, String criterion, ItemLike unlock) {
    return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
        .unlockedBy(criterion, has(unlock));
  }

  private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(item);
  }
}
