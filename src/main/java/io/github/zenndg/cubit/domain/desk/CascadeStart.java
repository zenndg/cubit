package io.github.zenndg.cubit.domain.desk;

/** Whether a plan may start, or the first reason why not. */
public enum CascadeStart {
  READY,
  TIER,
  NO_PLAN,
  LOCKED,
  MISSING,
  ORDER_ACTIVE,
  STOCK_LEFT;

  public static CascadeStart decide(CascadeCheck check) {
    if (!check.tier()) {
      return TIER;
    }
    if (!check.plan()) {
      return NO_PLAN;
    }
    if (!check.released()) {
      return LOCKED;
    }
    if (!check.materials()) {
      return MISSING;
    }
    if (check.orderActive()) {
      return ORDER_ACTIVE;
    }
    return check.stockLeft() ? STOCK_LEFT : READY;
  }
}
