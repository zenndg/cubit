package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RecipeTrailTest {
  private static final int ROBOT = 9;
  private static final int CORE = 5;
  private static final int NO_RECIPE = -1;

  @Test
  void aNewTrailHasNowhereToGoBack() {
    assertTrue(RecipeTrail.EMPTY.isEmpty());
    assertEquals(Optional.empty(), RecipeTrail.EMPTY.last());
    assertEquals(RecipeTrail.EMPTY, RecipeTrail.EMPTY.back());
  }

  @Test
  void itGoesBackOneStepAtATime() {
    RecipeTrail trail = RecipeTrail.EMPTY.leaving(ROBOT).leaving(CORE);
    assertFalse(trail.isEmpty());
    assertEquals(Optional.of(CORE), trail.last());
    assertEquals(Optional.of(ROBOT), trail.back().last());
    assertTrue(trail.back().back().isEmpty());
  }

  @Test
  void leavingWithoutARecipeIsNoStep() {
    assertEquals(RecipeTrail.EMPTY, RecipeTrail.EMPTY.leaving(NO_RECIPE));
  }

  @Test
  void theFirstPlaceOfTheCatalogIsAStep() {
    assertEquals(Optional.of(0), RecipeTrail.EMPTY.leaving(0).last());
  }

  @Test
  void beyondTheLimitTheOldestIsForgotten() {
    RecipeTrail trail = RecipeTrail.EMPTY;
    for (int place = 0; place <= RecipeTrail.MAX_STEPS; place++) {
      trail = trail.leaving(place);
    }
    assertEquals(RecipeTrail.MAX_STEPS, trail.places().size());
    assertEquals(1, trail.places().get(0));
    assertEquals(Optional.of(RecipeTrail.MAX_STEPS), trail.last());
  }

  @Test
  void itRefusesMoreStepsThanTheLimitAndNoRecipe() {
    List<Integer> tooMany = Collections.nCopies(RecipeTrail.MAX_STEPS + 1, ROBOT);
    assertThrows(IllegalArgumentException.class, () -> new RecipeTrail(tooMany));
    List<Integer> none = List.of(NO_RECIPE);
    assertThrows(IllegalArgumentException.class, () -> new RecipeTrail(none));
  }
}
