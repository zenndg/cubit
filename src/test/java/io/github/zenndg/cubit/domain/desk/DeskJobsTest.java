package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DeskJobsTest {
  @Test
  void anIdleDeskAllowsEveryWork() {
    DeskJobs idle = new DeskJobs(false, false, false);
    assertTrue(idle.allowsOrder());
    assertTrue(idle.allowsCascade());
    assertTrue(idle.allowsDisassembly());
  }

  @Test
  void aDisassemblyHoldsTheOrderAndThePlan() {
    DeskJobs taking = new DeskJobs(false, false, true);
    assertFalse(taking.allowsOrder());
    assertFalse(taking.allowsCascade());
    assertTrue(taking.allowsDisassembly());
  }

  @Test
  void anOrderOrAPlanHoldsTheDisassembly() {
    assertFalse(new DeskJobs(true, false, false).allowsDisassembly());
    assertFalse(new DeskJobs(false, true, false).allowsDisassembly());
    assertFalse(new DeskJobs(true, false, false).allowsCascade());
    assertFalse(new DeskJobs(false, true, false).allowsOrder());
  }
}
