package io.github.zenndg.cubit.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Every recipe of the mod can be reached from a new world, with the materials and the release of
 * each one, so no item stays locked forever (answers 1951 and 1991).
 */
class CraftTreeTest {
  private static final Path DATA = Path.of("src/generated/resources/data/cubit");
  private static final String RECIPES = "recipe";
  private static final String RELEASES = "advancement/recipes";
  private static final String JSON = ".json";
  private static final String MOD = "cubit:";
  private static final String NONE = "";
  private static final String DESK = "cubit:engineering_desk";
  private static final String ROBOT = "cubit:general_robot";
  private static final String DAMAGED_ROBOT = "cubit:damaged_general_robot";
  private static final String START = "minecraft:tick";
  private static final String RESULT_KEY = "\"result\":\\s*(?:\\{[^}]*\"id\":\\s*)?";
  private static final String MATERIAL_REGEX = "\"item\":\\s*\"(cubit:[a-z_]+)\"";
  private static final String ITEM_REGEX = "\"(cubit:[a-z_]+)\"";
  private static final String GIVEN_REGEX = "\"recipes\":\\s*\\[\\s*\"(cubit:[a-z_]+)\"";
  private static final String OWN_RECIPE_REGEX = "\"recipe\":\\s*\"cubit:[a-z_]+\"";
  private static final String CRITERIA_REGEX = "\"criteria\":(.*)\"requirements\"";
  private static final Pattern RESULT = Pattern.compile(RESULT_KEY + ITEM_REGEX);
  private static final Pattern MATERIAL = Pattern.compile(MATERIAL_REGEX);
  private static final Pattern ITEM = Pattern.compile(ITEM_REGEX);
  private static final Pattern GIVEN = Pattern.compile(GIVEN_REGEX);
  private static final Pattern OWN_RECIPE = Pattern.compile(OWN_RECIPE_REGEX);
  private static final Pattern CRITERIA = Pattern.compile(CRITERIA_REGEX, Pattern.DOTALL);
  private static final String EMPTY = "There must be recipes";
  private static final String NO_RESULT = "The recipe has no result: ";
  private static final String NO_RELEASE = "Recipes without a release";
  private static final String UNREACHABLE = "Recipes that cannot be reached";

  /** What a recipe gives, what it needs of the mod and what releases it. */
  private record Step(String result, Set<String> materials, Set<String> releases, boolean start) {
    boolean reached(Set<String> has) {
      boolean released = start || releases.stream().anyMatch(has::contains);
      return released && has.containsAll(materials);
    }
  }

  private static List<Path> files(String folder) throws IOException {
    try (Stream<Path> found = Files.walk(DATA.resolve(folder))) {
      return found.filter(path -> path.toString().endsWith(JSON)).toList();
    }
  }

  private static String first(Pattern pattern, String text) {
    Matcher matcher = pattern.matcher(text);
    return matcher.find() ? matcher.group(1) : NONE;
  }

  private static Set<String> every(Pattern pattern, String text) {
    Set<String> found = new HashSet<>();
    pattern.matcher(text).results().forEach(match -> found.add(match.group(1)));
    return found;
  }

  private static String idOf(Path recipe) {
    return MOD + recipe.getFileName().toString().replace(JSON, NONE);
  }

  /** The criteria of the release of each recipe, without the one of already having the recipe. */
  private static Map<String, String> releases() throws IOException {
    Map<String, String> byRecipe = new HashMap<>();
    for (Path path : files(RELEASES)) {
      String text = Files.readString(path);
      String criteria = OWN_RECIPE.matcher(first(CRITERIA, text)).replaceAll(NONE);
      byRecipe.put(first(GIVEN, text), criteria);
    }
    return byRecipe;
  }

  private static Step step(Path recipe, String criteria) throws IOException {
    String text = Files.readString(recipe);
    String result = first(RESULT, text);
    assertFalse(result.isEmpty(), NO_RESULT + recipe);
    boolean start = result.equals(DESK) || criteria.contains(START);
    return new Step(result, every(MATERIAL, text), every(ITEM, criteria), start);
  }

  private static Map<String, Step> steps() throws IOException {
    Map<String, String> releases = releases();
    Map<String, Step> steps = new HashMap<>();
    for (Path recipe : files(RECIPES)) {
      steps.put(idOf(recipe), step(recipe, releases.getOrDefault(idOf(recipe), NONE)));
    }
    return steps;
  }

  @Test
  void everyRecipeHasARelease() throws IOException {
    Set<String> missing = new TreeSet<>();
    files(RECIPES).forEach(recipe -> missing.add(idOf(recipe)));
    assertFalse(missing.isEmpty(), EMPTY);
    missing.removeAll(releases().keySet());
    assertEquals(Set.of(), missing, NO_RELEASE);
  }

  /** The damaged Cubit has no recipe: it drops when a Cubit dies. */
  @Test
  void everyRecipeIsReachedFromANewWorld() throws IOException {
    Map<String, Step> left = steps();
    Set<String> has = new HashSet<>();
    Set<String> now = new HashSet<>();
    do {
      now.clear();
      left.forEach((id, step) -> {
        if (step.reached(has)) {
          now.add(id);
        }
      });
      now.forEach(id -> has.add(left.remove(id).result()));
      if (has.contains(ROBOT)) {
        has.add(DAMAGED_ROBOT);
      }
    } while (!now.isEmpty());
    assertEquals(Set.of(), new TreeSet<>(left.keySet()), UNREACHABLE);
  }
}
