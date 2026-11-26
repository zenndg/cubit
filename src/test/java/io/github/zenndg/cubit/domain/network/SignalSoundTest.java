package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.domain.network.AntennaSignal.State;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SignalSoundTest {
  /** The first signal after placing locks, and a later one comes back (answers 2553 and 2561). */
  @Test
  void theFirstSignalLocksAndALaterOneComesBack() {
    assertEquals(
        Optional.of(SignalSound.LOCK), SignalSound.between(State.NO_SIGNAL, State.LINKED, true));
    assertEquals(
        Optional.of(SignalSound.REGAINED),
        SignalSound.between(State.NO_SIGNAL, State.LINKED, false));
    assertEquals(
        Optional.of(SignalSound.REGAINED), SignalSound.between(State.STOPPED, State.LINKED, false));
  }

  @Test
  void losingTheSignalIsHeardOnce() {
    assertEquals(
        Optional.of(SignalSound.LOST), SignalSound.between(State.LINKED, State.NO_SIGNAL, false));
    assertEquals(Optional.empty(), SignalSound.between(State.NO_SIGNAL, State.STOPPED, false));
    assertEquals(Optional.empty(), SignalSound.between(State.LINKED, State.LINKED, true));
  }
}
