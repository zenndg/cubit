package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.dock.DockSunState;
import io.github.zenndg.cubit.menu.ScreenKeys;
import java.util.function.BiConsumer;

/** The Solar Dock and its Solar tab, in Portuguese (answers 1386 and 1396). */
final class PortugueseSolarDockTexts {
  private static final String SOLAR = "Solar +%s";
  private static final String NO_SUN = "Solar: sem sol";
  private static final String TAB = "Solar";
  private static final String STATUS = "Status";
  private static final String REASON = "Motivo";
  private static final String NOW = "Agora";
  private static final String MAX = "Máximo do tier";
  private static final String HINT = "Ao ar livre, de dia e sem chuva";
  private static final String GENERATING = "Gerando";
  private static final String STOPPED = "Parado";
  private static final String NIGHT = "Noite";
  private static final String RAIN = "Chuva";
  private static final String COVERED = "Céu coberto";
  private static final String NO_SUN_HERE = "Sem sol nesta dimensão";
  private static final String PANEL = "Painel";
  private static final String STORED = "Guardado";
  private static final String DEPLOYING = "Montando";
  private static final String DEPLOYED = "Montado";
  private static final String RETRACTING = "Guardando";

  private PortugueseSolarDockTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(ScreenKeys.DOCK_SOLAR, SOLAR);
    add.accept(ScreenKeys.DOCK_NO_SUN, NO_SUN);
    add.accept(ScreenKeys.DOCK_TAB_SOLAR, TAB);
    add.accept(ScreenKeys.DOCK_SOLAR_STATUS, STATUS);
    add.accept(ScreenKeys.DOCK_SOLAR_REASON, REASON);
    add.accept(ScreenKeys.DOCK_SOLAR_NOW, NOW);
    add.accept(ScreenKeys.DOCK_SOLAR_MAX, MAX);
    add.accept(ScreenKeys.DOCK_SOLAR_HINT, HINT);
    addStates(add);
    addPanel(add);
  }

  private static void addStates(BiConsumer<String, String> add) {
    add.accept(ScreenKeys.dockSun(DockSunState.GENERATING), GENERATING);
    for (DockSunState state : DockSunState.values()) {
      if (state != DockSunState.GENERATING) {
        add.accept(ScreenKeys.dockSun(state), STOPPED);
      }
    }
    add.accept(ScreenKeys.dockSunReason(DockSunState.GENERATING), GENERATING);
    add.accept(ScreenKeys.dockSunReason(DockSunState.NIGHT), NIGHT);
    add.accept(ScreenKeys.dockSunReason(DockSunState.RAIN), RAIN);
    add.accept(ScreenKeys.dockSunReason(DockSunState.COVERED), COVERED);
    add.accept(ScreenKeys.dockSunReason(DockSunState.NO_SUN), NO_SUN_HERE);
  }

  /** Where the panel of the Dock is, on the Solar tab and in Jade (answer 1865). */
  private static void addPanel(BiConsumer<String, String> add) {
    add.accept(ScreenKeys.DOCK_SOLAR_PANEL, PANEL);
    add.accept(ScreenKeys.dockPanel(DockPanelState.STORED), STORED);
    add.accept(ScreenKeys.dockPanel(DockPanelState.DEPLOYING), DEPLOYING);
    add.accept(ScreenKeys.dockPanel(DockPanelState.DEPLOYED), DEPLOYED);
    add.accept(ScreenKeys.dockPanel(DockPanelState.RETRACTING), RETRACTING);
  }
}
