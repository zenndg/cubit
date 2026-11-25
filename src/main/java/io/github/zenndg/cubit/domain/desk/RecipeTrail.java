package io.github.zenndg.cubit.domain.desk;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The recipes the player left behind by opening the recipe of a part, from the oldest to the last,
 * each one as its place in the catalog. It goes back one at a time (answer 2478).
 */
public record RecipeTrail(List<Integer> places) {
  public static final RecipeTrail EMPTY = new RecipeTrail(List.of());
  public static final int MAX_STEPS = 8;

  private static final String TOO_LONG = "A trail keeps up to 8 recipes: ";
  private static final String NO_RECIPE = "A trail only keeps chosen recipes: ";

  public RecipeTrail {
    places = List.copyOf(places);
    if (places.size() > MAX_STEPS) {
      throw new IllegalArgumentException(TOO_LONG + places.size());
    }
    if (places.stream().anyMatch(place -> place < 0)) {
      throw new IllegalArgumentException(NO_RECIPE + places);
    }
  }

  /** Beyond the limit, the oldest one is forgotten. Without a chosen recipe, there is no step. */
  public RecipeTrail leaving(int place) {
    if (place < 0) {
      return this;
    }
    int kept = Math.min(places.size(), MAX_STEPS - 1);
    List<Integer> last = places.subList(places.size() - kept, places.size());
    return new RecipeTrail(Stream.concat(last.stream(), Stream.of(place)).toList());
  }

  /** Where one step back goes, or nothing at the start. */
  public Optional<Integer> last() {
    return places.isEmpty() ? Optional.empty() : Optional.of(places.get(places.size() - 1));
  }

  public RecipeTrail back() {
    return places.isEmpty() ? this : new RecipeTrail(places.subList(0, places.size() - 1));
  }

  public boolean isEmpty() {
    return places.isEmpty();
  }
}
