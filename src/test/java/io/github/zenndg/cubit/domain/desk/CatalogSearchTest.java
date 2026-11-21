package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CatalogSearchTest {
  private static final String NAME = "Célula de Energia Avançada";
  private static final String PLAIN = "celula";
  private static final String UPPER = "ENERGIA";
  private static final String MIDDLE = "de energia av";
  private static final String OTHER = "módulo";
  private static final String SPACES = "   ";
  private static final String PADDED = "  avançada ";

  @Test
  void ignoresAccents() {
    assertTrue(CatalogSearch.of(PLAIN).matches(NAME));
  }

  @Test
  void ignoresCase() {
    assertTrue(CatalogSearch.of(UPPER).matches(NAME));
  }

  @Test
  void findsTextInTheMiddle() {
    assertTrue(CatalogSearch.of(MIDDLE).matches(NAME));
  }

  @Test
  void ignoresTheSpacesAround() {
    assertTrue(CatalogSearch.of(PADDED).matches(NAME));
  }

  @Test
  void refusesAnotherName() {
    assertFalse(CatalogSearch.of(OTHER).matches(NAME));
  }

  @Test
  void blankTextIsNoSearch() {
    assertTrue(CatalogSearch.of(SPACES).isEmpty());
    assertTrue(CatalogSearch.of(null).isEmpty());
    assertFalse(CatalogSearch.of(PLAIN).isEmpty());
  }

  @Test
  void withoutASearchTheChosenCategoryShows() {
    CatalogSearch none = CatalogSearch.of(SPACES);
    assertTrue(none.shows(DeskCategory.ENERGY, NAME, DeskCategory.ENERGY));
    assertFalse(none.shows(DeskCategory.DOCK, NAME, DeskCategory.ENERGY));
  }

  @Test
  void aSearchLooksInEveryCategory() {
    CatalogSearch search = CatalogSearch.of(PLAIN);
    assertTrue(search.shows(DeskCategory.ENERGY, NAME, DeskCategory.DOCK));
    assertFalse(CatalogSearch.of(OTHER).shows(DeskCategory.ENERGY, NAME, DeskCategory.ENERGY));
  }
}
