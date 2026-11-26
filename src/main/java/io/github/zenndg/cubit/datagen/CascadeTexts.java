package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.CascadeTaskStatus;
import io.github.zenndg.cubit.item.EngineeringDeskItem;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.function.BiConsumer;

/** The Cascade tab of the Engineering Desk, in English, the base language (answer 2018). */
final class CascadeTexts {
  private static final String NAME = "Cascade";
  private static final String LOCKED = "Unlocks at Ultimate";
  private static final String LOCKS = "A plan is running";
  private static final String PICK = "Pick an item in the catalog";
  private static final String MATERIALS = "Materials for %s× %s";
  private static final String HAS = "In the input: %s / %s";
  private static final String READY_PART = "Already made: uses %s";
  private static final String VIEW = "View plan";
  private static final String START = "Start";
  private static final String NEW = "New plan";
  private static final String YES = "Yes";
  private static final String NO = "No";
  private static final String ASK = "Start this plan?";
  private static final String ASKED = "%s× %s · %s s";
  private static final String AMOUNT = "%s× %s";
  private static final String COST = "%s FE · %s s";
  private static final String NO_CANCEL = "It cannot be canceled.";
  private static final String ORDER_COLUMN = "#";
  private static final String ITEM_COLUMN = "Item";
  private static final String RESERVED_COLUMN = "Reserved";
  private static final String NEEDS_COLUMN = "Needs";
  private static final String STATUS_COLUMN = "Status";
  private static final String WAITING = "Waiting";
  private static final String CRAFTING = "Crafting %s/%s";
  private static final String DONE = "Done";
  private static final String TOOLTIP = "Cascade: %s · %s/%s";
  private static final String REASON_READY = "Ready";
  private static final String REASON_TIER = "Unlocks at Ultimate";
  private static final String REASON_NO_AMOUNT = "Press + to set the amount";
  private static final String REASON_NO_PLAN = "No plan for this item";
  private static final String REASON_MISSING = "Missing materials in the input";
  private static final String REASON_ORDER_ACTIVE = "An order is active";
  private static final String REASON_DISASSEMBLING = "A disassembly is running";
  private static final String REASON_STOCK_LEFT = "The last plan still has items";

  private CascadeTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.CASCADE, NAME);
    add.accept(DeskScreenKeys.CASCADE_LOCKED, LOCKED);
    add.accept(DeskScreenKeys.CASCADE_LOCKS, LOCKS);
    add.accept(DeskScreenKeys.CASCADE_PICK, PICK);
    add.accept(DeskScreenKeys.CASCADE_MATERIALS, MATERIALS);
    add.accept(DeskScreenKeys.CASCADE_HAS, HAS);
    add.accept(DeskScreenKeys.CASCADE_READY_PART, READY_PART);
    add.accept(DeskScreenKeys.CASCADE_VIEW, VIEW);
    add.accept(DeskScreenKeys.CASCADE_START, START);
    add.accept(DeskScreenKeys.CASCADE_NEW, NEW);
    add.accept(DeskScreenKeys.CASCADE_YES, YES);
    add.accept(DeskScreenKeys.CASCADE_NO, NO);
    add.accept(DeskScreenKeys.CASCADE_ASK, ASK);
    add.accept(DeskScreenKeys.CASCADE_ASKED, ASKED);
    add.accept(DeskScreenKeys.CASCADE_AMOUNT, AMOUNT);
    add.accept(DeskScreenKeys.CASCADE_COST, COST);
    add.accept(DeskScreenKeys.CASCADE_NO_CANCEL, NO_CANCEL);
    add.accept(EngineeringDeskItem.CASCADE_KEY, TOOLTIP);
    addTable(add);
    addReasons(add);
  }

  private static void addTable(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.CASCADE_ORDER_COLUMN, ORDER_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_ITEM_COLUMN, ITEM_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_RESERVED_COLUMN, RESERVED_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_NEEDS_COLUMN, NEEDS_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_STATUS_COLUMN, STATUS_COLUMN);
    add.accept(DeskScreenKeys.cascadeStatus(CascadeTaskStatus.WAITING), WAITING);
    add.accept(DeskScreenKeys.cascadeStatus(CascadeTaskStatus.CRAFTING), CRAFTING);
    add.accept(DeskScreenKeys.cascadeStatus(CascadeTaskStatus.DONE), DONE);
  }

  private static void addReasons(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.READY), REASON_READY);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.TIER), REASON_TIER);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.NO_AMOUNT), REASON_NO_AMOUNT);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.NO_PLAN), REASON_NO_PLAN);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.MISSING), REASON_MISSING);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.ORDER_ACTIVE), REASON_ORDER_ACTIVE);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.DISASSEMBLING), REASON_DISASSEMBLING);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.STOCK_LEFT), REASON_STOCK_LEFT);
  }
}
