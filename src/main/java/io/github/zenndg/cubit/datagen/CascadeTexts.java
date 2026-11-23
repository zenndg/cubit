package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.function.BiConsumer;

/** The Cascade tab of the Engineering Desk, in English, the base language (answer 2018). */
final class CascadeTexts {
  private static final String NAME = "Cascade";
  private static final String LOCKED = "Unlocks at Ultimate";
  private static final String LOCKS = "A plan is running";
  private static final String PART_LOCKED = "Locked: %s";
  private static final String PICK = "Pick an item in the catalog";
  private static final String MATERIAL = "%s / %s %s";
  private static final String START = "Start";
  private static final String NEW = "New plan";
  private static final String YES = "Yes";
  private static final String NO = "No";
  private static final String ASK = "Start this plan?";
  private static final String ASKED = "%s× %s";
  private static final String COST = "%s FE · %s s";
  private static final String NO_CANCEL = "It cannot be canceled.";
  private static final String STEP = "%s. %s× %s  %s/%s";
  private static final String REASON_READY = "Ready";
  private static final String REASON_TIER = "Unlocks at Ultimate";
  private static final String REASON_NO_PLAN = "No plan for this item";
  private static final String REASON_LOCKED = "A part of it is locked";
  private static final String REASON_MISSING = "Missing materials in the input";
  private static final String REASON_ORDER_ACTIVE = "An order is active";
  private static final String REASON_STOCK_LEFT = "The last plan still has items";

  private CascadeTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.CASCADE, NAME);
    add.accept(DeskScreenKeys.CASCADE_LOCKED, LOCKED);
    add.accept(DeskScreenKeys.CASCADE_LOCKS, LOCKS);
    add.accept(DeskScreenKeys.CASCADE_PART_LOCKED, PART_LOCKED);
    add.accept(DeskScreenKeys.CASCADE_PICK, PICK);
    add.accept(DeskScreenKeys.CASCADE_MATERIAL, MATERIAL);
    add.accept(DeskScreenKeys.CASCADE_START, START);
    add.accept(DeskScreenKeys.CASCADE_NEW, NEW);
    add.accept(DeskScreenKeys.CASCADE_YES, YES);
    add.accept(DeskScreenKeys.CASCADE_NO, NO);
    add.accept(DeskScreenKeys.CASCADE_ASK, ASK);
    add.accept(DeskScreenKeys.CASCADE_ASKED, ASKED);
    add.accept(DeskScreenKeys.CASCADE_COST, COST);
    add.accept(DeskScreenKeys.CASCADE_NO_CANCEL, NO_CANCEL);
    add.accept(DeskScreenKeys.CASCADE_STEP, STEP);
    addReasons(add);
  }

  private static void addReasons(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.READY), REASON_READY);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.TIER), REASON_TIER);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.NO_PLAN), REASON_NO_PLAN);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.LOCKED), REASON_LOCKED);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.MISSING), REASON_MISSING);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.ORDER_ACTIVE), REASON_ORDER_ACTIVE);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.STOCK_LEFT), REASON_STOCK_LEFT);
  }
}
