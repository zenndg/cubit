package io.github.zenndg.cubit.domain.robot;

import java.util.UUID;

/**
 * A marking of the Area app halfway: the Cubit clicked first, the dimension and the drawing. It
 * lives only in the memory of the server, for one player (answers 1054 and 1060).
 */
public record AreaMarking(UUID robot, String dimension, AreaDrawing drawing) {
  public static AreaMarking of(UUID robot, String dimension) {
    return new AreaMarking(robot, dimension, AreaDrawing.EMPTY);
  }

  public AreaMarking with(AreaDrawing next) {
    return new AreaMarking(robot, dimension, next);
  }
}
