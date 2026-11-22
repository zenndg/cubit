package io.github.zenndg.cubit.domain.tier;

import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * What a tier does with the card of the Galactic Patroller: the pistol, the slowness of its orb,
 * the blade, the extra armor, how far it looks, the jump and how many points the route keeps and
 * the side of its area, twice the one of the other cards (answers 1564, 1577, 1579 and 1600).
 */
public record PatrollerSpec(
    Weapons weapons, int armor, int searchRadius, Jump jump, int routePoints, int areaSize) {
  /** The pistol and the blade of a tier. */
  public record Weapons(
      int pistolDamage,
      int pistolRange,
      int pistolIntervalTicks,
      Energy shotCost,
      int slownessLevel,
      int bladeDamage,
      int bladeIntervalTicks,
      int bladeKnockback,
      double bladeReach) {
    private static final String INVALID = "Invalid weapons of the Patroller: ";
    private static final String SEPARATOR = ", ";

    public Weapons {
      if (pistolDamage < 0
          || pistolRange < 1
          || pistolIntervalTicks < 1
          || slownessLevel < 1
          || bladeDamage < 0
          || bladeIntervalTicks < 1
          || bladeKnockback < 0
          || !(bladeReach > 0)) {
        throw new IllegalArgumentException(INVALID + pistolDamage + SEPARATOR + bladeDamage);
      }
    }
  }

  /** How high the wings take it and the energy of each jump. */
  public record Jump(int height, Energy cost) {
    private static final String INVALID = "Invalid jump of the Patroller: ";

    public Jump {
      if (height < 0) {
        throw new IllegalArgumentException(INVALID + height);
      }
    }
  }

  public static final PatrollerSpec NONE =
      new PatrollerSpec(
          new Weapons(0, 1, 1, Energy.ZERO, 1, 0, 1, 0, 1), 0, 1, new Jump(0, Energy.ZERO), 0, 0);

  private static final String INVALID = "Invalid Patroller numbers: armor ";
  private static final String RADIUS = ", radius ";
  private static final String POINTS = ", points ";

  public PatrollerSpec {
    if (armor < 0 || searchRadius < 1 || routePoints < 0 || areaSize < 0) {
      throw new IllegalArgumentException(
          INVALID + armor + RADIUS + searchRadius + POINTS + routePoints);
    }
  }

  /** The area of the Patroller: its own side, with the height of the tier (answer 1600). */
  public AreaSpec area(AreaSpec tier) {
    return new AreaSpec(areaSize, tier.heightBlocks());
  }

  /** A shot needs its whole cost, so an almost empty battery uses the blade (answer 1571). */
  public boolean canShoot(Battery battery) {
    return battery.charge().amount() >= weapons.shotCost().amount();
  }

  public Battery payForShot(Battery battery) {
    return battery.consume(weapons.shotCost()).battery();
  }

  public boolean canJump(Battery battery) {
    return jump.height() > 0 && battery.charge().amount() >= jump.cost().amount();
  }

  public Battery payForJump(Battery battery) {
    return battery.consume(jump.cost()).battery();
  }
}
