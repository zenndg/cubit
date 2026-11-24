package io.github.zenndg.cubit.domain.dock;

import io.github.zenndg.cubit.domain.xp.XpTank;

/**
 * The notice of a Dock with a full tank: once when it overflows, and again only after it is below
 * nine tenths, so a tank that fills and lowers a point does not repeat it (answers 1840 and 2105).
 */
public record DockXpNotice(boolean told) {
  public static final DockXpNotice START = new DockXpNotice(false);

  private static final long REARM_PERCENT = 90;
  private static final long PERCENT = 100;

  public boolean tells(boolean overflowed) {
    return overflowed && !told;
  }

  /** Only once the owner read it, so one who comes near later still reads it. */
  public DockXpNotice afterTelling() {
    return new DockXpNotice(true);
  }

  public DockXpNotice update(XpTank tank) {
    return tank.fluid() * PERCENT < tank.capacity() * REARM_PERCENT ? START : this;
  }
}
