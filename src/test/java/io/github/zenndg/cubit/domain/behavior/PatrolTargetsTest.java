package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PatrolTargetsTest {
  private static final int ZOMBIE = 1;
  private static final int SKELETON = 2;
  private static final double NEAR = 2;
  private static final double FAR = 10;

  @Test
  void theOneHurtingSomeoneComesFirst() {
    List<PatrolTargets.Candidate> candidates =
        List.of(
            new PatrolTargets.Candidate(ZOMBIE, false, NEAR, false),
            new PatrolTargets.Candidate(SKELETON, true, FAR, false));
    assertEquals(Optional.of(SKELETON), PatrolTargets.choose(candidates));
  }

  @Test
  void thenTheNearestToAProtectedOne() {
    List<PatrolTargets.Candidate> candidates =
        List.of(
            new PatrolTargets.Candidate(ZOMBIE, false, FAR, false),
            new PatrolTargets.Candidate(SKELETON, false, NEAR, false));
    assertEquals(Optional.of(SKELETON), PatrolTargets.choose(candidates));
  }

  @Test
  void anotherPatrollerKeepsItsTarget() {
    List<PatrolTargets.Candidate> candidates =
        List.of(
            new PatrolTargets.Candidate(ZOMBIE, true, NEAR, true),
            new PatrolTargets.Candidate(SKELETON, false, FAR, false));
    assertEquals(Optional.of(SKELETON), PatrolTargets.choose(candidates));
  }

  @Test
  void aLoneHostileGetsThemAll() {
    List<PatrolTargets.Candidate> candidates =
        List.of(new PatrolTargets.Candidate(ZOMBIE, false, NEAR, true));
    assertEquals(Optional.of(ZOMBIE), PatrolTargets.choose(candidates));
  }

  @Test
  void nothingInReachIsNoTarget() {
    assertTrue(PatrolTargets.choose(List.of()).isEmpty());
  }
}
