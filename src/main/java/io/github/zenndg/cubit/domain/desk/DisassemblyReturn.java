package io.github.zenndg.cubit.domain.desk;

import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * What a disassembled item gives back: only the items of the mod of its recipe, one level down, and
 * one Module of each tier applied to it (answers 2523, 2525 and 2526). In a mark that takes several
 * items, the first of the list that is of the mod comes back, so making and taking apart never
 * create items (answer 2530).
 */
public final class DisassemblyReturn {
  private DisassemblyReturn() {}

  /**
   * The parts, in the order they first show in the recipe, and then the Modules.
   *
   * @param marks the items each mark of the recipe takes, in order
   * @param fromMod whether an item is of the mod
   * @param modules the Modules applied to the item, from the lowest
   */
  public static List<Part> of(
      List<List<String>> marks, Predicate<String> fromMod, List<String> modules) {
    Map<String, Integer> counts = new LinkedHashMap<>();
    for (List<String> mark : marks) {
      mark.stream()
          .filter(fromMod)
          .findFirst()
          .ifPresent(item -> counts.merge(item, 1, Integer::sum));
    }
    List<Part> parts = new ArrayList<>();
    counts.forEach((item, count) -> parts.add(new Part(item, count)));
    modules.forEach(module -> parts.add(new Part(module, 1)));
    return List.copyOf(parts);
  }

  /** Whether the recipe has a part of the mod to give back, which a disassembly needs. */
  public static boolean hasParts(List<List<String>> marks, Predicate<String> fromMod) {
    return marks.stream().anyMatch(mark -> mark.stream().anyMatch(fromMod));
  }

  /** The tiers whose Module took an item from the Base up to its tier, from the lowest. */
  public static List<RobotTier> modulesUpTo(RobotTier tier) {
    List<RobotTier> applied = new ArrayList<>();
    for (RobotTier step : RobotTier.values()) {
      if (step != RobotTier.BASE && step.ordinal() <= tier.ordinal()) {
        applied.add(step);
      }
    }
    return List.copyOf(applied);
  }

  /** An item that comes back and how many. */
  public record Part(String item, int count) {
    private static final String NO_ITEM = "A part needs an item";
    private static final String NO_COUNT = "A part comes back at least once: ";

    public Part {
      if (item == null || item.isBlank()) {
        throw new IllegalArgumentException(NO_ITEM);
      }
      if (count < 1) {
        throw new IllegalArgumentException(NO_COUNT + count);
      }
    }
  }
}
