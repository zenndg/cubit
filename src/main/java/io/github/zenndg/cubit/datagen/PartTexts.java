package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.registry.CubitItems;
import java.util.function.BiConsumer;

/** The parts of the Cubit and of the Power Banks, in English, the base language (answer 1349). */
final class PartTexts {
  private static final String CASING = "Cubit Casing";
  private static final String VISOR = "Cubit Visor";
  private static final String TREADS = "Cubit Treads";
  private static final String CORE = "Neural Core";
  private static final String POWER_BANK_CASING = "Power Bank Casing";
  private static final String BASE_CELL = "Base Power Cell";
  private static final String ADVANCED_CELL = "Advanced Power Cell";
  private static final String ELITE_CELL = "Elite Power Cell";
  private static final String ULTIMATE_CELL = "Ultimate Power Cell";
  private static final String CARD_CASING = "Card Casing";
  private static final String CIRCUIT_BOARD = "Circuit Board";
  private static final String XP_TANK = "XP Tank";
  private static final String HARD_DRIVE = "Hard Drive";
  private static final String SOLAR_PANEL = "Solar Panel";
  private static final String DISPLAY_PANEL = "Display Panel";
  private static final String SERVO_MOTOR = "Servo Motor";
  private static final String INDUCTION_COIL = "Induction Coil";
  private static final String TRANSCEIVER = "Transceiver";
  private static final String CHUNK_ANCHOR = "Chunk Anchor";

  private PartTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(CubitItems.GENERAL_ROBOT_CASING.get().getDescriptionId(), CASING);
    add.accept(CubitItems.GENERAL_ROBOT_VISOR.get().getDescriptionId(), VISOR);
    add.accept(CubitItems.GENERAL_ROBOT_TREADS.get().getDescriptionId(), TREADS);
    add.accept(CubitItems.NEURAL_CORE.get().getDescriptionId(), CORE);
    add.accept(CubitItems.POWER_BANK_CASING.get().getDescriptionId(), POWER_BANK_CASING);
    add.accept(CubitItems.BASE_POWER_CELL.get().getDescriptionId(), BASE_CELL);
    add.accept(CubitItems.ADVANCED_POWER_CELL.get().getDescriptionId(), ADVANCED_CELL);
    add.accept(CubitItems.ELITE_POWER_CELL.get().getDescriptionId(), ELITE_CELL);
    add.accept(CubitItems.ULTIMATE_POWER_CELL.get().getDescriptionId(), ULTIMATE_CELL);
    add.accept(CubitItems.CARD_CASING.get().getDescriptionId(), CARD_CASING);
    add.accept(CubitItems.CIRCUIT_BOARD.get().getDescriptionId(), CIRCUIT_BOARD);
    addShared(add);
  }

  private static void addShared(BiConsumer<String, String> add) {
    add.accept(CubitItems.XP_TANK.get().getDescriptionId(), XP_TANK);
    add.accept(CubitItems.HARD_DRIVE.get().getDescriptionId(), HARD_DRIVE);
    add.accept(CubitItems.SOLAR_PANEL.get().getDescriptionId(), SOLAR_PANEL);
    add.accept(CubitItems.DISPLAY_PANEL.get().getDescriptionId(), DISPLAY_PANEL);
    add.accept(CubitItems.SERVO_MOTOR.get().getDescriptionId(), SERVO_MOTOR);
    add.accept(CubitItems.INDUCTION_COIL.get().getDescriptionId(), INDUCTION_COIL);
    add.accept(CubitItems.TRANSCEIVER.get().getDescriptionId(), TRANSCEIVER);
    add.accept(CubitItems.CHUNK_ANCHOR.get().getDescriptionId(), CHUNK_ANCHOR);
  }
}
