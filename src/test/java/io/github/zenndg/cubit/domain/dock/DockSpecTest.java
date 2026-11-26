package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import org.junit.jupiter.api.Test;

class DockSpecTest {
  private static final Energy CAPACITY = new Energy(300_000);
  private static final Energy RECHARGE = new Energy(500);
  private static final SolarOutput SOLAR = new SolarOutput(new Energy(25), Energy.ZERO);
  private static final DockXpSpec XP = new DockXpSpec(32_000, 50);
  private static final int ROWS_OF_THREE = 27;
  private static final int NOT_A_ROW = 28;
  private static final int TOO_MANY = 63;
  private static final int ROWS = 3;
  private static final Energy STANDBY = new Energy(3);
  private static final Energy NEARLY = new Energy(299_997);

  /** The numbers of the Base (1374 and 1385), with the slots in whole rows of the screen. */
  @Test
  void aSpecKeepsItsNumbersAndCountsItsRows() {
    DockSpec spec = new DockSpec(CAPACITY, RECHARGE, ROWS_OF_THREE, SOLAR, XP, STANDBY);

    assertEquals(CAPACITY, spec.capacity());
    assertEquals(RECHARGE, spec.rechargePerTick());
    assertEquals(ROWS, spec.storageRows());
    assertEquals(SOLAR, spec.solar());
    assertEquals(XP, spec.xp());
  }

  @Test
  void itSpendsOnlyWithEnergyAndIsFullWithinATick() {
    DockSpec spec = new DockSpec(CAPACITY, RECHARGE, ROWS_OF_THREE, SOLAR, XP, STANDBY);

    assertEquals(STANDBY, spec.standby(CAPACITY));
    assertEquals(Energy.ZERO, spec.standby(Energy.ZERO));
    assertTrue(spec.isFull(CAPACITY));
    assertTrue(spec.isFull(NEARLY));
    assertFalse(spec.isFull(NEARLY.minus(new Energy(1))));
  }

  @Test
  void theXpOfADockNeedsATankAndAPace() {
    assertThrows(IllegalArgumentException.class, () -> new DockXpSpec(0, 1));
    assertThrows(IllegalArgumentException.class, () -> new DockXpSpec(1, 0));
  }

  @Test
  void anInvalidSpecIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new DockSpec(Energy.ZERO, RECHARGE, ROWS_OF_THREE, SOLAR, XP, STANDBY));
    assertThrows(
        IllegalArgumentException.class,
        () -> new DockSpec(CAPACITY, Energy.ZERO, ROWS_OF_THREE, SOLAR, XP, STANDBY));
    assertThrows(
        IllegalArgumentException.class,
        () -> new DockSpec(CAPACITY, RECHARGE, NOT_A_ROW, SOLAR, XP, STANDBY));
    assertThrows(
        IllegalArgumentException.class,
        () -> new DockSpec(CAPACITY, RECHARGE, TOO_MANY, SOLAR, XP, STANDBY));
  }
}
