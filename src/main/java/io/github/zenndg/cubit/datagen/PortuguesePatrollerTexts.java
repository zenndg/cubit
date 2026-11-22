package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.entity.RobotAutoReturn;
import io.github.zenndg.cubit.menu.DefenseKeys;
import io.github.zenndg.cubit.menu.TabletKeys;
import io.github.zenndg.cubit.menu.WorkKeys;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import io.github.zenndg.cubit.registry.CubitItems;
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
  private static final String HEADING = "%s está com pouca energia, indo ao Dock";
  private static final String NO_DOCK = "%s está com pouca energia e sem Dock";

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
  }
}
