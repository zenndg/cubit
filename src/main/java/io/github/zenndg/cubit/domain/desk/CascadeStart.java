package io.github.zenndg.cubit.domain.desk;

/** Whether a plan may start, or the first reason why not. */
public enum CascadeStart {
  READY,
  TIER,
  NO_AMOUNT,
  NO_PLAN,
  MISSING,
  ORDER_ACTIVE,
  DISASSEMBLING,
  STOCK_LEFT;

  /** Every material is there, so the plan may be seen, also while it cannot start (2015, 2017). */
  public boolean showsPlan() {
    return this == READY || this == ORDER_ACTIVE || this == DISASSEMBLING || this == STOCK_LEFT;
  }

  public static CascadeStart decide(CascadeCheck check) {
    if (!check.tier()) {
      return TIER;
    }
    if (!check.amount()) {
      return NO_AMOUNT;
    }
    if (!check.plan()) {
      return NO_PLAN;
    }
    if (!check.materials()) {
      return MISSING;
    }
    if (check.orderActive()) {
      return ORDER_ACTIVE;
    }
    if (check.disassembling()) {
      return DISASSEMBLING;
    }
    return check.stockLeft() ? STOCK_LEFT : READY;
  }
}
