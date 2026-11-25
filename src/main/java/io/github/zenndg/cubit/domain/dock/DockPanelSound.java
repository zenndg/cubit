package io.github.zenndg.cubit.domain.dock;

import io.github.zenndg.cubit.domain.energy.FoldingPanel;
import io.github.zenndg.cubit.domain.energy.PanelState;
import java.util.Optional;

/**
 * The three sounds of the panel of the Dock, each at its moment of the motion: the deploy when the
 * arms start out, the lock when the two halves meet at the center, and the retract when the arms
 * start back (answers 1861 and 1921).
 */
public enum DockPanelSound {
  DEPLOY,
  LOCK,
  RETRACT;

  /**
   * The sound a tick of the panel plays, if any.
   *
   * @param lockTick the tick of the deploy at which the halves meet
   */
  public static Optional<DockPanelSound> between(
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
