package io.github.zenndg.cubit.domain.desk;

/**
 * What the desk found when asked to start a plan: a tier with the cascade, a plan without a cycle,
 * the whole chain released, the materials, an order of the Project tab and what is left of another
 * plan (answers 2009, 2015 and 2028).
 */
public record CascadeCheck(
    boolean tier,
    boolean plan,
    boolean released,
    boolean materials,
    boolean orderActive,
    boolean stockLeft) {}
