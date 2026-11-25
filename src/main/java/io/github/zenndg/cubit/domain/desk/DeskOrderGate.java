package io.github.zenndg.cubit.domain.desk;

/**
 * What an order of the Engineering Desk needs to leave zero: the materials of one item in the desk,
 * without any progression. Only the start is held, so an order on its way grows freely and pauses
 * when the materials end (answers 1984, 1985 and 2400).
 */
public record DeskOrderGate(boolean materials) {
  public boolean allows(boolean orderActive) {
    return orderActive || materials;
  }
}
