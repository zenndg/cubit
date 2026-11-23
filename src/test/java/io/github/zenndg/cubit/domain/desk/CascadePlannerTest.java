package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CascadePlannerTest {
  private static final String ROBOT = "robot";
  private static final String CASING = "casing";
  private static final String VISOR = "visor";
  private static final String CARD = "card";
  private static final String CARD_B = "card_b";
  private static final String KIT = "kit";
  private static final String IRON = "iron";
  private static final String DIAMOND = "diamond";
  private static final String PLASTIC = "plastic";
  private static final String NAIL = "nail";
  private static final String BOX = "box";
  private static final String OTHER = "other";
  private static final String EGG = "egg";
  private static final String HEN = "hen";
  private static final int FOUR = 4;
  private static final CascadeRecipe CASING_RECIPE = recipe(CASING, 1, Map.of(IRON, FOUR));
  private static final CascadeRecipe VISOR_RECIPE = recipe(VISOR, 1, Map.of(DIAMOND, 2));
  private static final CascadeRecipe ROBOT_RECIPE = recipe(ROBOT, 1, Map.of(CASING, 1, VISOR, 1));
  private static final CascadeBook BOOK =
      CascadeBook.of(List.of(CASING_RECIPE, VISOR_RECIPE, ROBOT_RECIPE));
  private static final int FIVE = 5;
  private static final int TWO = 2;
  private static final int TEN = 10;
  private static final int TWENTY = 20;

  private static CascadeRecipe recipe(String result, int yield, Map<String, Integer> materials) {
    return new CascadeRecipe(result, result, yield, materials);
  }

  private static CascadePlan plan(String item, int quantity, Map<String, Integer> ready) {
    return CascadePlanner.plan(BOOK, item, quantity, ready).orElseThrow();
  }

  private static List<String> items(CascadePlan plan) {
    return plan.tasks().stream().map(task -> task.recipe().result()).toList();
  }

  /** Five robots take five of each part first, and the robots come last (answers 2006, 2012). */
  @Test
  void aPlanMakesThePartsBeforeTheItem() {
    CascadePlan plan = plan(ROBOT, FIVE, Map.of());

    assertEquals(Set.of(CASING, VISOR), Set.copyOf(items(plan).subList(0, TWO)));
    assertEquals(ROBOT, items(plan).getLast());
    assertEquals(Set.of(0, 1), plan.tasks().getLast().needs());
    assertTrue(plan.tasks().getFirst().needs().isEmpty());
    assertEquals(Map.of(IRON, TWENTY, DIAMOND, TEN), plan.base());
    assertEquals(FIVE + FIVE + FIVE, plan.totalCrafts());
  }

  /** Parts already made lower the crafts and the base materials (answer 2008). */
  @Test
  void readyPartsAreReused() {
    CascadePlan plan = plan(ROBOT, FIVE, Map.of(CASING, TWO, VISOR, TEN, IRON, TWENTY));

    assertEquals(List.of(CASING, ROBOT), items(plan));
    assertEquals(Map.of(CASING, TWO, VISOR, FIVE), plan.reused());
    assertEquals(Map.of(IRON, (FIVE - TWO) * FOUR), plan.base());
    assertEquals(Set.of(0), plan.tasks().getLast().needs());
  }

  /** The finished items asked are new: the ones already made are not counted. */
  @Test
  void theItemItselfIsNeverReused() {
    assertEquals(FIVE, plan(ROBOT, FIVE, Map.of(ROBOT, TEN)).tasks().getLast().crafts());
  }

  /** A part of two items is one task, for both (answer 2031). */
  @Test
  void aSharedPartIsOneTask() {
    CascadeRecipe card = recipe(CARD, 1, Map.of(PLASTIC, 1, CASING, 1));
    CascadeRecipe other = recipe(CARD_B, 1, Map.of(CASING, 1));
    CascadeRecipe kit = recipe(KIT, 1, Map.of(CARD, 1, CARD_B, 1));
    CascadeBook book = CascadeBook.of(List.of(CASING_RECIPE, card, other, kit));
    CascadePlan plan = CascadePlanner.plan(book, KIT, TWO, Map.of()).orElseThrow();

    assertEquals(1, items(plan).stream().filter(CASING::equals).count());
    assertEquals(
        TWO + TWO,
        plan.tasks().stream()
            .filter(task -> task.recipe().result().equals(CASING))
            .findFirst()
            .orElseThrow()
            .crafts());
  }

  /** A recipe that gives several makes only the crafts it needs, rounded up. */
  @Test
  void aRecipeOfManyRoundsTheCraftsUp() {
    CascadeRecipe nail = recipe(NAIL, FOUR, Map.of(IRON, 1));
    CascadeRecipe box = recipe(BOX, 1, Map.of(NAIL, TEN));
    CascadePlan plan =
        CascadePlanner.plan(CascadeBook.of(List.of(nail, box)), BOX, 1, Map.of()).orElseThrow();

    assertEquals(TWO + 1, plan.tasks().getFirst().crafts());
    assertEquals(Map.of(IRON, TWO + 1), plan.base());
  }

  /** Two recipes for the same item: the first of the catalog (answer 2032). */
  @Test
  void theFirstRecipeOfAnItemIsUsed() {
    CascadeRecipe later = new CascadeRecipe(OTHER, CASING, 1, Map.of(DIAMOND, TEN));
    CascadeBook book = CascadeBook.of(List.of(CASING_RECIPE, later));

    assertEquals(CASING_RECIPE, book.recipeOf(CASING).orElseThrow());
  }

  /** A cycle in a pack has no plan, and neither has an item without a recipe (answer 2034). */
  @Test
  void aCycleHasNoPlan() {
    CascadeBook book =
        CascadeBook.of(List.of(recipe(EGG, 1, Map.of(HEN, 1)), recipe(HEN, 1, Map.of(EGG, 1))));

    assertEquals(Optional.empty(), CascadePlanner.plan(book, EGG, 1, Map.of()));
    assertEquals(Optional.empty(), CascadePlanner.plan(book, IRON, 1, Map.of()));
  }

  /** What each task takes in all, for the third column of the table (answer 2024). */
  @Test
  void aTaskTellsWhatItReserves() {
    CascadePlan plan = plan(ROBOT, FIVE, Map.of());

    assertEquals(TEN, plan.tasks().getLast().reserved());
  }
}
