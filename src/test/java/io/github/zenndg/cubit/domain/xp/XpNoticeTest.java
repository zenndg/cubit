package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class XpNoticeTest {
  /** Once each time it fills, and again only after it lowers (answer 1840). */
  @Test
  void itTellsOnceEachTimeItFills() {
    XpNotice notice = XpNotice.START;
    assertTrue(notice.tells(true));
    notice = notice.update(true);
    assertFalse(notice.tells(true));
    notice = notice.update(false);
    assertFalse(notice.tells(false));
    assertTrue(notice.tells(true));
  }
}
