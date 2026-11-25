package io.github.zenndg.cubit.domain.robot;

import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.PowerState;

/**
 * Whether `Follow` can call the chosen Cubit now, or why not: it comes from anywhere, switched off,
 * from the Dock, from its area or active without the place. Without charge it comes switched off,
 * and one that already follows its owner nearby has nothing to do (answers 2450, 2457 and 2483).
 */
public enum FollowCall {
  READY,
  FOLLOWING,
  NO_CHARGE;

  public static FollowCall of(PowerState state, Battery battery, boolean followingNearby) {
    if (!charged(state, battery)) {
      return NO_CHARGE;
    }
    return followingNearby ? FOLLOWING : READY;
  }

  public static boolean charged(PowerState state, Battery battery) {
    return state != PowerState.INACTIVE_EXHAUSTED && !battery.isEmpty();
  }

  public boolean ready() {
    return this != FOLLOWING;
  }
}
