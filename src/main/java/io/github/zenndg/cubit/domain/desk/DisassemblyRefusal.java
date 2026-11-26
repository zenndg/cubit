package io.github.zenndg.cubit.domain.desk;

/**
 * Why a desk does not take an item apart, shown on the screen, or none (answers 2524, 2528, 2530
 * and 2531). The first cause found is the one shown.
 */
public enum DisassemblyRefusal {
  NONE,
  LOCKED_TIER,
  EMPTY,
  NO_PARTS,
  TOO_FEW,
  HAS_CONTENTS,
  BUSY;

  public static DisassemblyRefusal decide(Check check) {
    if (!check.unlocked()) {
      return LOCKED_TIER;
    }
    if (check.empty()) {
      return EMPTY;
    }
    if (!check.hasParts()) {
      return NO_PARTS;
    }
    if (!check.enough()) {
      return TOO_FEW;
    }
    if (check.hasContents()) {
      return HAS_CONTENTS;
    }
    return check.busy() ? BUSY : NONE;
  }

  public boolean allows() {
    return this == NONE;
  }

  /**
   * What the desk found: whether its tier has the tab, the slot is empty, the item has a recipe of
   * the desk with parts of the mod, the slot has at least what the recipe makes, the item keeps
   * items, XP, a card or torches, and an order or a plan runs.
   */
  public record Check(
      boolean unlocked,
      boolean empty,
      boolean hasParts,
      boolean enough,
      boolean hasContents,
      boolean busy) {}
}
