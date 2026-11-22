package io.github.zenndg.cubit.domain.behavior;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The hostile the team of Patrollers attacks, all together: first the one nearest to a wounded
 * Patroller, then the one hurting someone, then the nearest to a protected one. The team keeps its
 * target until it falls or goes out of reach, unless one of a higher kind shows up (answers 1568,
 * 1643 and 1691).
 */
public final class PatrolTargets {
  /** A hostile in reach of the team, as the Patroller sees it. */
  public record Candidate(
      int id,
      boolean nearWounded,
      double distanceToWounded,
      boolean hurtingSomeone,
      double distanceToProtected) {

    /** The kind that wins over the current target: near a wounded one, then hurting someone. */
    int urgency() {
      if (nearWounded) {
        return 0;
      }
      return hurtingSomeone ? 1 : 2;
    }
  }

  private static final Comparator<Candidate> PRIORITY =
      Comparator.comparingInt(Candidate::urgency)
          .thenComparingDouble(Candidate::distanceToWounded)
          .thenComparingDouble(Candidate::distanceToProtected)
          .thenComparingInt(Candidate::id);

  private PatrolTargets() {}

  /** The current target stays while in reach and nothing more urgent shows up. */
  public static Optional<Integer> choose(List<Candidate> candidates, Optional<Integer> current) {
    Optional<Candidate> best = candidates.stream().min(PRIORITY);
    Optional<Candidate> kept =
        current.flatMap(id -> candidates.stream().filter(c -> c.id() == id).findFirst());
    if (kept.isPresent() && best.get().urgency() >= kept.get().urgency()) {
      return Optional.of(kept.get().id());
    }
    return best.map(Candidate::id);
  }
}
