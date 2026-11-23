package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DeskOrderGateTest {
  /** An order starts only with the recipe released and the materials of one (answer 1991). */
  @Test
  void anOrderStartsOnlyReleasedAndWithMaterials() {
    assertTrue(new DeskOrderGate(true, true).allows(false));
    assertFalse(new DeskOrderGate(true, false).allows(false));
    assertFalse(new DeskOrderGate(false, true).allows(false));
    assertFalse(new DeskOrderGate(false, false).allows(false));
  }

  /** Only the start is held, so an order on its way can always grow (answers 1984 and 1985). */
  @Test
  void anActiveOrderIsNeverHeld() {
    assertTrue(new DeskOrderGate(true, false).allows(true));
    assertTrue(new DeskOrderGate(false, false).allows(true));
  }
}
