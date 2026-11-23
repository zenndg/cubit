package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CascadeRunTest {
  private static final String CASING = "casing";
  private static final String ROBOT = "robot";
  private static final CascadeRun RUN =
      new CascadeRun(List.of(new CascadeRun.Step(CASING, 2, 0), new CascadeRun.Step(ROBOT, 1, 0)));
  private static final int THREE = 3;
  private static final int FULL_SIGNAL = 15;
  private static final int TWO_THIRDS = 10;

  /** One task at a time, in order, with its progress (answers 2006 and 2012). */
  @Test
  void theTasksRunInSeries() {
    assertEquals(CascadeTaskStatus.CRAFTING, RUN.statusOf(0));
    assertEquals(CascadeTaskStatus.WAITING, RUN.statusOf(1));

    CascadeRun next = RUN.crafted();
    assertEquals(1, next.steps().getFirst().done());
    assertEquals(CascadeTaskStatus.CRAFTING, next.statusOf(0));

    CascadeRun last = next.crafted();
    assertEquals(CascadeTaskStatus.DONE, last.statusOf(0));
    assertEquals(CascadeTaskStatus.CRAFTING, last.statusOf(1));
  }

  @Test
  void theRunEndsAfterTheLastCraft() {
    CascadeRun end = RUN.crafted().crafted().crafted();

    assertTrue(end.isDone());
    assertFalse(RUN.isDone());
    assertEquals(CascadeTaskStatus.DONE, end.statusOf(1));
    assertTrue(end.current().isEmpty());
    assertThrows(IllegalStateException.class, end::crafted);
  }

  /** The total of the whole plan, for Jade and the comparator (answer 2022). */
  @Test
  void theSignalFallsFromFifteenToZero() {
    assertEquals(THREE, RUN.totalCrafts());
    assertEquals(FULL_SIGNAL, RUN.comparatorSignal());
    assertEquals(TWO_THIRDS, RUN.crafted().comparatorSignal());
    assertEquals(0, RUN.crafted().crafted().crafted().comparatorSignal());
  }

  @Test
  void anInvalidStepIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new CascadeRun.Step(CASING, 0, 0));
    assertThrows(IllegalArgumentException.class, () -> new CascadeRun.Step(CASING, 1, 2));
    assertThrows(IllegalArgumentException.class, () -> new CascadeRun(List.of()));
  }
}
