package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.menu.WorkKeys;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.function.BiConsumer;

/** The Galactic Patroller, in Portuguese, with the same keys as English (answers 1550 and 1569). */
final class PortuguesePatrollerTexts {
  private static final String JOB = "Patrulheiro Galático";
  private static final String CARD = "Aprendizado de Máquina: Patrulheiro Galático";

  private PortuguesePatrollerTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(WorkKeys.job(RobotJob.PATROLLER), JOB);
    add.accept(CubitItems.PATROLLER_CARD.get().getDescriptionId(), CARD);
  }
}
