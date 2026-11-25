package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DeskOrderGateTest {
  /** An order starts with the materials of one, without any progression (answers 1984, 2400). */
  @Test
  void anOrderStartsOnlyWithMaterials() {
    assertTrue(new DeskOrderGate(true).allows(false));
    assertFalse(new DeskOrderGate(false).allows(false));
  }

  /** Only the start is held, so an order on its way can always grow (answers 1984 and 1985). */
  @Test
  void anActiveOrderIsNeverHeld() {
    assertTrue(new DeskOrderGate(false).allows(true));
  }
}
