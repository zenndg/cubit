package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.CascadeTaskStatus;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
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
  public static final String ORDER_HOLDS = PREFIX + "order_holds";
  public static final String BACK = PREFIX + "back";
  public static final String MISSING = PREFIX + "missing";
  public static final String CASCADE = PREFIX + "cascade";
  public static final String CASCADE_LOCKED = PREFIX + "cascade_locked";
  public static final String CASCADE_LOCKS = PREFIX + "cascade_locks";
  public static final String CASCADE_PICK = PREFIX + "cascade_pick";
  public static final String CASCADE_MATERIALS = PREFIX + "cascade_materials";
  public static final String CASCADE_HAS = PREFIX + "cascade_has";
  public static final String CASCADE_READY_PART = PREFIX + "cascade_ready_part";
  public static final String CASCADE_VIEW = PREFIX + "cascade_view";
  public static final String CASCADE_START = PREFIX + "cascade_start";
  public static final String CASCADE_NEW = PREFIX + "cascade_new";
  public static final String CASCADE_YES = PREFIX + "cascade_yes";
  public static final String CASCADE_NO = PREFIX + "cascade_no";
  public static final String CASCADE_ASK = PREFIX + "cascade_ask";
  public static final String CASCADE_ASKED = PREFIX + "cascade_asked";
  public static final String CASCADE_AMOUNT = PREFIX + "cascade_amount";
  public static final String CASCADE_COST = PREFIX + "cascade_cost";
  public static final String CASCADE_NO_CANCEL = PREFIX + "cascade_no_cancel";
  public static final String CASCADE_ORDER_COLUMN = PREFIX + "cascade_column.order";
  public static final String CASCADE_ITEM_COLUMN = PREFIX + "cascade_column.item";
  public static final String CASCADE_RESERVED_COLUMN = PREFIX + "cascade_column.reserved";
  public static final String CASCADE_NEEDS_COLUMN = PREFIX + "cascade_column.needs";
  public static final String CASCADE_STATUS_COLUMN = PREFIX + "cascade_column.status";
  private static final String CASCADE_STATUS = PREFIX + "cascade_status.";
  private static final String CASCADE_REASON = PREFIX + "cascade_reason.";
  public static final String DISASSEMBLE = PREFIX + "disassemble";
  public static final String DISASSEMBLE_LOCKED = PREFIX + "disassemble_locked";
  public static final String DISASSEMBLE_ITEM = PREFIX + "disassemble_item";
  public static final String DISASSEMBLE_RETURNS = PREFIX + "disassemble_returns";
  public static final String DISASSEMBLE_EACH = PREFIX + "disassemble_each";
  public static final String DISASSEMBLE_LOST = PREFIX + "disassemble_lost";
  public static final String DISASSEMBLE_EMPTY = PREFIX + "disassemble_empty";
  public static final String DISASSEMBLE_LEFT = PREFIX + "disassemble_left";
  public static final String DISASSEMBLE_START = PREFIX + "disassemble_start";
  public static final String DISASSEMBLE_RUNS = PREFIX + "disassemble_runs";
  private static final String DISASSEMBLE_REASON = PREFIX + "disassemble_reason.";
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

  /** What a task of the plan is doing (answer 2012). */
  public static String cascadeStatus(CascadeTaskStatus status) {
    return CASCADE_STATUS + status.name().toLowerCase(Locale.ROOT);
  }

  /** Why the desk does not take an item apart (answer 2524). */
  public static String disassemblyRefusal(DisassemblyRefusal refusal) {
    return DISASSEMBLE_REASON + refusal.name().toLowerCase(Locale.ROOT);
  }

  /** Why a plan may or may not start. */
  public static String cascadeStart(CascadeStart start) {
    return CASCADE_REASON + start.name().toLowerCase(Locale.ROOT);
  }
}
