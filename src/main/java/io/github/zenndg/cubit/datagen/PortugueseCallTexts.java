package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.behavior.CallResult;
import io.github.zenndg.cubit.player.RobotCalls;
import java.util.function.BiConsumer;

/** Calling a Cubit, in Portuguese, with the same keys. */
final class PortugueseCallTexts {
  private static final String COMING = "Está vindo";
  private static final String ARRIVED = "Está aqui";
  private static final String SEARCHING = "Chamando de longe...";
  private static final String HERE = "Já está seguindo você";
  private static final String WAIT = "Espere %s s para chamar de novo";
  private static final String BUSY = "Outro Cubit está a caminho";
  private static final String NOT_FOUND = "Não encontrei esse Cubit";
  private static final String NO_SPOT = "Nenhum lugar seguro perto de você";

  private PortugueseCallTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RobotCalls.keyOf(CallResult.COMING), COMING);
    add.accept(RobotCalls.keyOf(CallResult.ARRIVED), ARRIVED);
    add.accept(RobotCalls.keyOf(CallResult.SEARCHING), SEARCHING);
    add.accept(RobotCalls.keyOf(CallResult.HERE), HERE);
    add.accept(RobotCalls.keyOf(CallResult.WAIT), WAIT);
    add.accept(RobotCalls.keyOf(CallResult.BUSY), BUSY);
    add.accept(RobotCalls.keyOf(CallResult.NOT_FOUND), NOT_FOUND);
    add.accept(RobotCalls.keyOf(CallResult.NO_SPOT), NO_SPOT);
  }
}
