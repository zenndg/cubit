package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.menu.WorkKeys;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.function.BiConsumer;

/** The Galactic Patroller, in Portuguese, with the same keys as English (answers 1550 to 1592). */
final class PortuguesePatrollerTexts {
  private static final String JOB = "Patrulheiro Galático";
  private static final String LASER_DEATH = "%1$s foi atingido pelo globo de laser de %2$s";
  private static final String BLADE_DEATH = "%1$s foi golpeado pela Plasma Blade de %2$s";
  private static final String CARD = "Aprendizado de Máquina: Patrulheiro Galático";

  private PortuguesePatrollerTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(WorkKeys.job(RobotJob.PATROLLER), JOB);
    add.accept(CubitItems.PATROLLER_CARD.get().getDescriptionId(), CARD);
    add.accept(DefenseTexts.DEATH_PREFIX + CubitDamageTypes.LASER_ORB_MESSAGE_ID, LASER_DEATH);
    add.accept(DefenseTexts.DEATH_PREFIX + CubitDamageTypes.PLASMA_BLADE_MESSAGE_ID, BLADE_DEATH);
  }
}
