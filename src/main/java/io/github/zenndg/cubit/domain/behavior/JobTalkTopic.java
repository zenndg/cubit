package io.github.zenndg.cubit.domain.behavior;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import java.util.Optional;

/**
 * What a Cubit with a card says: one of three phrases when the card goes in, and phrases of its
 * work (answers 1583, 1587, 1589 and 1590). The jobs that do not work yet say nothing.
 */
public enum JobTalkTopic {
  PATROLLER_ENTRY(RobotJob.PATROLLER, true, 3),
  PATROLLER_COMBAT(RobotJob.PATROLLER, false, 6),
  PATROLLER_DEFEAT(RobotJob.PATROLLER, false, 4),
  FISHER_ENTRY(RobotJob.FISHER, true, 3),
  FISHER_CATCH(RobotJob.FISHER, false, 5),
  FARMER_ENTRY(RobotJob.FARMER, true, 3),
  FARMER_HARVEST(RobotJob.FARMER, false, 5),
  ORGANIZER_ENTRY(RobotJob.ORGANIZER, true, 3),
  ORGANIZER_STORE(RobotJob.ORGANIZER, false, 5),
  COOK_ENTRY(RobotJob.COOK, true, 3),
  COOK_SERVE(RobotJob.COOK, false, 5);

  private final RobotJob job;
  private final boolean entry;
  private final int phrases;

  JobTalkTopic(RobotJob job, boolean entry, int phrases) {
    this.job = job;
    this.entry = entry;
    this.phrases = phrases;
  }

  public RobotJob job() {
    return job;
  }

  public boolean isEntry() {
    return entry;
  }

  public int phrases() {
    return phrases;
  }

  public static Optional<JobTalkTopic> entryOf(RobotJob job) {
    for (JobTalkTopic topic : values()) {
      if (topic.entry && topic.job == job) {
        return Optional.of(topic);
      }
    }
    return Optional.empty();
  }
}
