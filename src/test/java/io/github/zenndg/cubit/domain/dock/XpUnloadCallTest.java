package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class XpUnloadCallTest {
  /** The same reasons as Go to Dock, before the one of the empty tank (2079 and 2107). */
  @ParameterizedTest
  @EnumSource(DockGoal.class)
  void itHasTheReasonsOfGoToDock(DockGoal goal) {
    assertEquals(goal.name(), XpUnloadCall.of(goal, false).name());
    XpUnloadCall empty = XpUnloadCall.of(goal, true);
    assertEquals(goal.ready() ? XpUnloadCall.EMPTY.name() : goal.name(), empty.name());
  }

  @Test
  void onlyReadyGoes() {
    for (XpUnloadCall call : XpUnloadCall.values()) {
      assertEquals(call == XpUnloadCall.READY, call.ready());
    }
    assertTrue(XpUnloadCall.of(DockGoal.READY, false).ready());
    assertFalse(XpUnloadCall.of(DockGoal.READY, true).ready());
  }
}
