package io.github.zenndg.cubit.domain.behavior;

import io.github.zenndg.cubit.domain.robot.RobotJob;

/**
 * When a Cubit with a card speaks. The phrase of the card goes to whoever closed the screen, and
 * the ones of the work to the owner nearby, even when the Cubit does not follow, with a chance and
 * a minimum interval, so a large farm does not turn into spam (answers 1585 and 1586). The draw is
 * made by the caller.
 */
public final class JobTalkRules {
  private JobTalkRules() {}

  public static boolean drawSpeaks(int roll, int chancePercent) {
    return roll < chancePercent;
  }

  public static boolean hears(boolean sameDimension, double distance, int range) {
    return sameDimension && distance <= range;
  }

  /** Both kinds obey the switch of the chatter and only come with a calm face. */
  public static boolean mayTalk(boolean switchedOn, boolean calm) {
    return switchedOn && calm;
  }

  /** A card put in place of another, or in an empty slot, gets its phrase. */
  public static boolean isNewJob(RobotJob before, RobotJob after) {
    return after != null && after != before;
  }
}
