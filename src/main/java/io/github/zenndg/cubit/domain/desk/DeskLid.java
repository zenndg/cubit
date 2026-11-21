package io.github.zenndg.cubit.domain.desk;

/**
 * The lid of the laptop: open while someone uses the screen or while an order is active, crafting
 * or paused, and closed when the desk is parked with nobody at it (answer 1512).
 */
public final class DeskLid {
  private DeskLid() {}

  public static boolean isOpen(int viewers, DeskLook look) {
    return viewers > 0 || look.hasOrder();
  }
}
