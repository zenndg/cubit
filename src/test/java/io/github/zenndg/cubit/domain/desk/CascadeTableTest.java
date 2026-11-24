package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CascadeTableTest {
  private static final String ROBOT = "robot";
  private static final String CASING = "casing";
  private static final String NAIL = "nail";
  private static final String IRON = "iron";
  private static final String GONE = "gone";
  private static final int FOUR = 4;
  private static final int FIVE = 5;
  private static final int TWENTY = 20;
  private static final CascadeRecipe CASING_RECIPE =
      new CascadeRecipe(CASING, CASING, 1, Map.of(IRON, FOUR));
  private static final CascadeRecipe ROBOT_RECIPE =
      new CascadeRecipe(ROBOT, ROBOT, 1, Map.of(CASING, 1));
  private static final CascadeRecipe NAIL_RECIPE =
      new CascadeRecipe(NAIL, NAIL, FOUR, Map.of(IRON, 1));
  private static final CascadeBook BOOK =
      CascadeBook.of(List.of(CASING_RECIPE, ROBOT_RECIPE, NAIL_RECIPE));

  private static CascadeTable planned() {
    return CascadeTable.of(CascadePlanner.plan(BOOK, ROBOT, FIVE, Map.of()).orElseThrow());
  }

  private static CascadeTable running(CascadeRun.Step... steps) {
    return CascadeTable.of(new CascadeRun(List.of(steps)), BOOK);
  }

  /** Before the start every task waits, with what it takes and what it needs (answer 2024). */
  @Test
  void aPlanShowsWhatEachTaskTakes() {
    CascadeLine casing = planned().lines().getFirst();

    assertEquals(CASING, casing.item());
    assertEquals(FIVE, casing.made());
    assertEquals(Map.of(IRON, TWENTY), casing.takes());
    assertEquals(TWENTY, casing.reserved());
    assertEquals(CascadeTaskStatus.WAITING, casing.status());
    assertEquals(Set.of(0), planned().lines().getLast().needs());
  }

  /** A running plan has only the recipes and the crafts, and shows the same (answer 2024). */
  @Test
  void aRunShowsTheSameLinesWithTheProgress() {
    CascadeTable table =
        running(new CascadeRun.Step(CASING, FIVE, 2), new CascadeRun.Step(ROBOT, FIVE, 0));

    CascadeLine casing = table.lines().getFirst();
    CascadeLine robot = table.lines().getLast();
    assertEquals(CascadeTaskStatus.CRAFTING, casing.status());
    assertEquals(2, casing.done());
    assertEquals(TWENTY, casing.reserved());
    assertEquals(CascadeTaskStatus.WAITING, robot.status());
    assertEquals(Set.of(0), robot.needs());
  }

  @Test
  void aRecipeThatGivesManyShowsTheItems() {
    CascadeLine nails = running(new CascadeRun.Step(NAIL, FIVE, 0)).lines().getFirst();

    assertEquals(TWENTY, nails.made());
    assertEquals(FIVE, nails.reserved());
  }

  /** A recipe a pack took away keeps its line, so the numbers of the others do not move. */
  @Test
  void aGoneRecipeKeepsItsLine() {
    CascadeTable table =
        running(new CascadeRun.Step(GONE, 2, 0), new CascadeRun.Step(CASING, 1, 0));

    CascadeLine gone = table.lines().getFirst();
    assertTrue(gone.item().isEmpty());
    assertTrue(gone.needs().isEmpty());
    assertEquals(0, gone.reserved());
    assertEquals(CASING, table.lines().getLast().item());
  }

  @Test
  void theTableCountsTheCraftsOfThePlan() {
    assertEquals(FIVE + FIVE, planned().totalCrafts());
    assertEquals(0, CascadeTable.EMPTY.totalCrafts());
  }

  @Test
  void anInvalidLineIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new CascadeLine(CASING, 1, Map.of(), Set.of(), 0, 0, CascadeTaskStatus.WAITING));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CascadeLine(CASING, 1, Map.of(), Set.of(), 1, 2, CascadeTaskStatus.WAITING));
  }
}
