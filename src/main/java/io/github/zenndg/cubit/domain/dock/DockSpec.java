package io.github.zenndg.cubit.domain.dock;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;

/**
 * The numbers of an Induction Dock at a tier: the stock, the recharge of the Cubit and of the
 * stored Power Bank, the slots of the storage, in whole rows of the screen, what its panel gives by
 * day and at night, which may be zero, its XP and what it spends for being on (answers 1374, 1385,
 * 1833, 1944 and 2466).
 */
public record DockSpec(
    Energy capacity,
    Energy rechargePerTick,
    int storageSlots,
    SolarOutput solar,
    DockXpSpec xp,
    Energy standbyPerTick) {
  public static final int ROW = 9;
  private static final String NO_CAPACITY = "A dock needs a stock";
  private static final String NO_RECHARGE = "A dock needs a recharge";
  private static final String BAD_SLOTS = "The slots of a dock are whole rows, up to six: ";

  public DockSpec {
    if (capacity.isZero()) {
      throw new IllegalArgumentException(NO_CAPACITY);
    }
    if (rechargePerTick.isZero()) {
      throw new IllegalArgumentException(NO_RECHARGE);
    }
    if (storageSlots < ROW
        || storageSlots > DockStorageRules.MAX_SLOTS
        || storageSlots % ROW != 0) {
      throw new IllegalArgumentException(BAD_SLOTS + storageSlots);
    }
  }

  /** What it spends for being on, only with energy, and nothing at zero (answers 2435 and 2466). */
  public Energy standby(Energy stock) {
    return stock.isZero() ? Energy.ZERO : standbyPerTick;
  }

  /** Full also when only the spending of a tick is missing, as with energy coming in (2509). */
  public boolean isFull(Energy stock) {
    return stock.amount() + standbyPerTick.amount() >= capacity.amount();
  }

  public int storageRows() {
    return storageSlots / ROW;
  }
}
