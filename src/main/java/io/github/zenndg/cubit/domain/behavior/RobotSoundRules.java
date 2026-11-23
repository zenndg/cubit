package io.github.zenndg.cubit.domain.behavior;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Which sound each situation plays. The caller informs the situation and plays the sound. */
public final class RobotSoundRules {
  private RobotSoundRules() {}

  /** Entering the base has the recharge sound, and leaving it switched off changes nothing. */
  public static Optional<PowerSound> powerSound(PowerState from, PowerState to) {
    if (to == PowerState.ACTIVE && from != PowerState.ACTIVE) {
      return Optional.of(PowerSound.POWER_ON);
    }
    if (from == PowerState.ACTIVE && to == PowerState.INACTIVE_EXHAUSTED
        || from == PowerState.ACTIVE && to == PowerState.INACTIVE_SWITCHED_OFF) {
      return Optional.of(PowerSound.POWER_OFF);
    }
    return Optional.empty();
  }

  public static Set<ContinuousSound> playing(
      PowerState state, boolean walking, boolean burning, boolean charging) {
    Set<ContinuousSound> sounds = EnumSet.noneOf(ContinuousSound.class);
    if (state == PowerState.ACTIVE && walking) {
      sounds.add(ContinuousSound.TRACKS);
    }
    if (burning) {
      sounds.add(ContinuousSound.SHORT_CIRCUIT);
    }
    if (charging) {
      sounds.add(ContinuousSound.RECHARGING);
    }
    return sounds;
  }

  /**
   * Each part of the Patroller that changed plays its sound, in the order visor, blade and wings,
   * also when the card leaves and everything goes away (answers 1792, 1797 and 1799).
   */
  public static List<PatrolSound> patrolSounds(PatrolGear from, PatrolGear to) {
    List<PatrolSound> sounds = new ArrayList<>();
    change(sounds, from.visorDown(), to.visorDown(), PatrolSound.VISOR_DOWN, PatrolSound.VISOR_UP);
    change(sounds, from.bladeLit(), to.bladeLit(), PatrolSound.BLADE_IGNITE, PatrolSound.BLADE_OFF);
    change(
        sounds, from.wingsOpen(), to.wingsOpen(), PatrolSound.WINGS_OPEN, PatrolSound.WINGS_CLOSE);
    return sounds;
  }

  private static void change(
      List<PatrolSound> sounds, boolean before, boolean after, PatrolSound on, PatrolSound off) {
    if (!before && after) {
      sounds.add(on);
    } else if (before && !after) {
      sounds.add(off);
    }
  }

  /** A full battery or an empty dock change nothing, so they play no recharge sound. */
  public static boolean isCharging(Energy before, Energy after) {
    return after.amount() > before.amount();
  }
}
