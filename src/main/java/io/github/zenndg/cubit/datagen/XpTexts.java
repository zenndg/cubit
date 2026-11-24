package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.entity.RobotXpReturn;
import io.github.zenndg.cubit.menu.XpKeys;
import io.github.zenndg.cubit.registry.CubitFluids;
import java.util.function.BiConsumer;

/** The XP, in English, the base language (answers 1816, 1830 and 2070). */
final class XpTexts {
  private static final String FLUID = "Cubit XP Fluid";
  private static final String FULL_NOTICE = "%s: my XP tank is full";
  private static final String TAB = "XP";
  private static final String TANK = "Tank";
  private static final String AMOUNT = "%s / %s mB · %s points";
  private static final String AMOUNT_ONE = "%s / %s mB · %s point";
  private static final String MARK = "%s mB";
  private static final String YOUR_LEVEL = "Your level: %s";
  private static final String MODES = "Modes";
  private static final String DELETE_EXCESS = "Delete the excess";
  private static final String EXCESS_DETAIL = "With the tank full, what does not fit is deleted";
  private static final String ONLY_STORE = "Only in the mode Store";
  private static final String TAKE_DETAIL = "Takes a level from the tank to you";
  private static final String SEND_DETAIL = "Sends a level of yours to the tank";
  private static final String NOT_ACTIVE = "Only with the Cubit active";
  private static final String TANK_EMPTY = "The tank is empty";
  private static final String TANK_FULL = "The tank is full";
  private static final String NO_XP = "You have no XP";
  private static final String TOOLTIP = "XP %s / %s mB";
  private static final String IGNORE = "Ignore";
  private static final String IGNORE_DETAIL = "Deletes the XP of its work and of the ground";
  private static final String STORE = "Store";
  private static final String STORE_DETAIL = "Keeps the XP until the tank is full";
  private static final String RETURN_TO_DOCK = "Return to Dock";
  private static final String RETURN_TO_DOCK_DETAIL = "When full, empties the tank at the Dock";

  private XpTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(CubitFluids.XP_FLUID_TYPE.get().getDescriptionId(), FLUID);
    add.accept(RobotXpReturn.FULL_KEY, FULL_NOTICE);
    add.accept(XpKeys.TAB, TAB);
    add.accept(XpKeys.TANK, TANK);
    add.accept(XpKeys.AMOUNT, AMOUNT);
    add.accept(XpKeys.AMOUNT_ONE, AMOUNT_ONE);
    add.accept(XpKeys.MARK, MARK);
    add.accept(XpKeys.YOUR_LEVEL, YOUR_LEVEL);
    add.accept(XpKeys.MODES, MODES);
    add.accept(XpKeys.DELETE_EXCESS, DELETE_EXCESS);
    add.accept(XpKeys.DELETE_EXCESS_DETAIL, EXCESS_DETAIL);
    add.accept(XpKeys.ONLY_STORE, ONLY_STORE);
    add.accept(XpKeys.TAKE_DETAIL, TAKE_DETAIL);
    add.accept(XpKeys.SEND_DETAIL, SEND_DETAIL);
    add.accept(XpKeys.NOT_ACTIVE, NOT_ACTIVE);
    add.accept(XpKeys.TANK_EMPTY, TANK_EMPTY);
    add.accept(XpKeys.TANK_FULL, TANK_FULL);
    add.accept(XpKeys.NO_XP, NO_XP);
    add.accept(XpKeys.TOOLTIP, TOOLTIP);
    addModes(add);
  }

  private static void addModes(BiConsumer<String, String> add) {
    add.accept(XpKeys.mode(XpMode.IGNORE), IGNORE);
    add.accept(XpKeys.modeDetail(XpMode.IGNORE), IGNORE_DETAIL);
    add.accept(XpKeys.mode(XpMode.STORE), STORE);
    add.accept(XpKeys.modeDetail(XpMode.STORE), STORE_DETAIL);
    add.accept(XpKeys.mode(XpMode.RETURN_TO_DOCK), RETURN_TO_DOCK);
    add.accept(XpKeys.modeDetail(XpMode.RETURN_TO_DOCK), RETURN_TO_DOCK_DETAIL);
  }
}
