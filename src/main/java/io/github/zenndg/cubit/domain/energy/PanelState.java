package io.github.zenndg.cubit.domain.energy;

/**
 * Where a solar panel is: folded, on its way out, open or on its way back, for the screens and the
 * light of the doors of the Dock (answers 1859, 1863 and 1865).
 */
public enum PanelState {
  STORED,
  DEPLOYING,
  DEPLOYED,
  RETRACTING
}
