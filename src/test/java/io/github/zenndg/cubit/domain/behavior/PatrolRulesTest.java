package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.robot.DefenseMode;
import org.junit.jupiter.api.Test;

class PatrolRulesTest {
  private static final double SHIELD_CREEPER = 10;
  private static final double SHIELD_GUARDED = 30;
  private static final double SHIELD_KEEP = 6;
  private static final int SHIELD_KEEP_BLOCKS = 6;
  private static final double SHIELD_PRECISION = 1e-9;
  private static final int CHASE = 16;
  private static final int JUMP = 3;
  private static final double BLAST = 4;
  private static final double HALF_BLOCK = 0.5;
  private static final double LOW = 2;
  private static final double TOO_HIGH = JUMP + 1;
  private static final double OUT_OF_BLAST = BLAST + 1;

  @Test
  void theCardLocksTheAggressiveMode() {
    assertEquals(DefenseMode.AGGRESSIVE, PatrolRules.mode(DefenseMode.PEACEFUL, true));
    assertEquals(DefenseMode.PEACEFUL, PatrolRules.mode(DefenseMode.PEACEFUL, false));
  }

  @Test
  void theCardLocksEveryModeButTheAggressive() {
    assertTrue(PatrolRules.lockedByCard(DefenseMode.PEACEFUL, true));
    assertTrue(PatrolRules.lockedByCard(DefenseMode.RETALIATE, true));
    assertFalse(PatrolRules.lockedByCard(DefenseMode.AGGRESSIVE, true));
    assertFalse(PatrolRules.lockedByCard(DefenseMode.PEACEFUL, false));
  }

  @Test
  void chasesOnlySoFarFromTheRoute() {
    assertTrue(PatrolRules.mayChase(CHASE, CHASE));
    assertFalse(PatrolRules.mayChase(CHASE + HALF_BLOCK, CHASE));
  }

  @Test
  void jumpsAtATargetInTheAirItCannotShoot() {
    assertTrue(PatrolRules.jumps(new PatrolRules.JumpCheck(true, LOW, JUMP, false, true, true)));
  }

  @Test
  void doesNotJumpWhenItCanShoot() {
    assertFalse(PatrolRules.jumps(new PatrolRules.JumpCheck(true, LOW, JUMP, true, true, true)));
  }

  @Test
  void doesNotJumpTooHighOrWithoutCharge() {
    assertFalse(
        PatrolRules.jumps(new PatrolRules.JumpCheck(true, TOO_HIGH, JUMP, false, true, true)));
    assertFalse(PatrolRules.jumps(new PatrolRules.JumpCheck(true, LOW, JUMP, false, false, true)));
    assertFalse(PatrolRules.jumps(new PatrolRules.JumpCheck(true, LOW, JUMP, false, true, false)));
    assertFalse(PatrolRules.jumps(new PatrolRules.JumpCheck(false, LOW, JUMP, false, true, true)));
  }

  @Test
  void backsAwayFromASwellingCreeper() {
    assertTrue(PatrolRules.backsAway(true, LOW, BLAST));
    assertFalse(PatrolRules.backsAway(false, LOW, BLAST));
    assertFalse(PatrolRules.backsAway(true, OUT_OF_BLAST, BLAST));
  }

  @Test
  void theShieldStandsOutOfTheBlastOnTheWayToTheProtectedOne() {
    PatrolSiege.Spot spot =
        PatrolRules.shield(
            SHIELD_CREEPER, SHIELD_CREEPER, SHIELD_GUARDED, SHIELD_CREEPER, SHIELD_KEEP);
    assertEquals(SHIELD_CREEPER + SHIELD_KEEP, spot.x(), SHIELD_PRECISION);
    assertEquals(SHIELD_CREEPER, spot.z(), SHIELD_PRECISION);
  }

  @Test
  void theDistancesOfTheCreeperAreAtLeastOneBlock() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolRules.CreeperDistances(0, SHIELD_KEEP_BLOCKS));
  }
}
