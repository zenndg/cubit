package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.PowerState;
import org.junit.jupiter.api.Test;

class XpReturnTest {
  @Test
  void aFullTankGoesToTheDockInItsMode() {
    assertTrue(XpReturn.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, true, false));
    assertFalse(XpReturn.returns(XpMode.STORE, PowerState.ACTIVE, true, true, false));
    assertFalse(XpReturn.returns(XpMode.IGNORE, PowerState.ACTIVE, true, true, false));
    assertFalse(XpReturn.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, false, true, false));
  }

  /** As the return of the battery, without its switch, and after the fight (answer 1934). */
  @Test
  void itWaitsForWhatTheReturnOfTheBatteryWaits() {
    assertFalse(XpReturn.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, false, false));
    assertFalse(XpReturn.returns(XpMode.RETURN_TO_DOCK, PowerState.ACTIVE, true, true, true));
    assertFalse(XpReturn.returns(XpMode.RETURN_TO_DOCK, PowerState.HIBERNATING, true, true, false));
  }
}
