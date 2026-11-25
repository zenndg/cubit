package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.FoldingPanel;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.energy.SolarPanel;
import io.github.zenndg.cubit.domain.energy.SunState;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

/**
 * The panel of the Solar Relay: it opens with the sun and folds without it, and without a server it
 * stays folded (answers 2338 and 2374). Only a panel fully open generates.
 */
final class RelayPanel {
  /** The sky is read above the second block (Delivery 3, detail 9). */
  private static final int ABOVE_THE_PANEL = 2;

  /** The motion takes 4 s, as long as its animation (answer 2391). */
  private static final int MOTION_TICKS = 80;

  private static final SolarOutput NOTHING = new SolarOutput(Energy.ZERO, Energy.ZERO);

  private final SkyWatch sky = new SkyWatch(ABOVE_THE_PANEL);
  private final KeptPanel panel = new KeptPanel();
  private SunState state = SunState.NO_SUN;
  private Energy given = Energy.ZERO;
  private boolean shining;

  /** What the panel has to give in this tick. */
  Energy tick(Level level, BlockPos lower, Optional<SolarOutput> rates, Runnable changed) {
    sky.refresh(level, lower);
    SolarOutput output = rates.orElse(NOTHING);
    state = sky.state(level, true, output.givesAtNight());
    FoldingPanel.Timings timings =
        new FoldingPanel.Timings(CubitMod.CONFIG.dock().solarDelayTicks(), MOTION_TICKS);
    KeptPanel.Step step = panel.tick(rates.isPresent() && state.generates(), timings);
    if (step.worthSaving()) {
      changed.run();
    }
    boolean generates = state.generates() && panel.generates();
    return generates ? output.at(SolarPanel.isDay(level.getDayTime())) : Energy.ZERO;
  }

  /** What the server took of it. */
  void gave(Energy accepted) {
    given = accepted;
  }

  /** True when what the clients show changed. */
  boolean switched() {
    boolean now = !given.isZero();
    boolean changed = now != shining;
    shining = now;
    return panel.moved() || changed;
  }

  SolarReading reading(Optional<SolarOutput> rates, boolean day) {
    SolarOutput output = rates.orElse(NOTHING);
    return new SolarReading(
        given, output.most(day), output.givesAtNight(), state, shining, panel.state());
  }

  void save(CompoundTag tag) {
    panel.save(tag);
  }

  void load(CompoundTag tag) {
    panel.load(tag);
  }

  void write(CompoundTag tag) {
    panel.write(tag);
  }

  void read(CompoundTag tag) {
    panel.read(tag);
  }
}
