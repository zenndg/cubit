package io.github.zenndg.cubit.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = DomainBoundaryTest.ROOT_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class DomainBoundaryTest {
  static final String ROOT_PACKAGE = "io.github.zenndg.cubit";
  private static final String DOMAIN = "..cubit.domain..";
  private static final String JAVA = "java..";
  private static final String MOD_CLIENT = "..cubit.client..";
  private static final String GAME_CLIENT = "net.minecraft.client..";
  private static final String BLOCK = "..cubit.block..";
  private static final String ENTITY = "..cubit.entity..";
  private static final String GETTER = "get[A-Z].*";
  private static final String JADE_PLUGIN = "..cubit.compat.jade..";
  private static final String JADE = "snownee.jade..";
  private static final String JEI_PLUGIN = "..cubit.compat.jei..";
  private static final String JEI = "mezz.jei..";

  /** AGENTS.md, rule 12: this keeps out the game and also the other packages of the mod. */
  @ArchTest
  static final ArchRule theDomainOnlyDependsOnItselfAndJava =
      classes()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(DOMAIN, JAVA);

  /**
   * AGENTS.md, rule 23. The GameTests only find a client class that the server happens to load, and
   * this finds any of them.
   */
  @ArchTest
  static final ArchRule commonCodeDoesNotUseTheClient =
      noClasses()
          .that()
          .resideOutsideOfPackages(MOD_CLIENT, JEI_PLUGIN)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(MOD_CLIENT, GAME_CLIENT);

  /**
   * Answer 1535: JEI only loads its plugin on the client, and its own interfaces take classes of
   * the client of the game. The plugin still never uses the client package of the mod.
   */
  @ArchTest
  static final ArchRule theJeiPluginDoesNotUseTheClientOfTheMod =
      noClasses()
          .that()
          .resideInAPackage(JEI_PLUGIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(MOD_CLIENT);

  /** AGENTS.md, rule 42: the dock knows the robot by its interface, never by a concrete class. */
  @ArchTest
  static final ArchRule theDockDoesNotKnowTheEntity =
      noClasses()
          .that()
          .resideInAPackage(BLOCK)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(ENTITY);

  /**
   * AGENTS.md, rule 24, and answer 667: only the plugin imports Jade, which Jade finds by itself,
   * so the mod never loads a class of Jade and opens without it.
   */
  @ArchTest
  static final ArchRule onlyThePluginUsesJade =
      noClasses()
          .that()
          .resideOutsideOfPackage(JADE_PLUGIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(JADE);

  /**
   * AGENTS.md, rule 24, and answer 1461: only the plugin imports JEI, which JEI finds by itself.
   */
  @ArchTest
  static final ArchRule onlyThePluginUsesJei =
      noClasses()
          .that()
          .resideOutsideOfPackage(JEI_PLUGIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(JEI);

  /** AGENTS.md, rule 14: {@code capacity()}, not {@code getCapacity()}. */
  @ArchTest
  static final ArchRule theDomainHasNoGetters =
      noMethods()
          .that()
          .areDeclaredInClassesThat()
          .resideInAPackage(DOMAIN)
          .should()
          .haveNameMatching(GETTER);
}
