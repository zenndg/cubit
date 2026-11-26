package io.github.zenndg.cubit.domain.ownership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FloatingNameTest {
  private static final int FLOOR = 64;
  private static final int TWO_BLOCKS = 2;
  private static final double CENTER = 0.5;
  private static final FloatingName NAME = FloatingName.over(0, FLOOR, 0, TWO_BLOCKS);

  private static FloatingName.Viewer owner(double x) {
    return new FloatingName.Viewer(true, true, false, false, x, NAME.y(), CENTER);
  }

  @Test
  void itFloatsHalfABlockAboveTheTopOverTheMiddle() {
    assertEquals(CENTER, NAME.x());
    assertEquals(FLOOR + TWO_BLOCKS + FloatingName.ABOVE_TOP, NAME.y());
    assertEquals(CENTER, NAME.z());
  }

  @Test
  void theOwnerWithTheTabletSeesItWithinReach() {
    assertTrue(NAME.seenBy(owner(CENTER)));
    assertTrue(NAME.seenBy(owner(CENTER + FloatingName.REACH)));
    assertFalse(NAME.seenBy(owner(CENTER + FloatingName.REACH + 1)));
  }

  @Test
  void nobodyElseSeesItNorTheOwnerWithoutTheTablet() {
    double y = NAME.y();
    assertFalse(NAME.seenBy(new FloatingName.Viewer(false, true, false, false, CENTER, y, CENTER)));
    assertFalse(NAME.seenBy(new FloatingName.Viewer(true, false, false, false, CENTER, y, CENTER)));
  }

  @Test
  void aScreenOrTheHiddenInterfaceHidesIt() {
    double y = NAME.y();
    assertFalse(NAME.seenBy(new FloatingName.Viewer(true, true, true, false, CENTER, y, CENTER)));
    assertFalse(NAME.seenBy(new FloatingName.Viewer(true, true, false, true, CENTER, y, CENTER)));
  }

  @Test
  void theClientLooksForTheBlocksEveryTenTicks() {
    assertTrue(FloatingName.looksFor(0));
    assertFalse(FloatingName.looksFor(FloatingName.LOOK_INTERVAL_TICKS - 1));
    assertTrue(FloatingName.looksFor(FloatingName.LOOK_INTERVAL_TICKS));
  }

  @Test
  void aBlockHasAHeight() {
    assertThrows(IllegalArgumentException.class, () -> FloatingName.over(0, FLOOR, 0, 0));
  }
}
