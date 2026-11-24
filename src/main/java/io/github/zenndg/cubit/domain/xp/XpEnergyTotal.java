package io.github.zenndg.cubit.domain.xp;

import io.github.zenndg.cubit.domain.energy.Energy;

/** The energy the XP of a Dock made since it exists, only to read (answer 2109). */
public record XpEnergyTotal(Energy made) {
  public static final XpEnergyTotal ZERO = new XpEnergyTotal(Energy.ZERO);

  /** It stops at the largest number, so it never turns over. */
  public XpEnergyTotal plus(Energy more) {
    Energy room = new Energy(Long.MAX_VALUE - made.amount());
    return new XpEnergyTotal(made.plus(more.min(room)));
  }
}
