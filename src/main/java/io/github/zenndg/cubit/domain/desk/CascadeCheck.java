package io.github.zenndg.cubit.domain.desk;

/**
 * What the desk found when asked to start a plan: a tier with the cascade, an amount, a plan
 * without a cycle, the materials, an order of the Project tab and what is left of another plan
 * (answers 2015, 2028 and 2400).
 */
public record CascadeCheck(
    boolean tier,
    boolean amount,
    boolean plan,
    boolean materials,
    boolean orderActive,
    boolean stockLeft) {}
