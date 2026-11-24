package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.entity.RobotXpReturn;
import io.github.zenndg.cubit.registry.CubitFluids;
import java.util.function.BiConsumer;

/** The XP, in Portuguese, with the same keys as the English one (answer 1816). */
final class PortugueseXpTexts {
  private static final String FLUID = "Fluido de XP Cubit";
  private static final String FULL_NOTICE = "%s: meu tanque de XP está cheio";

  private PortugueseXpTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(CubitFluids.XP_FLUID_TYPE.get().getDescriptionId(), FLUID);
    add.accept(RobotXpReturn.FULL_KEY, FULL_NOTICE);
  }
}
