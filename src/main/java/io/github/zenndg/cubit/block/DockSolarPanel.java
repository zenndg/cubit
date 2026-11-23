package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockPanel;
import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.dock.DockStock;
import io.github.zenndg.cubit.domain.dock.DockSun;
import io.github.zenndg.cubit.domain.dock.DockSunState;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarPanel;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

/**
 * The solar panel of every Dock (answers 1852 to 1866). After a while with sun in a row the arms
 * take it out of the cabinet onto the tower, after the same while without sun they put it back, and
 * only a panel fully on the tower puts its rate into the stock, without passing it and apart from
 * the input of the cables. What it gave last shows on the screen and in Jade.
 */
final class DockSolarPanel {
  /** The panel lies on the top of the tower, so the sky is read in the block above the Dock. */
  private static final int ABOVE_THE_PANEL = 2;

  /** The six steps of the arms, about 4 s (answer 1859). */
  private static final int MOTION_TICKS = 80;

  private static final String SUN_KEY = "Sun";
  private static final String PANEL_KEY = "SolarPanel";
  private static final String SHOWN_KEY = "PanelState";
  private static final String DEPLOYED = "deployed";
  private static final String STORED = "stored";

  private Energy given = Energy.ZERO;
  private DockSunState state = DockSunState.NO_SUN;
  private DockPanel panel = DockPanel.STORED;
  private DockPanelState sent = DockPanelState.STORED;
  private boolean read;
  private boolean skyOpen;
  private boolean sunExists;

  /** On the client, whether the panel is generating, for the pulse of the cells (1392, 1853). */
  private boolean sunlit;

  Energy given() {
    return given;
  }

  /** What the sky does for the panel and why it stops, for the Solar tab (answer 1396). */
  DockSunState state() {
    return state;
  }

  /** Where the panel is, on the server and on the client (answer 1865). */
  DockPanelState panelState() {
    return panel.state();
  }

  boolean sunlit() {
    return sunlit;
  }

  /** True when the pulse or the place of the panel changed, so the client hears of it once. */
  boolean switched() {
    boolean now = !given.isZero();
    boolean changed = now != sunlit || panel.state() != sent;
    sunlit = now;
    sent = panel.state();
    return changed;
  }

  /** What the client needs: the pulse and where the panel is, also mid motion. */
  void writeSun(CompoundTag tag) {
    tag.putBoolean(SUN_KEY, sunlit);
    tag.putString(SHOWN_KEY, panel.state().name());
  }

  void readSun(CompoundTag tag) {
    sunlit = tag.getBoolean(SUN_KEY);
    if (tag.contains(SHOWN_KEY)) {
      panel = new DockPanel(shown(tag.getString(SHOWN_KEY)), 0);
    }
  }

  /** Only the end of a motion is saved, so a load shows the panel already there (answer 1864). */
  void save(CompoundTag tag) {
    tag.putString(PANEL_KEY, panel.deployedAtTheEnd() ? DEPLOYED : STORED);
  }

  void load(CompoundTag tag) {
    if (tag.contains(PANEL_KEY)) {
      panel = DockPanel.saved(DEPLOYED.equals(tag.getString(PANEL_KEY)));
    }
  }

  DockStock generate(Level level, BlockPos lower, DockStock stock, Energy rate, Runnable changed) {
    given = Energy.ZERO;
    if (!read || level.getGameTime() % CubitMod.CONFIG.solarCheckTicks() == 0) {
      readSky(level, lower);
    }
    boolean day = SolarPanel.isDay(level.getDayTime());
    state = new DockSun(day, skyOpen, level.isRaining() || level.isThundering(), sunExists).state();
    move(changed);
    if (rate.isZero() || state != DockSunState.GENERATING || !panel.generates()) {
      return stock;
    }
    DockStock.Received received = stock.receive(rate, rate);
    given = received.accepted();
    if (!given.isZero()) {
      changed.run();
    }
    return received.stock();
  }

  /** The arms follow the sky; a new end of the motion is saved (answers 1856 and 1862). */
  private void move(Runnable changed) {
    boolean before = panel.deployedAtTheEnd();
    DockPanel.Timings timings =
        new DockPanel.Timings(CubitMod.CONFIG.dock().solarDelayTicks(), MOTION_TICKS);
    panel = panel.tick(state == DockSunState.GENERATING, timings);
    if (before != panel.deployedAtTheEnd()) {
      changed.run();
    }
  }

  /** An unknown state, as from a newer server, shows the panel in the cabinet. */
  private static DockPanelState shown(String name) {
    for (DockPanelState value : DockPanelState.values()) {
      if (value.name().equals(name.toUpperCase(Locale.ROOT))) {
        return value;
      }
    }
    return DockPanelState.STORED;
  }

  /**
   * As the panel of the Cubit: light that passes through glass still counts, an opaque block over
   * the Dock stops it, and the Nether and the End have no sun. Only this waits for the interval of
   * the Cubit (rule 21).
   */
  private void readSky(Level level, BlockPos lower) {
    read = true;
    skyOpen = level.canSeeSky(lower.above(ABOVE_THE_PANEL));
    sunExists = level.dimensionType().hasSkyLight() && !level.dimensionType().hasCeiling();
  }
}
