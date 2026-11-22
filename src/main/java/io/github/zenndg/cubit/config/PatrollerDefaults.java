package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.tier.PatrollerSpec;

/**
 * The numbers of the Galactic Patroller on the Elite and the Ultimate (answers 1564, 1577, 1579 and
 * 1600). The knockback is a level: 1 is the medium push and 2 the strong one.
 */
final class PatrollerDefaults {
  static final PatrollerSpec ELITE =
      new PatrollerSpec(
          new PatrollerSpec.Weapons(5, 16, 20, new Energy(1_000), 1, 7, 12, 1, 2.5),
          6,
          16,
          new PatrollerSpec.Jump(3, new Energy(500)),
          16,
          64);
  static final PatrollerSpec ULTIMATE =
      new PatrollerSpec(
          new PatrollerSpec.Weapons(7, 24, 12, new Energy(800), 2, 10, 10, 2, 2.5),
          10,
          24,
          new PatrollerSpec.Jump(5, new Energy(400)),
          32,
          128);

  private PatrollerDefaults() {}
}
