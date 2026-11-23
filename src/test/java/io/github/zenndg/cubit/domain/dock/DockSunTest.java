package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DockSunTest {
  /** Only by day, under the open sky, without rain, where the world has a sun (answer 1384). */
  @Test
  void thePanelGeneratesOnlyUnderTheSun() {
    assertTrue(generates(new DockSun(true, true, false, true, true, false)));
    assertFalse(generates(new DockSun(false, true, false, true, true, false)));
    assertFalse(generates(new DockSun(true, false, false, true, true, false)));
    assertFalse(generates(new DockSun(true, true, true, true, true, false)));
    assertFalse(generates(new DockSun(true, true, false, false, true, false)));
  }

  /** The Solar tab tells why the panel stops, the most lasting reason first (answer 1396). */
  @Test
  void theStateTellsWhyThePanelStops() {
    assertEquals(
        DockSunState.GENERATING, new DockSun(true, true, false, true, true, false).state());
    assertEquals(DockSunState.NO_SUN, new DockSun(true, true, false, false, true, false).state());
    assertEquals(DockSunState.COVERED, new DockSun(false, false, true, true, true, false).state());
    assertEquals(DockSunState.NIGHT, new DockSun(false, true, true, true, true, false).state());
    assertEquals(DockSunState.RAIN, new DockSun(true, true, true, true, true, false).state());
  }

  /**
   * A Dock without the third block, from before it had one, has no room for the panel, and only a
   * world without a sun is a more lasting reason (answer 1897).
   */
  @Test
  void withoutTheThirdBlockThereIsNoRoomForThePanel() {
    assertEquals(DockSunState.NO_ROOM, new DockSun(true, true, false, true, false, false).state());
    assertEquals(DockSunState.NO_ROOM, new DockSun(false, false, true, true, false, false).state());
    assertEquals(DockSunState.NO_SUN, new DockSun(true, true, false, false, false, false).state());
    assertFalse(generates(new DockSun(true, true, false, true, false, false)));
  }

  /** At a tier that gives at night, a clear night keeps the panel at work (answer 1943). */
  @Test
  void theMoonGivesAtATierThatGeneratesAtNight() {
    DockSun moon = new DockSun(false, true, false, true, true, true);

    assertEquals(DockSunState.MOONLIGHT, moon.state());
    assertTrue(generates(moon));
    assertEquals(DockSunState.GENERATING, new DockSun(true, true, false, true, true, true).state());
  }

  /** The moon follows the rules of the day: the sky, the rain, the world and the room (1943). */
  @Test
  void theMoonFollowsTheRulesOfTheDay() {
    assertEquals(DockSunState.RAIN, new DockSun(false, true, true, true, true, true).state());
    assertEquals(DockSunState.COVERED, new DockSun(false, false, false, true, true, true).state());
    assertEquals(DockSunState.NO_SUN, new DockSun(false, true, false, false, true, true).state());
    assertEquals(DockSunState.NO_ROOM, new DockSun(false, true, false, true, false, true).state());
  }

  private static boolean generates(DockSun sun) {
    return sun.state().generates();
  }
}
