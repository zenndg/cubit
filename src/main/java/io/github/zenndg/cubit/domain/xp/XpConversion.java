package io.github.zenndg.cubit.domain.xp;

import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * How much energy a point of XP becomes (answer 1942). It goes only one way: XP that became energy
 * is never XP again, so nothing here turns energy into points (rule 58 and answer 2111).
 */
public record XpConversion(XpRate rate, int energyPerPoint) {
  private static final String INVALID = "A point needs some energy: ";

  public XpConversion {
    if (energyPerPoint < 1) {
      throw new IllegalArgumentException(INVALID + energyPerPoint);
    }
  }

  public Energy energyOf(int points) {
    return new Energy((long) points * energyPerPoint);
  }

  /** How many whole points have room for all of their energy (detail 11 of the concept). */
  public int pointsFitting(Energy room) {
    return (int) Math.min(room.amount() / energyPerPoint, Integer.MAX_VALUE);
  }
}
