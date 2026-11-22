package io.github.zenndg.cubit.domain.robot;

/**
 * A point of the patrol route: the top of a solid block, where the Cubit stands, and its dimension,
 * as in the marks of the other cards (answer 1579).
 */
public record RoutePoint(String dimension, int x, int y, int z) {
  public boolean in(String other) {
    return dimension.equals(other);
  }
}
