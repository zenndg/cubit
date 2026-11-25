package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.domain.robot.AreaColumn;
import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.AreaPath;
import io.github.zenndg.cubit.domain.robot.AreaShape;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.List;

/** Areas for the tests, without drawing them point by point. It has nothing of the game. */
public final class AreaBoxes {
  private AreaBoxes() {}

  /** A box between two corners, in any order. */
  public static WorkArea between(AreaCorner one, AreaCorner other, String dimension) {
    List<AreaColumn> points =
        List.of(
            one.column(),
            new AreaColumn(other.x(), one.z()),
            other.column(),
            new AreaColumn(one.x(), other.z()));
    return drawn(points, Math.min(one.y(), other.y()), Math.max(one.y(), other.y()), dimension);
  }

  public static WorkArea drawn(List<AreaColumn> points, int low, int high, String dimension) {
    AreaShape shape = AreaShape.of(new AreaPath(points)).orElseThrow();
    return new WorkArea(shape, low, high, dimension);
  }
}
