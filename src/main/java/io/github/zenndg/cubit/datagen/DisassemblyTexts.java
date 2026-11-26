package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.function.BiConsumer;

/**
 * The Disassemble tab of the Engineering Desk, in English, the base language (answers 2533 and
 * 2534).
 */
final class DisassemblyTexts {
  private static final String NAME = "Disassemble";
  private static final String LOCKED = "Unlocks at Elite";
  private static final String ITEM = "Item";
  private static final String RETURNS = "Returns";
  private static final String EACH = "Each: %s FE · %s s";
  private static final String LOST = "Energy, name and color are lost.";
  private static final String EMPTY = "Put an item of the mod to take apart.";
  private static final String LEFT = "%s left";
  private static final String START = "Start";
  private static final String RUNS = "A disassembly is running";
  private static final String LOCKED_TIER = LOCKED;
  private static final String NO_PARTS = "It has no parts of the mod to give back.";
  private static final String TOO_FEW = "The recipe makes more: put enough for one.";
  private static final String HAS_CONTENTS = "Empty it first: it has items, XP, a card or torches.";
  private static final String BUSY = "An order or a plan is running.";

  private DisassemblyTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.DISASSEMBLE, NAME);
    add.accept(DeskScreenKeys.DISASSEMBLE_LOCKED, LOCKED);
    add.accept(DeskScreenKeys.DISASSEMBLE_ITEM, ITEM);
    add.accept(DeskScreenKeys.DISASSEMBLE_RETURNS, RETURNS);
    add.accept(DeskScreenKeys.DISASSEMBLE_EACH, EACH);
    add.accept(DeskScreenKeys.DISASSEMBLE_LOST, LOST);
    add.accept(DeskScreenKeys.DISASSEMBLE_EMPTY, EMPTY);
    add.accept(DeskScreenKeys.DISASSEMBLE_LEFT, LEFT);
    add.accept(DeskScreenKeys.DISASSEMBLE_START, START);
    add.accept(DeskScreenKeys.DISASSEMBLE_RUNS, RUNS);
    add.accept(DeskScreenKeys.disassemblyRefusal(DisassemblyRefusal.LOCKED_TIER), LOCKED_TIER);
    add.accept(DeskScreenKeys.disassemblyRefusal(DisassemblyRefusal.NO_PARTS), NO_PARTS);
    add.accept(DeskScreenKeys.disassemblyRefusal(DisassemblyRefusal.TOO_FEW), TOO_FEW);
    add.accept(DeskScreenKeys.disassemblyRefusal(DisassemblyRefusal.HAS_CONTENTS), HAS_CONTENTS);
    add.accept(DeskScreenKeys.disassemblyRefusal(DisassemblyRefusal.BUSY), BUSY);
  }
}
