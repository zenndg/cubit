package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.behavior.CallResult;
import io.github.zenndg.cubit.player.CallCommands;
import io.github.zenndg.cubit.player.RobotCalls;
import java.util.function.BiConsumer;

/** Calling a Cubit and its commands, in English (answers 2450 to 2501). */
final class CallTexts {
  private static final String COMING = "It is coming";
  private static final String ARRIVED = "It is here";
  private static final String SEARCHING = "Calling it from far away...";
  private static final String HERE = "It already follows you";
  private static final String WAIT = "Wait %s s to call again";
  private static final String BUSY = "Another Cubit is on its way";
  private static final String NOT_FOUND = "Could not find that Cubit";
  private static final String NO_SPOT = "No safe spot near you";
  private static final String TITLE = "Your Cubits (%s)";
  private static final String NONE = "You have no Cubits";
  private static final String ENTRY = "  %s  %s";
  private static final String WHERE = "     %s · %s";
  private static final String UNLOADED = "Unloaded";
  private static final String NO_MATCH = "No Cubit of yours starts with %s";

  private CallTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RobotCalls.keyOf(CallResult.COMING), COMING);
    add.accept(RobotCalls.keyOf(CallResult.ARRIVED), ARRIVED);
    add.accept(RobotCalls.keyOf(CallResult.SEARCHING), SEARCHING);
    add.accept(RobotCalls.keyOf(CallResult.HERE), HERE);
    add.accept(RobotCalls.keyOf(CallResult.WAIT), WAIT);
    add.accept(RobotCalls.keyOf(CallResult.BUSY), BUSY);
    add.accept(RobotCalls.keyOf(CallResult.NOT_FOUND), NOT_FOUND);
    add.accept(RobotCalls.keyOf(CallResult.NO_SPOT), NO_SPOT);
    add.accept(CallCommands.TITLE_KEY, TITLE);
    add.accept(CallCommands.NONE_KEY, NONE);
    add.accept(CallCommands.ENTRY_KEY, ENTRY);
    add.accept(CallCommands.WHERE_KEY, WHERE);
    add.accept(CallCommands.UNLOADED_KEY, UNLOADED);
    add.accept(CallCommands.NO_MATCH_KEY, NO_MATCH);
  }
}
