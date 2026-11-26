package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PanelAimTest {
  private static final long DAWN = 0;
  private static final long NOON = 6_000;
  private static final long DUSK = 12_000;
  private static final long MIDNIGHT = 18_000;
  private static final double EDGE = 11;
  private static final double FLAT = 90;
  private static final double NOON_TILT = 55;
  private static final double LOW_TILT = 32;

  private static PanelAim aim(PanelAim.Side side, long time) {
    return PanelAim.of(side, time);
  }

  @Test
  void facingNorthOrSouthItTurnsToItsSide() {
    assertEquals(new PanelAim(LOW_TILT, EDGE), aim(PanelAim.Side.NORTH, DAWN));
    assertEquals(new PanelAim(LOW_TILT, -EDGE), aim(PanelAim.Side.NORTH, DUSK));
    assertEquals(new PanelAim(LOW_TILT, -EDGE), aim(PanelAim.Side.SOUTH, DAWN));
    assertEquals(new PanelAim(LOW_TILT, EDGE), aim(PanelAim.Side.SOUTH, DUSK));
    assertEquals(NOON_TILT, aim(PanelAim.Side.SOUTH, NOON).tilt());
  }

  @Test
  void facingEastOrWestItGoesAroundFlat() {
    assertEquals(new PanelAim(FLAT - EDGE, 0), aim(PanelAim.Side.EAST, DAWN));
    assertEquals(new PanelAim(FLAT + EDGE, 0), aim(PanelAim.Side.EAST, DUSK));
    assertEquals(new PanelAim(FLAT + EDGE, 0), aim(PanelAim.Side.WEST, DAWN));
    assertEquals(new PanelAim(FLAT - EDGE, 0), aim(PanelAim.Side.WEST, DUSK));
    assertEquals(new PanelAim(FLAT, 0), aim(PanelAim.Side.EAST, NOON));
  }

  @Test
  void byTheMoonItLiesFlat() {
    for (PanelAim.Side side : PanelAim.Side.values()) {
      assertEquals(new PanelAim(FLAT, 0), aim(side, MIDNIGHT));
    }
  }
}
