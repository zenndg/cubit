package io.github.zenndg.cubit.domain.dock;

/** The XP tank of a Dock at a tier, and the fluid a docked Cubit passes to it each tick (1835). */
public record DockXpSpec(int tankCapacity, int transferPerTick) {
  private static final String INVALID = "Invalid XP numbers of a dock: ";
  private static final String PER_TICK = ", per tick ";

  public DockXpSpec {
    if (tankCapacity < 1 || transferPerTick < 1) {
      throw new IllegalArgumentException(INVALID + tankCapacity + PER_TICK + transferPerTick);
    }
  }
}
