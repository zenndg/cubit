package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.entity.RobotAutoReturn;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.menu.DefenseKeys;
import io.github.zenndg.cubit.menu.TabletKeys;
import io.github.zenndg.cubit.menu.WorkKeys;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import io.github.zenndg.cubit.registry.CubitItems;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.function.BiConsumer;

/**
 * The Galactic Patroller, in Portuguese, with the same keys as English (answers 1550 to 1592, 1610
 * and 1611).
 */
final class PortuguesePatrollerTexts {
  private static final String JOB = "Patrulheiro Galático";
  private static final String LASER_DEATH = "%1$s foi atingido pelo globo de laser de %2$s";
  private static final String BLADE_DEATH = "%1$s foi golpeado pela Plasma Blade de %2$s";
  private static final String CARD = "Aprendizado de Máquina: Patrulheiro Galático";

  private static final String LOCKED = "Travado pelo card do Patrulheiro Galático";
  private static final String POINT = "Ponto %s de %s marcado para %s";
  private static final String NO_AREA = "Marque a área primeiro com o app Área";
  private static final String OUTSIDE = "Só dentro da área de %s";
  private static final String NOT_SOLID = "Marque o topo de um bloco sólido";
  private static final String ESCORT_APP = "Escolta";
  private static final String ESCORTING = "%s está escoltando %s";
  private static final String ESCORT_STOPPED = "%s parou a escolta";
  private static final String NOT_ESCORTABLE = "%s ataca este mob, então não pode escoltá-lo";
  private static final String OTHER_DIMENSION = "%s só escolta na própria dimensão";
  private static final String ESCORTING_YOU = "%s está escoltando você";
  private static final String STOPPED_YOU = "%s parou de escoltar você";
  private static final String HEADING = "%s está com pouca energia, indo ao %s";
  private static final String NO_DOCK = "%s está com pouca energia e sem Dock";

  private static final String PISTOL_SHOT_SUBTITLE = "Cubit dispara o laser";
  private static final String ORB_BURST_SUBTITLE = "Globo de laser estoura";
  private static final String BLADE_IGNITE_SUBTITLE = "Plasma Blade acende";
  private static final String BLADE_SWING_SUBTITLE = "Plasma Blade golpeia";
  private static final String BLADE_HIT_SUBTITLE = "Plasma Blade acerta";
  private static final String BLADE_OFF_SUBTITLE = "Plasma Blade apaga";
  private static final String WINGS_OPEN_SUBTITLE = "Cubit abre as asas";
  private static final String WINGS_CLOSE_SUBTITLE = "Cubit fecha as asas";
  private static final String JUMP_SUBTITLE = "Cubit dá um impulso";
  private static final String VISOR_DOWN_SUBTITLE = "Cubit trava a mira";
  private static final String VISOR_UP_SUBTITLE = "Cubit ergue o visor";

  private PortuguesePatrollerTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(WorkKeys.job(RobotJob.PATROLLER), JOB);
    add.accept(CubitItems.PATROLLER_CARD.get().getDescriptionId(), CARD);
    add.accept(DefenseTexts.DEATH_PREFIX + CubitDamageTypes.LASER_ORB_MESSAGE_ID, LASER_DEATH);
    add.accept(DefenseTexts.DEATH_PREFIX + CubitDamageTypes.PLASMA_BLADE_MESSAGE_ID, BLADE_DEATH);
    add.accept(DefenseKeys.LOCKED_BY_CARD, LOCKED);
    add.accept(TabletKeys.ROUTE_POINT, POINT);
    add.accept(TabletKeys.ROUTE_NO_AREA, NO_AREA);
    add.accept(TabletKeys.ROUTE_OUTSIDE_AREA, OUTSIDE);
    add.accept(TabletKeys.ROUTE_NOT_SOLID, NOT_SOLID);
    add.accept(RobotAutoReturn.HEADING_KEY, HEADING);
    add.accept(RobotAutoReturn.NO_DOCK_KEY, NO_DOCK);
    add.accept(TabletItem.appKey(TabletApp.ESCORT), ESCORT_APP);
    add.accept(TabletKeys.ESCORTING, ESCORTING);
    add.accept(TabletKeys.ESCORT_STOPPED, ESCORT_STOPPED);
    add.accept(TabletKeys.NOT_ESCORTABLE, NOT_ESCORTABLE);
    add.accept(TabletKeys.ESCORT_OTHER_DIMENSION, OTHER_DIMENSION);
    add.accept(TabletKeys.ESCORTING_YOU, ESCORTING_YOU);
    add.accept(TabletKeys.STOPPED_ESCORTING_YOU, STOPPED_YOU);
    addSubtitles(add);
  }

  /** The subtitles of the sounds of the Patroller (answer 1791). */
  private static void addSubtitles(BiConsumer<String, String> add) {
    subtitle(add, CubitSounds.PATROL_PISTOL_SHOT_NAME, PISTOL_SHOT_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_ORB_BURST_NAME, ORB_BURST_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_BLADE_IGNITE_NAME, BLADE_IGNITE_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_BLADE_SWING_NAME, BLADE_SWING_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_BLADE_HIT_NAME, BLADE_HIT_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_BLADE_OFF_NAME, BLADE_OFF_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_WINGS_OPEN_NAME, WINGS_OPEN_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_WINGS_CLOSE_NAME, WINGS_CLOSE_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_JUMP_NAME, JUMP_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_VISOR_DOWN_NAME, VISOR_DOWN_SUBTITLE);
    subtitle(add, CubitSounds.PATROL_VISOR_UP_NAME, VISOR_UP_SUBTITLE);
  }

  private static void subtitle(BiConsumer<String, String> add, String name, String text) {
    add.accept(CubitSoundProvider.patrolSubtitleKey(name), text);
  }
}
