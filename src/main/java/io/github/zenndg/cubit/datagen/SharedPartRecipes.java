package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

/**
 * The parts many items share, with the grids of _docs/itens.md (answers 2136 and 2150). Only from
 * the Overworld and open from the start, but for the coil, which comes with the Dock.
 */
final class SharedPartRecipes {
  private static final String TANK_TOP_ROW = "GIG";
  private static final String TANK_MIDDLE_ROW = "GBG";
  private static final String TANK_BOTTOM_ROW = " I ";
  private static final String DRIVE_TOP_ROW = "ICI";
  private static final String DRIVE_BOTTOM_ROW = "RAU";
  private static final String PANES_ROW = "PPP";
  private static final String SOLAR_MIDDLE_ROW = "LLL";
  private static final String SOLAR_BOTTOM_ROW = "URU";
  private static final String DISPLAY_MIDDLE_ROW = "IDI";
  private static final String DISPLAY_BOTTOM_ROW = "ROU";
  private static final String MOTOR_TOP_ROW = " U ";
  private static final String MOTOR_MIDDLE_ROW = "ISI";
  private static final String MOTOR_BOTTOM_ROW = " R ";
  private static final String COIL_TOP_ROW = "SUS";
  private static final String COIL_BOTTOM_ROW = "IRI";
  private static final char GLASS = 'G';
  private static final char IRON = 'I';
  private static final char BUCKET = 'B';
  private static final char CHEST = 'C';
  private static final char REDSTONE = 'R';
  private static final char GOLD = 'A';
  private static final char COPPER = 'U';
  private static final char PANE = 'P';
  private static final char LAPIS = 'L';
  private static final char DIAMOND = 'D';
  private static final char OBSIDIAN = 'O';
  private static final char PISTON = 'S';

  private SharedPartRecipes() {}

  static void save(RecipeOutput output) {
    tank().save(output);
    drive().save(output);
    solar().save(output);
    display().save(output);
    motor().save(output);
    coil().save(output);
  }

  private static ShapedRecipeBuilder tank() {
    return CubitPartRecipes.fromStart(CubitItems.XP_TANK.get())
        .pattern(TANK_TOP_ROW)
        .pattern(TANK_MIDDLE_ROW)
        .pattern(TANK_BOTTOM_ROW)
        .define(GLASS, Items.GLASS)
        .define(IRON, Items.IRON_INGOT)
        .define(BUCKET, Items.BUCKET);
  }

  private static ShapedRecipeBuilder drive() {
    return CubitPartRecipes.fromStart(CubitItems.HARD_DRIVE.get())
        .pattern(DRIVE_TOP_ROW)
        .pattern(DRIVE_BOTTOM_ROW)
        .define(IRON, Items.IRON_INGOT)
        .define(CHEST, Items.CHEST)
        .define(REDSTONE, Items.REDSTONE)
        .define(GOLD, Items.GOLD_INGOT)
        .define(COPPER, Items.COPPER_INGOT);
  }

  private static ShapedRecipeBuilder solar() {
    return CubitPartRecipes.fromStart(CubitItems.SOLAR_PANEL.get())
        .pattern(PANES_ROW)
        .pattern(SOLAR_MIDDLE_ROW)
        .pattern(SOLAR_BOTTOM_ROW)
        .define(PANE, Items.GLASS_PANE)
        .define(LAPIS, Items.LAPIS_LAZULI)
        .define(COPPER, Items.COPPER_INGOT)
        .define(REDSTONE, Items.REDSTONE);
  }

  private static ShapedRecipeBuilder display() {
    return CubitPartRecipes.fromStart(CubitItems.DISPLAY_PANEL.get())
        .pattern(PANES_ROW)
        .pattern(DISPLAY_MIDDLE_ROW)
        .pattern(DISPLAY_BOTTOM_ROW)
        .define(PANE, Items.GLASS_PANE)
        .define(IRON, Items.IRON_INGOT)
        .define(DIAMOND, Items.DIAMOND)
        .define(REDSTONE, Items.REDSTONE)
        .define(OBSIDIAN, Items.OBSIDIAN)
        .define(COPPER, Items.COPPER_INGOT);
  }

  private static ShapedRecipeBuilder motor() {
    return CubitPartRecipes.fromStart(CubitItems.SERVO_MOTOR.get())
        .pattern(MOTOR_TOP_ROW)
        .pattern(MOTOR_MIDDLE_ROW)
        .pattern(MOTOR_BOTTOM_ROW)
        .define(COPPER, Items.COPPER_INGOT)
        .define(IRON, Items.IRON_INGOT)
        .define(PISTON, Items.PISTON)
        .define(REDSTONE, Items.REDSTONE);
  }

  private static ShapedRecipeBuilder coil() {
    return CubitPartRecipes.shaped(
            CubitItems.INDUCTION_COIL.get(),
            CubitPartRecipes.HAS_ROBOT,
            CubitItems.GENERAL_ROBOT.get())
        .pattern(COIL_TOP_ROW)
        .pattern(COIL_BOTTOM_ROW)
        .define(PISTON, Items.PISTON)
        .define(COPPER, Items.COPPER_INGOT)
        .define(IRON, Items.IRON_INGOT)
        .define(REDSTONE, Items.REDSTONE);
  }
}
