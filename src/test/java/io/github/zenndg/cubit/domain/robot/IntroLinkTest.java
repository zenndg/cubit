package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IntroLinkTest {
  private static final long SHOWN_AT = 1_000;
  private static final long COOLDOWN = 12_000;
  private static final long BEFORE_THE_END = SHOWN_AT + COOLDOWN - 1;
  private static final long AT_THE_END = SHOWN_AT + COOLDOWN;
  private static final long EARLIER = 10;
  private static final long BELOW_NEVER = -5;
  private static final long NEGATIVE = -1;
  private static final String GITHUB = "https://github.com/zenndg/cubit";
  private static final String PLAIN_HTTP = "http://github.com/zenndg/cubit";
  private static final String ONLY_THE_SCHEME = "https://";
  private static final String WITH_A_SPACE = "https://github.com/zenndg/ cubit";
  private static final String SCRIPT = "javascript:alert(1)";
  private static final String EMPTY = "";

  @Test
  void theFirstLinkOfAPlayerIsDue() {
    assertTrue(IntroLink.NEVER.isDue(SHOWN_AT, COOLDOWN));
  }

  @Test
  void aLinkWaitsForTheCooldown() {
    IntroLink shown = IntroLink.NEVER.shownAt(SHOWN_AT);
    assertFalse(shown.isDue(SHOWN_AT, COOLDOWN));
    assertFalse(shown.isDue(BEFORE_THE_END, COOLDOWN));
    assertTrue(shown.isDue(AT_THE_END, COOLDOWN));
  }

  @Test
  void noCooldownShowsEveryLink() {
    assertTrue(IntroLink.NEVER.shownAt(SHOWN_AT).isDue(SHOWN_AT, 0));
  }

  /** A time before the last link, as in another world, cannot keep the link waiting forever. */
  @Test
  void aTimeBeforeTheLastLinkIsDue() {
    assertTrue(IntroLink.NEVER.shownAt(SHOWN_AT).isDue(EARLIER, COOLDOWN));
  }

  @Test
  void theShownTimeIsKept() {
    assertEquals(SHOWN_AT, IntroLink.NEVER.shownAt(SHOWN_AT).lastShown());
  }

  @Test
  void rejectsInvalidTimes() {
    assertThrows(IllegalArgumentException.class, () -> new IntroLink(BELOW_NEVER));
    assertThrows(IllegalArgumentException.class, () -> IntroLink.NEVER.shownAt(NEGATIVE));
    assertThrows(IllegalArgumentException.class, () -> IntroLink.NEVER.isDue(SHOWN_AT, NEGATIVE));
  }

  /** Only an address that opens a secure page is accepted (answer 1674). */
  @Test
  void onlyAnHttpsAddressIsSafe() {
    assertTrue(IntroLink.isSafeAddress(GITHUB));
    assertFalse(IntroLink.isSafeAddress(PLAIN_HTTP));
    assertFalse(IntroLink.isSafeAddress(ONLY_THE_SCHEME));
    assertFalse(IntroLink.isSafeAddress(WITH_A_SPACE));
    assertFalse(IntroLink.isSafeAddress(SCRIPT));
    assertFalse(IntroLink.isSafeAddress(EMPTY));
  }
}
