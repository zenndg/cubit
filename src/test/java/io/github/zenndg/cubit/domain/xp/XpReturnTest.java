package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.PowerState;
import org.junit.jupiter.api.Test;

class XpReturnTest {
  private static final XpReturn START = XpReturn.START;

  @Test
  void aFullTankGoesToTheDockInItsMode() {
    assertTrue(START.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, true, false));
    assertFalse(START.returns(XpMode.STORE, PowerState.ACTIVE, true, true, false));
    assertFalse(START.returns(XpMode.IGNORE, PowerState.ACTIVE, true, true, false));
    assertFalse(START.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, false, true, false));
  }

  /** As the return of the battery, without its switch, and after the fight (answer 1934). */
  @Test
  void itWaitsForWhatTheReturnOfTheBatteryWaits() {
    assertFalse(START.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, false, false));
    assertFalse(START.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, true, true));
    assertFalse(START.returns(XpMode.RETURN_TO_DOCK, PowerState.HIBERNATING, true, true, false));
  }

  /** After a trip that emptied nothing it waits, not to go back and forth (answer 2063). */
  @Test
  void aTripInVainWaitsForTheTankToLower() {
    XpReturn vain = START.leftTheDock(true);
    assertFalse(vain.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, true, false));
    assertEquals(vain, vain.update(true));
    XpReturn lowered = vain.update(false);
    assertTrue(lowered.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, true, false));
    assertEquals(START, START.leftTheDock(false));
  }

  @Test
  void aTripInVainEndsWhenTheDockHasRoom() {
    XpReturn vain = START.leftTheDock(true);
    assertTrue(vain.inVain());
    assertEquals(START, vain.dockHasRoom());
  }
}
