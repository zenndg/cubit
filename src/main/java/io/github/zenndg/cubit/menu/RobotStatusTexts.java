package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.behavior.HibernationReason;
import io.github.zenndg.cubit.domain.behavior.RobotScreenStatus;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import java.util.Optional;
import net.minecraft.network.chat.Component;

/** The text of the state of a Cubit, for the screens and the commands. */
public final class RobotStatusTexts {
  private RobotStatusTexts() {}

  /**
   * The text of a state, with the job while working and the reason while hibernating away, as the
   * top of the screens and the list of the Tablet show it (answer 1168).
   */
  public static Component status(
      RobotScreenStatus status, Optional<RobotJob> job, Optional<HibernationReason> reason) {
    String key = ScreenKeys.robotStatus(status);
    return switch (status) {
      case WORKING -> Component.translatable(key, jobName(job));
      case HIBERNATING_AWAY ->
          Component.translatable(
              key,
              Component.translatable(
                  WorkKeys.reason(reason.orElse(HibernationReason.INVENTORY_FULL))));
      default -> Component.translatable(key);
    };
  }

  public static Component jobName(Optional<RobotJob> job) {
    return job.map(found -> (Component) Component.translatable(WorkKeys.job(found)))
        .orElseGet(() -> Component.translatable(WorkKeys.NO_JOB));
  }
}
