package io.github.zenndg.cubit.domain.desk;

/**
 * What the desk shows to everyone around it: parked, crafting or paused. The causes of a pause stay
 * on the server, and the client only needs to know that the desk is waiting (answer 1508).
 */
public enum DeskLook {
  IDLE,
  CRAFTING,
  PAUSED;

  public static DeskLook of(DeskStatus status) {
    if (status.isWorking()) {
      return CRAFTING;
    }
    return status.isPaused() ? PAUSED : IDLE;
  }

  /** Only a desk that crafts lights the room. The half light of a pause is only seen (1513). */
  public boolean lightsRoom() {
    return this == CRAFTING;
  }

  /** Crafting and paused both have an order that is not done. */
  public boolean hasOrder() {
    return this != IDLE;
  }
}
