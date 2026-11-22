package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PatrolWatchTest {
  private static final int ALERT = 10;
  private static final int OWNER_CHECK = 20;
  private static final int OWNER_DISTANCE = 16;
  private static final int VISOR_CALM = 60;
  private static final int LOST_SIGHT = 40;
  private static final int NEGATIVE = -1;
  private static final PatrolWatch.Timings TIMES =
      new PatrolWatch.Timings(ALERT, OWNER_CHECK, OWNER_DISTANCE, VISOR_CALM, LOST_SIGHT);

  private static PatrolWatch ticks(PatrolWatch watch, int ticks) {
    PatrolWatch next = watch;
    for (int tick = 0; tick < ticks; tick++) {
      next = next.tick(TIMES);
    }
    return next;
  }

  @Test
  void onTheRoundTheVisorIsUpAndItDoesNotAttack() {
    assertEquals(PatrolWatch.Phase.ROUND, PatrolWatch.START.phase());
    assertFalse(PatrolWatch.START.visorDown());
    assertFalse(PatrolWatch.START.attacks());
  }

  @Test
  void aSightedHostileBringsTheAlertWithTheVisorDownAndThenTheFight() {
    PatrolWatch alert = PatrolWatch.START.sighted(false, false, TIMES);
    assertEquals(PatrolWatch.Phase.ALERT, alert.phase());
    assertTrue(alert.visorDown());
    assertTrue(alert.holdsStill());
    assertFalse(alert.attacks());
    assertEquals(PatrolWatch.Phase.ALERT, ticks(alert, ALERT - 1).phase());
    assertTrue(ticks(alert, ALERT).attacks());
  }

  @Test
  void aHostileHurtingSomeoneSkipsTheAlert() {
    assertTrue(PatrolWatch.START.sighted(true, false, TIMES).attacks());
  }

  @Test
  void anotherTargetInTheFightNeedsNoNewAlert() {
    PatrolWatch fight = PatrolWatch.START.sighted(true, false, TIMES);
    assertTrue(fight.sighted(false, false, TIMES).attacks());
  }

  @Test
  void theLastKillWithTheOwnerNearBringsTheCheckAndThenTheRound() {
    PatrolWatch check = PatrolWatch.START.sighted(true, false, TIMES).killedTheLast(true, TIMES);
    assertEquals(PatrolWatch.Phase.OWNER_CHECK, check.phase());
    assertTrue(check.holdsStill());
    assertEquals(PatrolWatch.Phase.ROUND, ticks(check, OWNER_CHECK).phase());
  }

  @Test
  void theLastKillWithTheOwnerFarGoesBackToTheRound() {
    PatrolWatch round = PatrolWatch.START.sighted(true, false, TIMES).killedTheLast(false, TIMES);
    assertEquals(PatrolWatch.Phase.ROUND, round.phase());
  }

  @Test
  void theVisorGoesUpOnlyAfterTheCalm() {
    PatrolWatch round = PatrolWatch.START.sighted(true, false, TIMES).killedTheLast(false, TIMES);
    assertTrue(ticks(round, VISOR_CALM - 1).visorDown());
    assertFalse(ticks(round, VISOR_CALM).visorDown());
  }

  @Test
  void aHostileDuringTheCheckStartsTheFightAgain() {
    PatrolWatch check = PatrolWatch.START.sighted(true, false, TIMES).killedTheLast(true, TIMES);
    assertTrue(check.sighted(true, false, TIMES).attacks());
  }

  @Test
  void aLostTargetEndsTheFight() {
    assertEquals(
        PatrolWatch.Phase.ROUND, PatrolWatch.START.sighted(true, false, TIMES).lost(false).phase());
  }

  @Test
  void theOwnerCountsAsNearWithinTheDistance() {
    assertTrue(TIMES.ownerNear(OWNER_DISTANCE));
    assertFalse(TIMES.ownerNear(OWNER_DISTANCE + 1));
  }

  @Test
  void rejectsNegativeTimes() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new PatrolWatch.Timings(NEGATIVE, OWNER_CHECK, OWNER_DISTANCE, VISOR_CALM, LOST_SIGHT));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PatrolWatch.Timings(ALERT, OWNER_CHECK, 0, VISOR_CALM, LOST_SIGHT));
  }

  @Test
  void aLostTargetWithinTheChaseIsInvestigatedWithTheVisorDown() {
    PatrolWatch looking = PatrolWatch.START.sighted(true, false, TIMES).lost(true);
    assertEquals(PatrolWatch.Phase.INVESTIGATION, looking.phase());
    assertTrue(looking.visorDown());
    assertFalse(looking.attacks());
  }

  @Test
  void afterTheInvestigationTheVisorGoesUpWithTheRound() {
    PatrolWatch round = PatrolWatch.START.sighted(true, false, TIMES).lost(true).searched();
    assertEquals(PatrolWatch.Phase.ROUND, round.phase());
    assertFalse(round.visorDown());
  }

  @Test
  void theHostileLookedForComesBackWithoutAnAlert() {
    PatrolWatch looking = PatrolWatch.START.sighted(true, false, TIMES).lost(true);
    assertTrue(looking.sighted(false, true, TIMES).attacks());
    assertEquals(PatrolWatch.Phase.ALERT, looking.sighted(false, false, TIMES).phase());
  }

  @Test
  void outOfSightAfterTheTime() {
    assertFalse(TIMES.outOfSight(LOST_SIGHT - 1));
    assertTrue(TIMES.outOfSight(LOST_SIGHT));
    PatrolWatch.Timings atOnce =
        new PatrolWatch.Timings(ALERT, OWNER_CHECK, OWNER_DISTANCE, VISOR_CALM, 0);
    assertTrue(atOnce.outOfSight(1));
    assertFalse(atOnce.outOfSight(0));
  }
}
