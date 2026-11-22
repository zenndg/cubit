package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PatrolTargetsTest {
  private static final int ZOMBIE = 1;
  private static final int SKELETON = 2;
  private static final int SPIDER = 3;
  private static final double NEAR = 2;
  private static final double FAR = 10;
  private static final double NOBODY = Double.MAX_VALUE;

  private static PatrolTargets.Candidate calm(int id, double distance) {
    return new PatrolTargets.Candidate(id, false, NOBODY, false, distance);
  }

  private static PatrolTargets.Candidate hurting(int id, double distance) {
    return new PatrolTargets.Candidate(id, false, NOBODY, true, distance);
  }

  private static PatrolTargets.Candidate nearWounded(int id, double toWounded) {
    return new PatrolTargets.Candidate(id, true, toWounded, false, FAR);
  }

  @Test
  void theOneHurtingSomeoneComesFirst() {
    List<PatrolTargets.Candidate> candidates = List.of(calm(ZOMBIE, NEAR), hurting(SKELETON, FAR));
    assertEquals(Optional.of(SKELETON), PatrolTargets.choose(candidates, Optional.empty()));
  }

  @Test
  void thenTheNearestToAProtectedOne() {
    List<PatrolTargets.Candidate> candidates = List.of(calm(ZOMBIE, FAR), calm(SKELETON, NEAR));
    assertEquals(Optional.of(SKELETON), PatrolTargets.choose(candidates, Optional.empty()));
  }

  @Test
  void theOneNearAWoundedPatrollerComesBeforeAll() {
    List<PatrolTargets.Candidate> candidates =
        List.of(hurting(ZOMBIE, NEAR), nearWounded(SKELETON, FAR), nearWounded(SPIDER, NEAR));
    assertEquals(Optional.of(SPIDER), PatrolTargets.choose(candidates, Optional.empty()));
  }

  @Test
  void theTeamKeepsItsTargetUntilItIsGone() {
    List<PatrolTargets.Candidate> candidates = List.of(calm(ZOMBIE, FAR), calm(SKELETON, NEAR));
    assertEquals(Optional.of(ZOMBIE), PatrolTargets.choose(candidates, Optional.of(ZOMBIE)));
  }

  @Test
  void aHostileThatStartsHurtingTakesOver() {
    List<PatrolTargets.Candidate> candidates = List.of(calm(ZOMBIE, NEAR), hurting(SKELETON, FAR));
    assertEquals(Optional.of(SKELETON), PatrolTargets.choose(candidates, Optional.of(ZOMBIE)));
  }

  @Test
  void aTargetOutOfReachIsDropped() {
    List<PatrolTargets.Candidate> candidates = List.of(calm(SKELETON, NEAR));
    assertEquals(Optional.of(SKELETON), PatrolTargets.choose(candidates, Optional.of(ZOMBIE)));
  }

  @Test
  void nothingInReachIsNoTarget() {
    assertTrue(PatrolTargets.choose(List.of(), Optional.of(ZOMBIE)).isEmpty());
  }
}
