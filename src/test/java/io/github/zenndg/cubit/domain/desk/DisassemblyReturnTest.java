package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.desk.DisassemblyReturn.Part;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class DisassemblyReturnTest {
  private static final String CASING = "cubit:casing";
  private static final String CORE = "cubit:core";
  private static final String OTHER_CORE = "cubit:other_core";
  private static final String IRON = "minecraft:iron_ingot";
  private static final String DIAMOND = "minecraft:diamond";
  private static final String ADVANCED = "cubit:advanced_module";
  private static final String ELITE = "cubit:elite_module";
  private static final int TWICE = 2;
  private static final String MOD = "cubit:";
  private static final String BLANK = " ";
  private static final Predicate<String> FROM_MOD = item -> item.startsWith(MOD);

  @Test
  void onlyThePartsOfTheModComeBackWithTheirCount() {
    List<List<String>> marks =
        List.of(List.of(CASING), List.of(IRON), List.of(CORE), List.of(CASING), List.of(DIAMOND));
    assertEquals(
        List.of(new Part(CASING, TWICE), new Part(CORE, 1)),
        DisassemblyReturn.of(marks, FROM_MOD, List.of()));
  }

  @Test
  void aMarkOfSeveralGivesTheFirstOfTheMod() {
    List<List<String>> marks = List.of(List.of(IRON, CORE, OTHER_CORE), List.of(DIAMOND, IRON));
    assertEquals(List.of(new Part(CORE, 1)), DisassemblyReturn.of(marks, FROM_MOD, List.of()));
  }

  @Test
  void theModulesComeAfterTheParts() {
    assertEquals(
        List.of(new Part(CASING, 1), new Part(ADVANCED, 1), new Part(ELITE, 1)),
        DisassemblyReturn.of(List.of(List.of(CASING)), FROM_MOD, List.of(ADVANCED, ELITE)));
  }

  @Test
  void anItemNeedsAPartOfTheMod() {
    assertTrue(DisassemblyReturn.hasParts(List.of(List.of(IRON, CORE)), FROM_MOD));
    assertFalse(DisassemblyReturn.hasParts(List.of(List.of(IRON), List.of(DIAMOND)), FROM_MOD));
  }

  @Test
  void eachTierAboveTheBaseHadItsModule() {
    assertEquals(List.of(), DisassemblyReturn.modulesUpTo(RobotTier.BASE));
    assertEquals(
        List.of(RobotTier.ADVANCED, RobotTier.ELITE),
        DisassemblyReturn.modulesUpTo(RobotTier.ELITE));
    assertEquals(
        List.of(RobotTier.ADVANCED, RobotTier.ELITE, RobotTier.ULTIMATE),
        DisassemblyReturn.modulesUpTo(RobotTier.ULTIMATE));
  }

  @Test
  void aPartHasAnItemAndAtLeastOne() {
    assertThrows(IllegalArgumentException.class, () -> new Part(BLANK, 1));
    assertThrows(IllegalArgumentException.class, () -> new Part(CASING, 0));
  }
}
