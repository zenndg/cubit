package io.github.zenndg.cubit.domain.dock;

/**
 * Where the solar panel of the Dock is: in the cabinet, on its way up, on the tower or on its way
 * back, for the Solar tab and the light of the doors (answers 1859, 1863 and 1865).
 */
public enum DockPanelState {
  STORED,
  DEPLOYING,
  DEPLOYED,
  RETRACTING
}
