package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import org.junit.jupiter.api.Test;

class JobTalkRulesTest {
  private static final int CHANCE = 20;
  private static final int LUCKY = 19;
  private static final int UNLUCKY = 20;
  private static final int RANGE = 32;
  private static final double NEAR = 31.9;
  private static final double FAR = 32.1;

  @Test
  void aDrawUnderTheChanceSpeaks() {
    assertTrue(JobTalkRules.drawSpeaks(LUCKY, CHANCE));
    assertFalse(JobTalkRules.drawSpeaks(UNLUCKY, CHANCE));
  }

  @Test
  void theOwnerHearsWithinTheRangeInTheSameDimension() {
    assertTrue(JobTalkRules.hears(true, NEAR, RANGE));
    assertFalse(JobTalkRules.hears(true, FAR, RANGE));
    assertFalse(JobTalkRules.hears(false, NEAR, RANGE));
  }

  @Test
  void itSpeaksOnlyWithTheSwitchOnAndCalm() {
    assertTrue(JobTalkRules.mayTalk(true, true));
    assertFalse(JobTalkRules.mayTalk(false, true));
    assertFalse(JobTalkRules.mayTalk(true, false));
  }

  @Test
  void aNewJobSpeaksAndTheSameJobDoesNot() {
    assertTrue(JobTalkRules.isNewJob(null, RobotJob.FISHER));
    assertTrue(JobTalkRules.isNewJob(RobotJob.COOK, RobotJob.FISHER));
    assertFalse(JobTalkRules.isNewJob(RobotJob.FISHER, RobotJob.FISHER));
    assertFalse(JobTalkRules.isNewJob(RobotJob.FISHER, null));
  }
}
