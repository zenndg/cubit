package io.github.zenndg.cubit.domain.xp;

import io.github.zenndg.cubit.domain.energy.PowerState;

/**
 * When a Cubit in the mode Return to Dock goes there: active, with a full tank and following,
 * working or escorting, after a fight (answer 1934).
 */
public final class XpReturn {
  private XpReturn() {}

  public static boolean returns(
      XpMode mode, PowerState state, boolean full, boolean following, boolean held) {
    return mode == XpMode.RETURN_TO_DOCK
        && state == PowerState.ACTIVE
        && full
        && following
        && !held;
  }
}
