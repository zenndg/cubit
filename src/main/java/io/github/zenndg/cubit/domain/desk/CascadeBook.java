package io.github.zenndg.cubit.domain.desk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** The recipe of each item, the first in the order of the catalog (answer 2032). */
public record CascadeBook(Map<String, CascadeRecipe> byResult) {
  public static CascadeBook of(List<CascadeRecipe> inCatalogOrder) {
    Map<String, CascadeRecipe> byResult = new LinkedHashMap<>();
    inCatalogOrder.forEach(recipe -> byResult.putIfAbsent(recipe.result(), recipe));
    return new CascadeBook(byResult);
  }

  public Optional<CascadeRecipe> recipeOf(String item) {
    return Optional.ofNullable(byResult.get(item));
  }

  public Optional<CascadeRecipe> recipeWithId(String id) {
    return byResult.values().stream().filter(recipe -> recipe.id().equals(id)).findFirst();
  }
}
