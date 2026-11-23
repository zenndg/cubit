package io.github.zenndg.cubit.domain.desk;

import java.util.List;
import java.util.Map;

/** Which item a mark with many takes: the one the input has most, or the first (answer 2033). */
public final class CascadeChoice {
  private CascadeChoice() {}

  public static String of(List<String> options, Map<String, Integer> input) {
    String best = options.getFirst();
    for (String option : options) {
      if (input.getOrDefault(option, 0) > input.getOrDefault(best, 0)) {
        best = option;
      }
    }
    return best;
  }
}
