package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal.Check;
import org.junit.jupiter.api.Test;

class DisassemblyRefusalTest {
  private static final Check READY = new Check(true, false, true, true, false, false);

  @Test
  void aReadyItemIsTakenApart() {
    assertEquals(DisassemblyRefusal.NONE, DisassemblyRefusal.decide(READY));
    assertTrue(DisassemblyRefusal.NONE.allows());
  }

  @Test
  void theFirstCauseIsTheOneShown() {
    assertEquals(
        DisassemblyRefusal.LOCKED_TIER,
        DisassemblyRefusal.decide(new Check(false, true, false, false, true, true)));
    assertEquals(
        DisassemblyRefusal.EMPTY,
        DisassemblyRefusal.decide(new Check(true, true, false, false, true, true)));
    assertEquals(
        DisassemblyRefusal.NO_PARTS,
        DisassemblyRefusal.decide(new Check(true, false, false, false, true, true)));
    assertEquals(
        DisassemblyRefusal.TOO_FEW,
        DisassemblyRefusal.decide(new Check(true, false, true, false, true, true)));
    assertEquals(
        DisassemblyRefusal.HAS_CONTENTS,
        DisassemblyRefusal.decide(new Check(true, false, true, true, true, true)));
    assertEquals(
        DisassemblyRefusal.BUSY,
        DisassemblyRefusal.decide(new Check(true, false, true, true, false, true)));
    assertFalse(DisassemblyRefusal.BUSY.allows());
  }
}
