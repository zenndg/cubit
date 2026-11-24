package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.registry.CubitItems;
import java.util.function.BiConsumer;

/** The parts of the Cubit and of the Power Banks, in Portuguese (answer 1352). */
final class PortuguesePartTexts {
  private static final String CASING = "Carcaça do Cubit";
  private static final String VISOR = "Visor do Cubit";
  private static final String TREADS = "Esteiras do Cubit";
  private static final String CORE = "Núcleo Neural do Cubit";
  private static final String POWER_BANK_CASING = "Carcaça da Power Bank";
  private static final String BASE_CELL = "Célula de Energia Base";
  private static final String ADVANCED_CELL = "Célula de Energia Avançada";
  private static final String ELITE_CELL = "Célula de Energia Elite";
  private static final String ULTIMATE_CELL = "Célula de Energia Suprema";
  private static final String CARD_CASING = "Carcaça do Card";
  private static final String CIRCUIT_BOARD = "Placa de Circuito";
  private static final String XP_TANK = "Tanque de XP";
  private static final String HARD_DRIVE = "HD";
  private static final String SOLAR_PANEL = "Painel Solar";
  private static final String DISPLAY_PANEL = "Tela";
  private static final String SERVO_MOTOR = "Servomotor";
  private static final String INDUCTION_COIL = "Bobina de Indução";

  private PortuguesePartTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(CubitItems.GENERAL_ROBOT_CASING.get().getDescriptionId(), CASING);
    add.accept(CubitItems.GENERAL_ROBOT_VISOR.get().getDescriptionId(), VISOR);
    add.accept(CubitItems.GENERAL_ROBOT_TREADS.get().getDescriptionId(), TREADS);
    add.accept(CubitItems.GENERAL_ROBOT_CORE.get().getDescriptionId(), CORE);
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
  }
}
