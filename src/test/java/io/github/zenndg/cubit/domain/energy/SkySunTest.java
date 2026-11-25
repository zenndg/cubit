package io.github.zenndg.cubit.domain.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SkySunTest {
  /** Only by day, under the open sky, without rain, where the world has a sun (answer 1384). */
  @Test
  void thePanelGeneratesOnlyUnderTheSun() {
    assertTrue(generates(new SkySun(true, true, false, true, true, false)));
    assertFalse(generates(new SkySun(false, true, false, true, true, false)));
    assertFalse(generates(new SkySun(true, false, false, true, true, false)));
    assertFalse(generates(new SkySun(true, true, true, true, true, false)));
    assertFalse(generates(new SkySun(true, true, false, false, true, false)));
  }

  /** The Solar tab tells why the panel stops, the most lasting reason first (answer 1396). */
  @Test
  void theStateTellsWhyThePanelStops() {
    assertEquals(SunState.GENERATING, new SkySun(true, true, false, true, true, false).state());
    assertEquals(SunState.NO_SUN, new SkySun(true, true, false, false, true, false).state());
    assertEquals(SunState.COVERED, new SkySun(false, false, true, true, true, false).state());
    assertEquals(SunState.NIGHT, new SkySun(false, true, true, true, true, false).state());
    assertEquals(SunState.RAIN, new SkySun(true, true, true, true, true, false).state());
  }

  /**
   * A Dock without the third block, from before it had one, has no room for the panel, and only a
   * world without a sun is a more lasting reason (answer 1897).
   */
  @Test
  void withoutTheThirdBlockThereIsNoRoomForThePanel() {
    assertEquals(SunState.NO_ROOM, new SkySun(true, true, false, true, false, false).state());
    assertEquals(SunState.NO_ROOM, new SkySun(false, false, true, true, false, false).state());
    assertEquals(SunState.NO_SUN, new SkySun(true, true, false, false, false, false).state());
    assertFalse(generates(new SkySun(true, true, false, true, false, false)));
  }

  /** At a tier that gives at night, a clear night keeps the panel at work (answer 1943). */
  @Test
  void theMoonGivesAtATierThatGeneratesAtNight() {
    SkySun moon = new SkySun(false, true, false, true, true, true);

    assertEquals(SunState.MOONLIGHT, moon.state());
    assertTrue(generates(moon));
    assertEquals(SunState.GENERATING, new SkySun(true, true, false, true, true, true).state());
  }

  /** The moon follows the rules of the day: the sky, the rain, the world and the room (1943). */
  @Test
  void theMoonFollowsTheRulesOfTheDay() {
    assertEquals(SunState.RAIN, new SkySun(false, true, true, true, true, true).state());
    assertEquals(SunState.COVERED, new SkySun(false, false, false, true, true, true).state());
    assertEquals(SunState.NO_SUN, new SkySun(false, true, false, false, true, true).state());
    assertEquals(SunState.NO_ROOM, new SkySun(false, true, false, true, false, true).state());
  }

  private static boolean generates(SkySun sun) {
    return sun.state().generates();
  }
}
