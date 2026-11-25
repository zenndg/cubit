package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.energy.FoldingPanel;
import io.github.zenndg.cubit.domain.energy.PanelSound;
import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.energy.SolarPanel;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.registry.CubitSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/**
 * The solar panel of every Dock (answers 1852 to 1866). After a while with sun in a row the arms
 * take it out of the cabinet onto the tower, after the same while without sun they put it back, and
 * only a panel fully on the tower puts its rate into the stock, without passing it and apart from
 * the input of the cables. At a tier that gives at night, the moon counts as the sun (answer 1943).
 * What it gave last shows on the screen and in Jade.
 */
final class DockSolarPanel {
  /** The panel lies in the third block of the Dock, so the sky is read just above it (1901). */
  private static final int ABOVE_THE_PANEL = 3;

  /** The transformation of the arms, about 6 s, as long as its animation (answer 1895). */
  private static final int MOTION_TICKS = 120;

  /** The tick of the deploy at which the two halves meet and lock, at 5.3 s (answer 1921). */
  private static final int LOCK_TICK = 106;

  private static final float VOLUME = 1f;
  private static final float PITCH = 1f;

  private static final String SUN_KEY = "Sun";

  private final SkyWatch sky = new SkyWatch(ABOVE_THE_PANEL);
  private final KeptPanel panel = new KeptPanel();
  private Energy given = Energy.ZERO;
  private SunState state = SunState.NO_SUN;
  private boolean day = true;
  private boolean room;
  private Boolean plateShown;

  /** On the client, whether the panel is generating, for the pulse of the cells (1392, 1853). */
  private boolean sunlit;

  /** For the Solar tab, Jade and the client (answers 1396, 1865 and 1949). */
  SolarReading reading(SolarOutput rates) {
    return new SolarReading(
        given, rates.most(day), rates.givesAtNight(), state, sunlit, panel.state());
  }

  /** True when the pulse or the place of the panel changed, so the client hears of it once. */
  boolean switched() {
    boolean now = !given.isZero();
    boolean changed = now != sunlit;
    sunlit = now;
    return panel.moved() || changed;
  }

  /** What the client needs: the pulse and where the panel is, also mid motion. */
  void writeSun(CompoundTag tag) {
    tag.putBoolean(SUN_KEY, sunlit);
    panel.write(tag);
  }

  void readSun(CompoundTag tag) {
    sunlit = tag.getBoolean(SUN_KEY);
    panel.read(tag);
  }

  void save(CompoundTag tag) {
    panel.save(tag);
  }

  void load(CompoundTag tag) {
    panel.load(tag);
  }

  EnergyStock generate(
      Level level, BlockPos lower, EnergyStock stock, SolarOutput rates, Runnable changed) {
    given = Energy.ZERO;
    if (sky.refresh(level, lower)) {
      // A Dock from before the third block takes it here, if the space is empty.
      room = DockTop.claim(level, lower);
    }
    day = SolarPanel.isDay(level.getDayTime());
    state = sky.state(level, room, rates.givesAtNight());
    move(level, lower, changed);
    showPlate(level, lower);
    Energy rate = rates.at(day);
    if (rate.isZero() || !state.generates() || !panel.generates()) {
      return stock;
    }
    EnergyStock.Received received = stock.receive(rate, rate);
    given = received.accepted();
    if (!given.isZero()) {
      changed.run();
    }
    return received.stock();
  }

  /** The arms follow the sky; a new end of the motion is saved (answers 1856 and 1862). */
  private void move(Level level, BlockPos lower, Runnable changed) {
    FoldingPanel.Timings timings =
        new FoldingPanel.Timings(CubitMod.CONFIG.dock().solarDelayTicks(), MOTION_TICKS);
    KeptPanel.Step step = panel.tick(state.generates(), timings);
    if (step.worthSaving()) {
      changed.run();
    }
    PanelSound.between(step.before(), step.after(), LOCK_TICK)
        .ifPresent(sound ->
            level.playSound(null, lower, eventOf(sound), SoundSource.BLOCKS, VOLUME, PITCH));
  }

  /** The server plays them, so everyone near hears them, as the Patroller (answer 1921). */
  private static SoundEvent eventOf(PanelSound sound) {
    return switch (sound) {
      case DEPLOY -> CubitSounds.DOCK_PANEL_DEPLOY.get();
      case LOCK -> CubitSounds.DOCK_PANEL_LOCK.get();
      case RETRACT -> CubitSounds.DOCK_PANEL_RETRACT.get();
    };
  }

  /** The third block stands as the plate of the panel only with it deployed (answer 1898). */
  private void showPlate(Level level, BlockPos lower) {
    boolean deployed = panel.state() == PanelState.DEPLOYED;
    if (plateShown == null || plateShown != deployed) {
      DockTop.show(level, lower, deployed);
      plateShown = deployed;
    }
  }
}
