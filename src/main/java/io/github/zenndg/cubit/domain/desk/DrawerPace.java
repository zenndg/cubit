package io.github.zenndg.cubit.domain.desk;

/**
 * When a drawer of the desk may open again. Automation moves items every tick, and without a pause
 * the drawer would flicker (answer 1511). The caller gives the game time.
 */
public record DrawerPace(long lastOpened) {
  /** A drawer that never opened, so it opens at once. */
  public static final DrawerPace NEVER = new DrawerPace(Long.MIN_VALUE);

  /** About the length of the drawer animation, so one opening ends before the next. */
  public static final long INTERVAL_TICKS = 20;

  /** A clock that went back, after a time command, does not lock the drawer. */
  public boolean allows(long now) {
    return lastOpened == Long.MIN_VALUE || now < lastOpened || now - lastOpened >= INTERVAL_TICKS;
  }

  public DrawerPace open(long now) {
    return allows(now) ? new DrawerPace(now) : this;
  }
}
