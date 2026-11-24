package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.xp.XpMode;

/** The translation keys of the XP tab, a contract with whoever translates the mod (1830, 2070). */
public final class XpKeys {
  private static final String PREFIX = "gui.cubit.xp.";
  private static final String DETAIL = ".detail";

  public static final String TAB = PREFIX + "tab";
  public static final String TANK = PREFIX + "tank";
  public static final String AMOUNT = PREFIX + "amount";
  public static final String AMOUNT_ONE = PREFIX + "amount_one";
  public static final String MARK = PREFIX + "mark";
  public static final String YOUR_LEVEL = PREFIX + "your_level";
  public static final String MODES = PREFIX + "modes";
  public static final String DELETE_EXCESS = PREFIX + "delete_excess";
  public static final String DELETE_EXCESS_DETAIL = DELETE_EXCESS + DETAIL;
  public static final String ONLY_STORE = PREFIX + "only_store";
  public static final String TAKE_DETAIL = PREFIX + "take_detail";
  public static final String SEND_DETAIL = PREFIX + "send_detail";
  public static final String NOT_ACTIVE = PREFIX + "not_active";
  public static final String TANK_EMPTY = PREFIX + "tank_empty";
  public static final String TANK_FULL = PREFIX + "tank_full";
  public static final String NO_XP = PREFIX + "no_xp";
  public static final String TOOLTIP = "tooltip.cubit.xp";

  private XpKeys() {}

  public static String mode(XpMode mode) {
    return PREFIX + mode.key();
  }

  public static String modeDetail(XpMode mode) {
    return mode(mode) + DETAIL;
  }
}
