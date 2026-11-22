package io.github.zenndg.cubit.domain.robot;

import java.util.Locale;
import java.util.Optional;

/**
 * The jobs a card of Machine Learning gives the Cubit, one per card (answers 192, 1145 and 1146).
 * The keys are a config contract, in the lists of the jobs each tier accepts (answer 1163).
 */
public enum RobotJob {
  FISHER(true),
  FARMER(true),
  RANCHER(true),
  ORGANIZER(true),
  COOK(true),
  /** It guards, so it never stops for a full inventory and never leaves its area unguarded. */
  PATROLLER(false),
  MINER(true),
  BUILDER(true),
  MECHANIC(true),
  ALCHEMIST(true);

  private final boolean hibernatesWhenFull;

  RobotJob(boolean hibernatesWhenFull) {
    this.hibernatesWhenFull = hibernatesWhenFull;
  }

  /** Whether a full inventory with nowhere to unload puts it to sleep away from the Dock (1639). */
  public boolean hibernatesWhenFull() {
    return hibernatesWhenFull;
  }

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Optional<RobotJob> fromKey(String key) {
    for (RobotJob job : values()) {
      if (job.key().equals(key)) {
        return Optional.of(job);
      }
    }
    return Optional.empty();
  }
}
