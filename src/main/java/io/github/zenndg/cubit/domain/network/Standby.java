package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What a Network Server spends per tick only for being on, apart from its chunk, and what it pays
 * for its Solar Relay, at its tier (answers 2435, 2436 and 2439). Zero makes it free.
 */
public record Standby(Energy server, Energy relay) {
  public static final Standby NONE = new Standby(Energy.ZERO, Energy.ZERO);

  /** With the relay of its pair, which the server pays for. */
  public Energy total(boolean paired) {
    return server.plus(relay(paired));
  }

  public Energy relay(boolean paired) {
    return paired ? relay : Energy.ZERO;
  }
}
