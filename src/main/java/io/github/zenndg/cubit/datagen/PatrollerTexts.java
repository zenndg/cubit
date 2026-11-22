package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.menu.WorkKeys;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.function.BiConsumer;

/** The Galactic Patroller, in English, the base language (answers 1550 and 1569). */
final class PatrollerTexts {
  private static final String JOB = "Galactic Patroller";
  private static final String CARD = "Machine Learning: Galactic Patroller";

  private PatrollerTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(WorkKeys.job(RobotJob.PATROLLER), JOB);
    add.accept(CubitItems.PATROLLER_CARD.get().getDescriptionId(), CARD);
  }
}
