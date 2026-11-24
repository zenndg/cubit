package io.github.zenndg.cubit.domain.xp;

import io.github.zenndg.cubit.domain.energy.PowerState;

/**
 * When a Cubit in the mode Return to Dock goes there: active, with a full tank and following,
 * working or escorting, after a fight (answer 1934). After a trip that emptied nothing it waits for
 * room in the Dock or for its tank to lower, not to go back and forth (answer 2063).
 */
public record XpReturn(boolean inVain) {
  public static final XpReturn START = new XpReturn(false);

  public boolean returns(
      XpMode mode, PowerState state, boolean full, boolean following, boolean held) {
    return mode == XpMode.RETURN_TO_DOCK
        && state == PowerState.ACTIVE
        && full
        && following
        && !held
        && !inVain;
  }

  public XpReturn leftTheDock(boolean stillFull) {
    return new XpReturn(stillFull);
  }

  public XpReturn update(boolean full) {
    return new XpReturn(inVain && full);
  }

  public XpReturn dockHasRoom() {
    return START;
  }
}
