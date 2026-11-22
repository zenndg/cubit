package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JobTalkTopicTest {
  private static final int ENTRY_PHRASES = 3;
  private static final List<RobotJob> SPEAKING_JOBS =
      List.of(
          RobotJob.PATROLLER, RobotJob.FISHER, RobotJob.FARMER, RobotJob.ORGANIZER, RobotJob.COOK);

  @Test
  void everyJobThatWorksHasThreeEntryPhrases() {
    for (RobotJob job : SPEAKING_JOBS) {
      Optional<JobTalkTopic> entry = JobTalkTopic.entryOf(job);
      assertTrue(entry.isPresent());
      assertEquals(ENTRY_PHRASES, entry.get().phrases());
      assertTrue(entry.get().isEntry());
      assertEquals(job, entry.get().job());
    }
  }

  @Test
  void aJobThatDoesNotWorkYetSaysNothing() {
    assertTrue(JobTalkTopic.entryOf(RobotJob.MINER).isEmpty());
  }

  @Test
  void theWorkTopicsAreNotEntries() {
    assertFalse(JobTalkTopic.PATROLLER_COMBAT.isEntry());
    assertEquals(RobotJob.PATROLLER, JobTalkTopic.PATROLLER_DEFEAT.job());
  }

  @Test
  void eachTopicHasPhrases() {
    for (JobTalkTopic topic : JobTalkTopic.values()) {
      assertTrue(topic.phrases() > 0);
    }
  }
}
