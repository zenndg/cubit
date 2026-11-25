package io.github.zenndg.cubit.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * The recipe book of the game never holds a recipe of the desk, on the server or on the screen: a
 * mod may empty the book or never send it to the client (answer 2400).
 */
@AnalyzeClasses(
    packages = DomainBoundaryTest.ROOT_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class RecipeBookTest {
  private static final String RECIPE_BOOK = "RecipeBook";

  @ArchTest
  static final ArchRule nothingReadsTheRecipeBook =
      noClasses().should().dependOnClassesThat().haveSimpleNameEndingWith(RECIPE_BOOK);
}
