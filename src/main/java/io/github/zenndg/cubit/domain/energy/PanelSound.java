package io.github.zenndg.cubit.domain.energy;

import java.util.Optional;

/**
 * The three sounds of a panel, of a Dock or of a Solar Relay, each at its moment of the motion: the
 * deploy when it starts to open, the lock when its parts meet, and the retract when it starts back
 * (answers 1861, 1921 and 2339).
 */
public enum PanelSound {
  DEPLOY,
  LOCK,
  RETRACT;

  /**
   * The sound a tick of the panel plays, if any.
   *
   * @param lockTick the tick of the deploy at which the parts meet
   */
  public static Optional<PanelSound> between(
      FoldingPanel before, FoldingPanel after, int lockTick) {
    if (after.state() == PanelState.DEPLOYING && before.state() != PanelState.DEPLOYING) {
      return Optional.of(DEPLOY);
    }
    if (after.state() == PanelState.RETRACTING && before.state() != PanelState.RETRACTING) {
      return Optional.of(RETRACT);
    }
    boolean locks =
        after.state() == PanelState.DEPLOYING
            && before.ticks() < lockTick
            && after.ticks() >= lockTick;
    return locks ? Optional.of(LOCK) : Optional.empty();
  }
}
