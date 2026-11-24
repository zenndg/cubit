package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeskCategoryTest {
  private static final String ENERGY = "energy";
  private static final String PARTS = "parts";
  private static final String UNKNOWN = "furniture";

  @Test
  void theIdsAreTheNamesInTheJson() {
    assertEquals(ENERGY, DeskCategory.ENERGY.id());
  }

  /** The parts many items share, right after the Cubit and its own ones (2141 and 2155). */
  @Test
  void theSharedPartsComeAfterTheCubit() {
    assertEquals(Optional.of(DeskCategory.PARTS), DeskCategory.byId(PARTS));
    assertEquals(DeskCategory.ROBOT.ordinal() + 1, DeskCategory.PARTS.ordinal());
  }

  @Test
  void readsAKnownId() {
    assertEquals(Optional.of(DeskCategory.ENERGY), DeskCategory.byId(ENERGY));
  }

  @Test
  void anUnknownIdIsNoCategory() {
    assertTrue(DeskCategory.byId(UNKNOWN).isEmpty());
    assertTrue(DeskCategory.byId(null).isEmpty());
  }

  @Test
  void toolsIsTheDefault() {
    assertEquals(DeskCategory.TOOLS, DeskCategory.DEFAULT);
  }

  @Test
  void everyIdReadsBackItsCategory() {
    for (DeskCategory category : DeskCategory.values()) {
      assertEquals(Optional.of(category), DeskCategory.byId(category.id()));
      assertFalse(category.id().isBlank());
    }
  }
}
