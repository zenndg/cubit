package io.github.zenndg.cubit.domain.behavior;

/**
 * What the Galactic Patroller is doing, as a lookout: on its round, on the alert of a hostile just
 * seen, in the fight, checking on its owner after the last kill or investigating where a hostile
 * was last seen, and whether its visor is down (answers 1623, 1624, 1629, 1721 and 1722).
 *
 * @param phase what it is doing
 * @param left the ticks left of the alert or of the check on the owner
 * @param calm the ticks without a threat since the fight, toward raising the visor
 * @param visorDown the visor down, from the alert until the calm or the end of the investigation
 */
public record PatrolWatch(Phase phase, int left, int calm, boolean visorDown) {
  /** On the round with the visor up. */
  public static final PatrolWatch START = new PatrolWatch(Phase.ROUND, 0, 0, false);

  private static final String INVALID = "Invalid times of the watch: ";
  private static final String SEPARATOR = ", ";

  /** What the Patroller is doing. */
  public enum Phase {
    ROUND,
    ALERT,
    FIGHT,
    OWNER_CHECK,
    INVESTIGATION
  }

  /**
   * How long the alert and the check on the owner last, how near the owner counts, how long without
   * a threat the visor waits to go up and how long out of sight a target is gone (answers 1623,
   * 1629, 1632 and 1721).
   */
  public record Timings(
      int alertTicks,
      int ownerCheckTicks,
      int ownerCheckDistance,
      int visorCalmTicks,
      int lostSightTicks) {
    public Timings {
      if (alertTicks < 0
          || ownerCheckTicks < 0
          || ownerCheckDistance < 1
          || visorCalmTicks < 0
          || lostSightTicks < 0) {
        throw new IllegalArgumentException(
            INVALID + alertTicks + SEPARATOR + ownerCheckTicks + SEPARATOR + ownerCheckDistance);
      }
    }

    public boolean ownerNear(double distance) {
      return distance <= ownerCheckDistance;
    }

    /** Out of sight long enough to be gone, at once with no time set (answer 1721). */
    public boolean outOfSight(int unseenTicks) {
      return unseenTicks >= Math.max(1, lostSightTicks);
    }
  }

  public PatrolWatch {
    left = Math.max(0, left);
    calm = Math.max(0, calm);
  }

  /** It only shoots and swings in the fight. */
  public boolean attacks() {
    return phase == Phase.FIGHT;
  }

  /** It stands and looks: at the hostile on the alert, at the owner on the check. */
  public boolean holdsStill() {
    return phase == Phase.ALERT || phase == Phase.OWNER_CHECK;
  }

  /**
   * A hostile in sight: the alert first, with the visor down, unless it already hurts someone, a
   * fight is on or it is the one being looked for (answers 1623 and 1722).
   */
  public PatrolWatch sighted(boolean hurtingSomeone, boolean lookedFor, Timings times) {
    if (phase == Phase.FIGHT || phase == Phase.ALERT) {
      return this;
    }
    boolean found = phase == Phase.INVESTIGATION && lookedFor;
    if (hurtingSomeone || found || times.alertTicks() == 0) {
      return new PatrolWatch(Phase.FIGHT, 0, 0, true);
    }
    return new PatrolWatch(Phase.ALERT, times.alertTicks(), 0, true);
  }

  /** The last hostile nearby fell: a look at the owner, if near, and then the round (1629). */
  public PatrolWatch killedTheLast(boolean ownerNear, Timings times) {
    if (ownerNear && times.ownerCheckTicks() > 0) {
      return new PatrolWatch(Phase.OWNER_CHECK, times.ownerCheckTicks(), 0, visorDown);
    }
    return new PatrolWatch(Phase.ROUND, 0, 0, visorDown);
  }

  /**
   * The target is gone without a kill: it investigates where it was last seen, when that is within
   * its chase, or else goes back to the round (answer 1624).
   */
  public PatrolWatch lost(boolean worthInvestigating) {
    Phase next = worthInvestigating ? Phase.INVESTIGATION : Phase.ROUND;
    return new PatrolWatch(next, 0, 0, visorDown);
  }

  /** It looked around the last place, and the visor goes up with the round (answer 1629). */
  public PatrolWatch searched() {
    return phase == Phase.INVESTIGATION ? new PatrolWatch(Phase.ROUND, 0, 0, false) : this;
  }

  /** A tick: the alert and the check run out, and the calm raises the visor (answer 1629). */
  public PatrolWatch tick(Timings times) {
    return switch (phase) {
      case ALERT -> left <= 1 ? new PatrolWatch(Phase.FIGHT, 0, 0, true) : countDown();
      case OWNER_CHECK ->
          left <= 1 ? new PatrolWatch(Phase.ROUND, 0, calm + 1, visorDown) : countDown();
      case ROUND -> calmed(times);
      case FIGHT, INVESTIGATION -> this;
    };
  }

  private PatrolWatch countDown() {
    return new PatrolWatch(phase, left - 1, calm + 1, visorDown);
  }

  private PatrolWatch calmed(Timings times) {
    int next = calm + 1;
    return new PatrolWatch(phase, 0, next, visorDown && next < times.visorCalmTicks());
  }
}
