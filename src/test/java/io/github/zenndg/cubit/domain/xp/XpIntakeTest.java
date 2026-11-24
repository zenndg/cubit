package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class XpIntakeTest {
  private static final XpRate RATE = new XpRate(20);
  private static final int CAPACITY = 200;
  private static final int POINTS = 12;
  private static final int FIT = 10;
  private static final String UNKNOWN = "unknown";
  private static final int SPILLED = 2;
  private static final XpTank EMPTY = new XpTank(0, CAPACITY);
  private static final XpTank FULL = new XpTank(CAPACITY, CAPACITY);
  private static final XpSettings IGNORE = new XpSettings(XpMode.IGNORE, false);
  private static final XpSettings STORE = XpSettings.INITIAL;
  private static final XpSettings DELETE = new XpSettings(XpMode.STORE, true);
  private static final XpSettings RETURN = new XpSettings(XpMode.RETURN_TO_DOCK, false);
  private static final XpSettings RETURN_DELETE = new XpSettings(XpMode.RETURN_TO_DOCK, true);

  /** Nothing is lost without the player choosing it (answer 1829). */
  @Test
  void aNewCubitStoresAndDeletesNothing() {
    assertEquals(new XpSettings(XpMode.STORE, false), XpSettings.INITIAL);
  }

  @Test
  void ignoringDeletesTheWorkAndTheOrbs() {
    assertEquals(new XpIntake.Work(EMPTY, 0), XpIntake.ofWork(EMPTY, IGNORE, POINTS, RATE));
    assertEquals(new XpIntake.Orb(EMPTY, POINTS), XpIntake.ofOrb(EMPTY, IGNORE, POINTS, RATE));
    assertTrue(XpIntake.collects(FULL, IGNORE, RATE));
  }

  @Test
  void storingSpillsTheWorkAndLeavesTheOrbThatDoesNotFit() {
    assertEquals(new XpIntake.Work(FULL, SPILLED), XpIntake.ofWork(EMPTY, STORE, POINTS, RATE));
    assertEquals(new XpIntake.Orb(FULL, FIT), XpIntake.ofOrb(EMPTY, STORE, POINTS, RATE));
    assertFalse(XpIntake.collects(FULL, STORE, RATE));
    assertTrue(XpIntake.collects(EMPTY, STORE, RATE));
  }

  @Test
  void theExcessGoesOnlyWithItsSwitch() {
    assertEquals(new XpIntake.Work(FULL, 0), XpIntake.ofWork(EMPTY, DELETE, POINTS, RATE));
    assertEquals(new XpIntake.Orb(FULL, POINTS), XpIntake.ofOrb(EMPTY, DELETE, POINTS, RATE));
    assertTrue(XpIntake.collects(FULL, DELETE, RATE));
  }

  /** On its way to the Dock it loses nothing, whatever the switch of the store (answer 2064). */
  @Test
  void returningNeverDeletes() {
    for (XpSettings settings : new XpSettings[] {RETURN, RETURN_DELETE}) {
      assertEquals(
          new XpIntake.Work(FULL, SPILLED), XpIntake.ofWork(EMPTY, settings, POINTS, RATE));
      assertEquals(new XpIntake.Orb(FULL, FIT), XpIntake.ofOrb(EMPTY, settings, POINTS, RATE));
      assertFalse(XpIntake.collects(FULL, settings, RATE));
    }
  }

  @Test
  void aModeIsKnownByItsKey() {
    for (XpMode mode : XpMode.values()) {
      assertEquals(mode, XpMode.fromKey(mode.key()).orElseThrow());
    }
    assertTrue(XpMode.fromKey(UNKNOWN).isEmpty());
  }
}
