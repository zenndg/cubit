package io.github.zenndg.cubit.domain.behavior;

/**
 * The numbers of the Galactic Patroller that hold for every tier: the chance and the time of the
 * slowness, the chance of a head, how far it chases from the route, where it changes weapons and
 * the pace of its round (answers 1557, 1559, 1562, 1577, 1614 to 1618 and 1628).
 */
public record PatrolSettings(
    int slownessChancePercent,
    int slownessTicks,
    int headChancePercent,
    int chaseDistance,
    int weaponSwitchDistance,
    Round round) {
  /**
   * The pace of the round: the share of the normal speed it walks at, and the limits of each stop
   * and of the blocks it walks between two stops. Each stop and each leg is drawn between its
   * limits by the caller, so the domain stays free of randomness (answers 1614 to 1618 and 1628).
   */
  public record Round(
      double speed, int stopMinTicks, int stopMaxTicks, int stopMinDistance, int stopMaxDistance) {
    private static final String INVALID_ROUND = "Invalid round of the patrol: ";

    public Round {
      if (!(speed > 0 && speed <= 1)
          || stopMinTicks < 0
          || stopMaxTicks < stopMinTicks
          || stopMinDistance < 1
          || stopMaxDistance < stopMinDistance) {
        throw new IllegalArgumentException(
            INVALID_ROUND + speed + SEPARATOR + stopMinTicks + SEPARATOR + stopMaxTicks);
      }
    }

    /** How long a stop lasts, from a drawn number kept within the limits. */
    public int stopTicks(int drawn) {
      return Math.clamp(drawn, stopMinTicks, stopMaxTicks);
    }

    /** How many blocks it walks to the next stop, from a drawn number kept within the limits. */
    public int stopDistance(int drawn) {
      return Math.clamp(drawn, stopMinDistance, stopMaxDistance);
    }
  }

  private static final int PERCENT = 100;
  private static final String INVALID = "Invalid patrol settings: ";
  private static final String SEPARATOR = ", ";

  public PatrolSettings {
    if (outOfPercent(slownessChancePercent)
        || outOfPercent(headChancePercent)
        || slownessTicks < 1
        || chaseDistance < 0
        || weaponSwitchDistance < 0
        || round == null) {
      throw new IllegalArgumentException(
          INVALID + slownessChancePercent + SEPARATOR + headChancePercent);
    }
  }

  private static boolean outOfPercent(int value) {
    return value < 0 || value > PERCENT;
  }

  /** The draw is made by the caller, so the domain needs no randomness. */
  public boolean slows(int roll) {
    return roll < slownessChancePercent;
  }

  public boolean dropsHead(int roll) {
    return roll < headChancePercent;
  }
}
