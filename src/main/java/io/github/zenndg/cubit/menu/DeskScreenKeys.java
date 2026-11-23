package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import java.util.Locale;

/** The translation keys of the Engineering Desk screen, shared by the screen and the datagen. */
public final class DeskScreenKeys {
  private static final String PREFIX = "gui.cubit.engineering_desk.";
  private static final String STATUS_PREFIX = PREFIX + "status.";
  public static final String INPUT = PREFIX + "input";
  public static final String OUTPUT = PREFIX + "output";
  public static final String STORED_ENERGY = PREFIX + "stored_energy";
  public static final String ORDER = PREFIX + "order";
  public static final String CANCEL = PREFIX + "cancel";
  public static final String LOCKED = PREFIX + "locked";
  public static final String UNLOCKS_WITH = PREFIX + "unlocks_with";
  public static final String MISSING = PREFIX + "missing";
  public static final String CASCADE = PREFIX + "cascade";
  public static final String CASCADE_LOCKED = PREFIX + "cascade_locked";
  public static final String CASCADE_LOCKS = PREFIX + "cascade_locks";
  public static final String CASCADE_PART_LOCKED = PREFIX + "cascade_part_locked";
  public static final String CASCADE_PICK = PREFIX + "cascade_pick";
  public static final String CASCADE_MATERIAL = PREFIX + "cascade_material";
  public static final String CASCADE_START = PREFIX + "cascade_start";
  public static final String CASCADE_NEW = PREFIX + "cascade_new";
  public static final String CASCADE_YES = PREFIX + "cascade_yes";
  public static final String CASCADE_NO = PREFIX + "cascade_no";
  public static final String CASCADE_ASK = PREFIX + "cascade_ask";
  public static final String CASCADE_ASKED = PREFIX + "cascade_asked";
  public static final String CASCADE_COST = PREFIX + "cascade_cost";
  public static final String CASCADE_NO_CANCEL = PREFIX + "cascade_no_cancel";
  public static final String CASCADE_STEP = PREFIX + "cascade_step";
  private static final String CASCADE_REASON = PREFIX + "cascade_reason.";
  public static final String CATALOG = PREFIX + "catalog";
  public static final String SEARCH = PREFIX + "search";
  public static final String STORAGE_ARROW = PREFIX + "storage_arrow";
  public static final String STORAGE_FEEDS = PREFIX + "storage_feeds";
  public static final String STORAGE_FINISHED = PREFIX + "storage_finished";
  private static final String CATEGORY_PREFIX = PREFIX + "category.";

  private DeskScreenKeys() {}

  public static String category(DeskCategory category) {
    return CATEGORY_PREFIX + category.id();
  }

  public static String status(DeskStatus status) {
    return STATUS_PREFIX + status.name().toLowerCase(Locale.ROOT);
  }

  /** Why a plan may or may not start. */
  public static String cascadeStart(CascadeStart start) {
    return CASCADE_REASON + start.name().toLowerCase(Locale.ROOT);
  }
}
