package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.network.AntennaSignal.State;
import java.util.Optional;

/**
 * What a Signal Antenna plays when its signal changes: it locks the first time after it is placed,
 * gets the signal back after losing it and is heard when it loses it (answers 2552, 2553 and 2561).
 * Stopping after the time without signal plays nothing more.
 */
public enum SignalSound {
  LOCK,
  LOST,
  REGAINED;

  /**
   * @param first whether the antenna has not had signal yet since it was placed
   */
  public static Optional<SignalSound> between(State before, State after, boolean first) {
    boolean was = before == State.LINKED;
    boolean is = after == State.LINKED;
    if (!was && is) {
      return Optional.of(first ? LOCK : REGAINED);
    }
    return was && !is ? Optional.of(LOST) : Optional.empty();
  }
}
