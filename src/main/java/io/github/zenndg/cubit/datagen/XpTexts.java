package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.entity.RobotXpReturn;
import io.github.zenndg.cubit.registry.CubitFluids;
import java.util.function.BiConsumer;

/** The XP, in English, the base language (answers 1816, 1830 and 2070). */
final class XpTexts {
  private static final String FLUID = "Cubit XP Fluid";
  private static final String FULL_NOTICE = "%s: my XP tank is full";

  private XpTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(CubitFluids.XP_FLUID_TYPE.get().getDescriptionId(), FLUID);
    add.accept(RobotXpReturn.FULL_KEY, FULL_NOTICE);
  }
}
