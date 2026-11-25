package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.entity.RobotDockReach;
import io.github.zenndg.cubit.menu.ScreenKeys;
import io.github.zenndg.cubit.registry.CubitSounds;
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
  private static final String NIGHT_HINT = "Ao ar livre, sem chuva, também à noite";
  private static final String MOONLIGHT = "Luz da lua";
  private static final String MOONLIGHT_RATE = "Luz da lua +%s";
  private static final String GENERATING = "Gerando";
  private static final String STOPPED = "Parado";
  private static final String NIGHT = "Noite";
  private static final String RAIN = "Chuva";
  private static final String COVERED = "Céu coberto";
  private static final String NO_SUN_HERE = "Sem sol nesta dimensão";
  private static final String NO_ROOM = "Sem espaço para o painel";
  private static final String DEPLOY_SOUND = "Painel do Dock monta";
  private static final String LOCK_SOUND = "Painel do Dock trava";
  private static final String RETRACT_SOUND = "Painel do Dock guarda";
  private static final String PANEL = "Painel";
  private static final String STORED = "Guardado";
  private static final String DEPLOYING = "Montando";
  private static final String DEPLOYED = "Montado";
  private static final String RETRACTING = "Guardando";
  private static final String UNREACHABLE = "%s: não consigo chegar ao Dock";

  private PortugueseSolarDockTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RobotDockReach.UNREACHABLE_KEY, UNREACHABLE);
    add.accept(ScreenKeys.DOCK_SOLAR, SOLAR);
    add.accept(ScreenKeys.DOCK_NO_SUN, NO_SUN);
    add.accept(ScreenKeys.DOCK_TAB_SOLAR, TAB);
    add.accept(ScreenKeys.DOCK_SOLAR_STATUS, STATUS);
    add.accept(ScreenKeys.DOCK_SOLAR_REASON, REASON);
    add.accept(ScreenKeys.DOCK_SOLAR_NOW, NOW);
    add.accept(ScreenKeys.DOCK_SOLAR_MAX, MAX);
    add.accept(ScreenKeys.DOCK_SOLAR_HINT, HINT);
    add.accept(ScreenKeys.DOCK_SOLAR_NIGHT_HINT, NIGHT_HINT);
    add.accept(ScreenKeys.DOCK_MOONLIGHT, MOONLIGHT_RATE);
    addStates(add);
    addPanel(add);
    addSounds(add);
  }

  /** The subtitles of the sounds of the panel (answer 1921). */
  private static void addSounds(BiConsumer<String, String> add) {
    add.accept(
        CubitSoundProvider.dockSubtitleKey(CubitSounds.DOCK_PANEL_DEPLOY_NAME), DEPLOY_SOUND);
    add.accept(CubitSoundProvider.dockSubtitleKey(CubitSounds.DOCK_PANEL_LOCK_NAME), LOCK_SOUND);
    add.accept(
        CubitSoundProvider.dockSubtitleKey(CubitSounds.DOCK_PANEL_RETRACT_NAME), RETRACT_SOUND);
  }

  private static void addStates(BiConsumer<String, String> add) {
    add.accept(ScreenKeys.dockSun(SunState.GENERATING), GENERATING);
    add.accept(ScreenKeys.dockSun(SunState.MOONLIGHT), MOONLIGHT);
    for (SunState state : SunState.values()) {
      if (!state.generates()) {
        add.accept(ScreenKeys.dockSun(state), STOPPED);
      }
    }
    add.accept(ScreenKeys.dockSunReason(SunState.GENERATING), GENERATING);
    add.accept(ScreenKeys.dockSunReason(SunState.MOONLIGHT), MOONLIGHT);
    add.accept(ScreenKeys.dockSunReason(SunState.NIGHT), NIGHT);
    add.accept(ScreenKeys.dockSunReason(SunState.RAIN), RAIN);
    add.accept(ScreenKeys.dockSunReason(SunState.COVERED), COVERED);
    add.accept(ScreenKeys.dockSunReason(SunState.NO_SUN), NO_SUN_HERE);
    add.accept(ScreenKeys.dockSunReason(SunState.NO_ROOM), NO_ROOM);
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
