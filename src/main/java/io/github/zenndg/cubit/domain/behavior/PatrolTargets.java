package io.github.zenndg.cubit.domain.behavior;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The hostile a Patroller attacks: first the one hurting someone, then the nearest to a protected
 * one. Each Patroller keeps its own, and others choose another, but a lone hostile gets them all
 * (answer 1568).
 */
public final class PatrolTargets {
  /** A hostile in reach, as the Patroller sees it. */
  public record Candidate(
      int id, boolean hurtingSomeone, double distanceToProtected, boolean reservedByOther) {}

  private static final Comparator<Candidate> PRIORITY =
      Comparator.comparing((Candidate candidate) -> !candidate.hurtingSomeone())
          .thenComparingDouble(Candidate::distanceToProtected)
          .thenComparingInt(Candidate::id);

  private PatrolTargets() {}

  public static Optional<Integer> choose(List<Candidate> candidates) {
    Optional<Candidate> free =
        candidates.stream().filter(candidate -> !candidate.reservedByOther()).min(PRIORITY);
    return free.or(() -> candidates.stream().min(PRIORITY)).map(Candidate::id);
  }
}
