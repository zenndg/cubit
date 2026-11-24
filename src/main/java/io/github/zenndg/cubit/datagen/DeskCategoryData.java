package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * The category of each recipe of the mod in the catalog of the desk, by the item it makes, which is
 * also the name of the recipe (answers 1521 and 1525). The repair goes with the Cubit and the
 * recharge with the energy. A recipe missing here fails the datagen, so no recipe of the mod falls
 * into the tools by mistake.
 */
final class DeskCategoryData {
  private static final String MISSING = "No desk category for the recipe ";
  private static final String FROM_CASING = "_from_casing";
  private static final String REPAIR = "repair_";
  private static final String RECHARGE = "recharge_";
  private static final String NOTHING = "";

  private static final Map<DeskCategory, List<Supplier<Item>>> BY_ITEM =
      Map.of(
          DeskCategory.ROBOT,
          List.of(
              CubitItems.GENERAL_ROBOT_CASING,
              CubitItems.GENERAL_ROBOT_VISOR,
              CubitItems.GENERAL_ROBOT_TREADS,
              CubitItems.GENERAL_ROBOT),
          DeskCategory.PARTS,
          List.of(
              CubitItems.XP_TANK,
              CubitItems.HARD_DRIVE,
              CubitItems.SOLAR_PANEL,
              CubitItems.DISPLAY_PANEL,
              CubitItems.SERVO_MOTOR,
              CubitItems.NEURAL_CORE,
              CubitItems.CIRCUIT_BOARD,
              CubitItems.INDUCTION_COIL),
          DeskCategory.ENERGY,
          List.of(
              CubitItems.POWER_BANK_CASING,
              CubitItems.BASE_POWER_CELL,
              CubitItems.ADVANCED_POWER_CELL,
              CubitItems.ELITE_POWER_CELL,
              CubitItems.ULTIMATE_POWER_CELL,
              CubitItems.BASE_POWER_BANK,
              CubitItems.ADVANCED_POWER_BANK,
              CubitItems.ELITE_POWER_BANK,
              CubitItems.ULTIMATE_POWER_BANK,
              CubitItems.RECHARGE_SPLASH),
          DeskCategory.DOCK,
          List.of(CubitItems.INDUCTION_DOCK),
          DeskCategory.MODULES,
          List.of(CubitItems.ADVANCED_MODULE, CubitItems.ELITE_MODULE, CubitItems.ULTIMATE_MODULE),
          DeskCategory.CARDS,
          List.of(
              CubitItems.CARD_CASING,
              CubitItems.FISHER_CARD,
              CubitItems.FARMER_CARD,
              CubitItems.ORGANIZER_CARD,
              CubitItems.COOK_CARD,
              CubitItems.PATROLLER_CARD),
          DeskCategory.TOOLS,
          List.of(CubitItems.TOOLBOX, CubitItems.TABLET));

  private DeskCategoryData() {}

  static DeskCategory of(ResourceLocation id) {
    if (!CubitMod.MOD_ID.equals(id.getNamespace())) {
      throw new IllegalStateException(MISSING + id);
    }
    String path = id.getPath().replace(FROM_CASING, NOTHING);
    for (Map.Entry<DeskCategory, List<Supplier<Item>>> entry : BY_ITEM.entrySet()) {
      for (Supplier<Item> item : entry.getValue()) {
        if (BuiltInRegistries.ITEM.getKey(item.get()).getPath().equals(path)) {
          return entry.getKey();
        }
      }
    }
    if (path.startsWith(REPAIR)) {
      return DeskCategory.ROBOT;
    }
    if (path.startsWith(RECHARGE)) {
      return DeskCategory.ENERGY;
    }
    throw new IllegalStateException(MISSING + id);
  }
}
