package io.github.zenndg.cubit.domain.behavior;

import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * The experience of a kill of the Galactic Patroller as energy: far from its owner, it takes the
 * points that fit in its battery, each worth a little energy, and the rest is left for the owner.
 * Whole points only, so no energy comes from nothing (answers 1728 to 1730 and rule 13).
 *
 * @param energyPerPoint the energy of one point, with 0 to turn it off
 * @param ownerDistance within it the owner takes the experience, as without the card
 */
public record PatrolXp(int energyPerPoint, int ownerDistance) {
  private static final String INVALID = "Invalid experience of the Patroller: ";

  /** The battery after it took the points, and how many it took. */
  public record Absorbed(Battery battery, int points) {}

  public PatrolXp {
    if (energyPerPoint < 0 || ownerDistance < 1) {
      throw new IllegalArgumentException(INVALID + energyPerPoint);
    }
  }

  public boolean ownerNear(double distance) {
    return distance <= ownerDistance;
  }

  public Absorbed absorb(int points, Battery battery, boolean ownerNear) {
    if (ownerNear || energyPerPoint == 0 || points <= 0) {
      return new Absorbed(battery, 0);
    }
    long room = battery.capacity().amount() - battery.charge().amount();
    int fit = (int) Math.min(points, room / energyPerPoint);
    Battery.Charged charged = battery.recharge(new Energy((long) fit * energyPerPoint));
    return new Absorbed(charged.battery(), fit);
  }
}
