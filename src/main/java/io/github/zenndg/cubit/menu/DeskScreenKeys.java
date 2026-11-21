package io.github.zenndg.cubit.menu;

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
}
