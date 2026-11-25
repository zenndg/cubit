package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.FoldingPanel;
import io.github.zenndg.cubit.domain.energy.PanelState;
import java.util.Locale;
import net.minecraft.nbt.CompoundTag;

/**
 * The folding panel of a block of the mod, in its save and on the clients. Only the end of a motion
 * is saved, so a load shows the panel already there (answer 1864).
 */
final class KeptPanel {
  private static final String PANEL_KEY = "SolarPanel";
  private static final String SHOWN_KEY = "PanelState";
  private static final String DEPLOYED = "deployed";
  private static final String STORED = "stored";

  private FoldingPanel panel = FoldingPanel.STORED;
  private PanelState sent = PanelState.STORED;

  PanelState state() {
    return panel.state();
  }

  boolean generates() {
    return panel.generates();
  }

  /** The panel follows the sky; a new end of the motion is worth saving (answers 1856 and 1862). */
  Step tick(boolean sun, FoldingPanel.Timings timings) {
    FoldingPanel before = panel;
    panel = panel.tick(sun, timings);
    return new Step(before, panel);
  }

  /** True once for each new place of the panel, so the client hears of it once. */
  boolean moved() {
    boolean changed = panel.state() != sent;
    sent = panel.state();
    return changed;
  }

  void save(CompoundTag tag) {
    tag.putString(PANEL_KEY, panel.deployedAtTheEnd() ? DEPLOYED : STORED);
  }

  void load(CompoundTag tag) {
    if (tag.contains(PANEL_KEY)) {
      panel = FoldingPanel.saved(DEPLOYED.equals(tag.getString(PANEL_KEY)));
    }
  }

  /** What the client needs: where the panel is, also mid motion. */
  void write(CompoundTag tag) {
    tag.putString(SHOWN_KEY, panel.state().name());
  }

  void read(CompoundTag tag) {
    if (tag.contains(SHOWN_KEY)) {
      panel = new FoldingPanel(shown(tag.getString(SHOWN_KEY)), 0);
    }
  }

  /** An unknown state, as from a newer server, shows the panel stored. */
  private static PanelState shown(String name) {
    for (PanelState value : PanelState.values()) {
      if (value.name().equals(name.toUpperCase(Locale.ROOT))) {
        return value;
      }
    }
    return PanelState.STORED;
  }

  /** What a tick did, for the sounds and the save. */
  record Step(FoldingPanel before, FoldingPanel after) {
    boolean worthSaving() {
      return before.deployedAtTheEnd() != after.deployedAtTheEnd();
    }
  }
}
