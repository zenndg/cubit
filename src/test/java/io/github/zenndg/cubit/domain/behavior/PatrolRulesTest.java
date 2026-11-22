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
  private static final double PUSH_BASE = 0.4;
  private static final double STRAIGHT = 10;
  private static final double SLIGHTLY = 3;
  private static final double WIDE = 6;
  private static final double PUSH_STEP = 0.5;
  private static final int PUSH_LEVEL = 2;
  private static final double PUSH_AT_LEVEL = 1.4;
  private static final double NEGATIVE_PUSH = -0.1;
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

  @Test
  void theBladePushesWithABaseAndAStepPerLevel() {
    PatrolRules.Knockback push = new PatrolRules.Knockback(PUSH_BASE, PUSH_STEP);
    assertEquals(PUSH_BASE, push.strength(0), SHIELD_PRECISION);
    assertEquals(PUSH_AT_LEVEL, push.strength(PUSH_LEVEL), SHIELD_PRECISION);
    assertThrows(
        IllegalArgumentException.class, () -> new PatrolRules.Knockback(NEGATIVE_PUSH, PUSH_STEP));
  }

  @Test
  void aShotHeadsForSomeoneWithinTheCone() {
    assertTrue(PatrolRules.headsFor(1, 0, 0, STRAIGHT, 0, 0));
    assertTrue(PatrolRules.headsFor(1, 0, 0, STRAIGHT, 0, SLIGHTLY));
    assertFalse(PatrolRules.headsFor(1, 0, 0, STRAIGHT, 0, WIDE));
    assertFalse(PatrolRules.headsFor(-1, 0, 0, STRAIGHT, 0, 0));
  }

  @Test
  void aStillShotHeadsForNoOne() {
    assertFalse(PatrolRules.headsFor(0, 0, 0, STRAIGHT, 0, 0));
  }
}
