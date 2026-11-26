package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.CubitConfig;
import io.github.zenndg.cubit.item.RobotRechargeRecipe;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/** The recipes become plain JSON files, which the pack can override with KubeJS. */
final class CubitRecipeProvider extends RecipeProvider {
  private static final String TOOLBOX_TOP_ROW = "III";
  private static final String TOOLBOX_MIDDLE_ROW = "IRI";
  private static final String TOOLBOX_BOTTOM_ROW = "IOI";
  private static final String TABLET_TOP_ROW = "QRQ";
  private static final String TABLET_BOTTOM_ROW = "QDQ";
  private static final char QUARTZ = 'Q';
  private static final char IRON = 'I';
  private static final char REDSTONE = 'R';
  private static final char OBSIDIAN = 'O';
  private static final char DISPLAY = 'D';
  private static final String FROM_CASING_SUFFIX = "_from_casing";
  private static final String HAS_ROBOT = "has_robot";
  private static final String HAS_PREFIX = "has_";
  private static final String RECHARGE_PREFIX = "recharge_";
  private static final String RECIPE_ADVANCEMENT_PREFIX = "recipes/misc/";
  private static final String HAS_THE_RECIPE = "has_the_recipe";
  private static final String HAS_POWER_BANK = "has_power_bank";

  CubitRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries);
  }

  /**
   * Every recipe of the mod runs in the Engineering Desk, and only the desk itself stays in the
   * crafting table (answers 1427 and 1430).
   */
  @Override
  protected void buildRecipes(RecipeOutput output) {
    DeskRecipeData.desk(output);
    RecipeOutput desk = new EngineeringRecipeOutput(output);
    toolbox(desk);
    SharedPartRecipes.save(desk);
    CubitPartRecipes.save(desk);
    robot(desk);
    dock(desk);
    networkServer(desk);
    solarRelay(desk);
    signalAntenna(desk);
    tablet(desk);
    modules(desk);
    powerBanks(desk);
    CubitCardRecipes.save(desk);
    recharge(desk);
    RechargeSplashRecipe.save(desk);
    DeskRecipeData.repair(desk);
  }

  private void toolbox(RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CubitItems.TOOLBOX.get())
        .pattern(TOOLBOX_TOP_ROW)
        .pattern(TOOLBOX_MIDDLE_ROW)
        .pattern(TOOLBOX_BOTTOM_ROW)
        .define(IRON, Items.IRON_INGOT)
        .define(REDSTONE, Items.REDSTONE)
        .define(OBSIDIAN, Items.OBSIDIAN)
        .unlockedBy(DeskRecipeData.FROM_START, DeskRecipeData.fromStart())
        .save(output);
  }

  /** Its four parts and three shared ones, without a shape, open from the start (1496, 2146). */
  private void robot(RecipeOutput output) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CubitItems.GENERAL_ROBOT.get())
        .requires(CubitItems.GENERAL_ROBOT_CASING.get())
        .requires(CubitItems.GENERAL_ROBOT_VISOR.get())
        .requires(CubitItems.GENERAL_ROBOT_TREADS.get())
        .requires(CubitItems.NEURAL_CORE.get())
        .requires(CubitItems.XP_TANK.get())
        .requires(CubitItems.HARD_DRIVE.get())
        .requires(CubitItems.SOLAR_PANEL.get())
        .unlockedBy(DeskRecipeData.FROM_START, DeskRecipeData.fromStart())
        .save(output);
  }

  /**
   * The modules, chained like the cells, in a cross with the previous one in the center and the
   * Neural Core in the one of the Advanced (answers 1357 and 1358).
   */
  private void modules(RecipeOutput output) {
    module(
            CubitItems.ADVANCED_MODULE.get(),
            Items.LAPIS_LAZULI,
            Items.GOLD_INGOT,
            CubitItems.NEURAL_CORE.get())
        .save(output);
    module(
            CubitItems.ELITE_MODULE.get(),
            Items.PURPLE_DYE,
            Items.DIAMOND,
            CubitItems.ADVANCED_MODULE.get())
        .save(output);
    module(
            CubitItems.ULTIMATE_MODULE.get(),
            Items.NETHERITE_INGOT,
            Items.BLAZE_ROD,
            CubitItems.ELITE_MODULE.get())
        .save(output);
  }

  /**
   * The Advanced Module unlocks with a Cubit, and each next one with the Module in its center
   * (answer 1498).
   */
  private static ShapedRecipeBuilder module(
      ItemLike result, ItemLike edge, ItemLike side, ItemLike center) {
    ItemLike unlock =
        center == CubitItems.NEURAL_CORE.get() ? CubitItems.GENERAL_ROBOT.get() : center;
    String criterion = HAS_PREFIX + BuiltInRegistries.ITEM.getKey(unlock.asItem()).getPath();
    return CubitPartRecipes.cross(
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result), edge, side, center)
        .unlockedBy(criterion, has(unlock));
  }

  /**
   * Each bank from scratch, with the casing and the cell of its tier, empty, and each next one from
   * the previous, in the cross of its cell, carrying the charge (answers 902 and 1355).
   */
  private void powerBanks(RecipeOutput output) {
    fromCasing(CubitItems.BASE_POWER_BANK.get(), CubitItems.BASE_POWER_CELL.get()).save(output);
    fromCasing(CubitItems.ADVANCED_POWER_BANK.get(), CubitItems.ADVANCED_POWER_CELL.get())
        .save(output, fromCasingId(CubitItems.ADVANCED_POWER_BANK.get()));
    fromCasing(CubitItems.ELITE_POWER_BANK.get(), CubitItems.ELITE_POWER_CELL.get())
        .save(output, fromCasingId(CubitItems.ELITE_POWER_BANK.get()));
    fromCasing(CubitItems.ULTIMATE_POWER_BANK.get(), CubitItems.ULTIMATE_POWER_CELL.get())
        .save(output, fromCasingId(CubitItems.ULTIMATE_POWER_BANK.get()));
    RecipeOutput upgrades = new PowerBankRecipeOutput(output);
    upgrade(
            CubitItems.BASE_POWER_BANK.get(),
            CubitItems.ADVANCED_POWER_BANK.get(),
            Items.LAPIS_LAZULI,
            Items.GOLD_INGOT)
        .save(upgrades);
    upgrade(
            CubitItems.ADVANCED_POWER_BANK.get(),
            CubitItems.ELITE_POWER_BANK.get(),
            Items.PURPLE_DYE,
            Items.DIAMOND)
        .save(upgrades);
    upgrade(
            CubitItems.ELITE_POWER_BANK.get(),
            CubitItems.ULTIMATE_POWER_BANK.get(),
            Items.NETHERITE_INGOT,
            Items.BLAZE_ROD)
        .save(upgrades);
  }

  /** The casing and the cell, without a shape, unlocked by the cell (answer 1355). */
  private static ShapelessRecipeBuilder fromCasing(ItemLike bank, ItemLike cell) {
    String criterion = HAS_PREFIX + BuiltInRegistries.ITEM.getKey(cell.asItem()).getPath();
    return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, bank)
        .requires(CubitItems.POWER_BANK_CASING.get())
        .requires(cell)
        .unlockedBy(criterion, has(cell));
  }

  private static ResourceLocation fromCasingId(ItemLike bank) {
    return BuiltInRegistries.ITEM.getKey(bank.asItem()).withSuffix(FROM_CASING_SUFFIX);
  }

  private static ShapedRecipeBuilder upgrade(
      ItemLike previous, ItemLike next, ItemLike edge, ItemLike side) {
    String criterion = HAS_PREFIX + BuiltInRegistries.ITEM.getKey(previous.asItem()).getPath();
    return CubitPartRecipes.cross(
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, next), edge, side, previous)
        .unlockedBy(criterion, has(previous));
  }

  /** Waking a stored Cubit with any Power Bank, which unlocks the recipe (answers 908 and 910). */
  private void recharge(RecipeOutput output) {
    ResourceLocation id =
        ResourceLocation.fromNamespaceAndPath(
            CubitMod.MOD_ID, RECHARGE_PREFIX + CubitConfig.GENERAL_ROBOT.id());
    AdvancementHolder unlock =
        output
            .advancement()
            .addCriterion(HAS_THE_RECIPE, RecipeUnlockedTrigger.unlocked(id))
            .addCriterion(
                HAS_POWER_BANK, inventoryTrigger(ItemPredicate.Builder.item().of(powerBanks())))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
            .build(id.withPrefix(RECIPE_ADVANCEMENT_PREFIX));
    output.accept(
        id,
        new RobotRechargeRecipe(
            Ingredient.of(CubitItems.GENERAL_ROBOT.get()),
            Ingredient.of(powerBanks()),
            CubitItems.GENERAL_ROBOT.get()),
        unlock);
  }

  private static ItemLike[] powerBanks() {
    return new ItemLike[] {
      CubitItems.BASE_POWER_BANK.get(),
      CubitItems.ADVANCED_POWER_BANK.get(),
      CubitItems.ELITE_POWER_BANK.get(),
      CubitItems.ULTIMATE_POWER_BANK.get()
    };
  }

  /** Only shared parts and the Base cell, without a shape, because it starts Base (2144, 2147). */
  private void dock(RecipeOutput output) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CubitItems.INDUCTION_DOCK.get())
        .requires(CubitItems.HARD_DRIVE.get())
        .requires(CubitItems.XP_TANK.get())
        .requires(CubitItems.BASE_POWER_CELL.get())
        .requires(CubitItems.INDUCTION_COIL.get())
        .requires(CubitItems.DISPLAY_PANEL.get())
        .requires(CubitItems.SERVO_MOTOR.get())
        .requires(CubitItems.SOLAR_PANEL.get())
        .unlockedBy(HAS_ROBOT, has(CubitItems.GENERAL_ROBOT.get()))
        .save(output);
  }

  /** An iron block as the cabinet and the parts, without a shape, as the dock (answer 2282). */
  private void networkServer(RecipeOutput output) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CubitItems.NETWORK_SERVER.get())
        .requires(Items.IRON_BLOCK)
        .requires(CubitItems.HARD_DRIVE.get())
        .requires(CubitItems.XP_TANK.get())
        .requires(CubitItems.BASE_POWER_CELL.get())
        .requires(CubitItems.NEURAL_CORE.get())
        .requires(CubitItems.CHUNK_ANCHOR.get())
        .requires(CubitItems.TRANSCEIVER.get())
        .requires(CubitItems.DISPLAY_PANEL.get())
        .unlockedBy(HAS_ROBOT, has(CubitItems.GENERAL_ROBOT.get()))
        .save(output);
  }

  private void solarRelay(RecipeOutput output) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CubitItems.SOLAR_RELAY.get())
        .requires(CubitItems.SOLAR_PANEL.get())
        .requires(CubitItems.TRANSCEIVER.get())
        .requires(CubitItems.BASE_POWER_CELL.get())
        .requires(CubitItems.XP_TANK.get())
        .unlockedBy(HAS_ROBOT, has(CubitItems.GENERAL_ROBOT.get()))
        .save(output);
  }

  /** A lightning rod as the mast, with the parts, without a shape (answer 2282). */
  private void signalAntenna(RecipeOutput output) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CubitItems.SIGNAL_ANTENNA.get())
        .requires(Items.LIGHTNING_ROD)
        .requires(CubitItems.TRANSCEIVER.get())
        .requires(CubitItems.CHUNK_ANCHOR.get())
        .requires(CubitItems.INDUCTION_COIL.get())
        .requires(CubitItems.BASE_POWER_CELL.get())
        .requires(CubitItems.XP_TANK.get())
        .unlockedBy(HAS_ROBOT, has(CubitItems.GENERAL_ROBOT.get()))
        .save(output);
  }

  /** A display in a frame of quartz, with redstone on top (answer 2145). */
  private void tablet(RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, CubitItems.TABLET.get())
        .pattern(TABLET_TOP_ROW)
        .pattern(TABLET_BOTTOM_ROW)
        .define(QUARTZ, Items.QUARTZ)
        .define(REDSTONE, Items.REDSTONE)
        .define(DISPLAY, CubitItems.DISPLAY_PANEL.get())
        .unlockedBy(HAS_ROBOT, has(CubitItems.GENERAL_ROBOT.get()))
        .save(output);
  }
}
