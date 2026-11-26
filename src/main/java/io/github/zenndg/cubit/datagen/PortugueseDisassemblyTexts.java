package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.function.BiConsumer;

/** The Disassemble tab of the Engineering Desk, in Portuguese (answers 2533 and 2534). */
final class PortugueseDisassemblyTexts {
  private static final String NAME = "Desmontar";
  private static final String LOCKED = "Libera no Elite";
  private static final String ITEM = "Item";
  private static final String RETURNS = "Volta";
  private static final String EACH = "Cada: %s FE · %s s";
  private static final String LOST = "A energia, o nome e a cor se perdem.";
  private static final String EMPTY = "Ponha um item do mod para desmontar.";
  private static final String LEFT = "Faltam %s";
  private static final String START = "Iniciar";
  private static final String RUNS = "Há uma desmontagem rodando";
  private static final String LOCKED_TIER = LOCKED;
  private static final String NO_PARTS = "Ele não tem peças do mod para devolver.";
  private static final String TOO_FEW = "A receita rende mais: ponha o bastante para uma.";
  private static final String HAS_CONTENTS = "Esvazie antes: ele guarda itens, XP, card ou tochas.";
  private static final String BUSY = "Há um pedido ou um plano rodando.";

  private PortugueseDisassemblyTexts() {}

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
