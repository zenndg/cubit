package io.github.zenndg.cubit.domain.robot;

import java.util.List;
import java.util.Optional;

/** The box around the points of an area, from its lowest corner to its highest. */
public record AreaBounds(AreaCorner min, AreaCorner max) {
  private static final double HALF = 0.5;
  private static final String INVERTED_MESSAGE = "The corners of an area are inverted";

  public AreaBounds {
    if (min.x() > max.x() || min.y() > max.y() || min.z() > max.z()) {
      throw new IllegalArgumentException(INVERTED_MESSAGE);
    }
  }

  /** Around any points, or nothing without them. */
  public static Optional<AreaBounds> around(List<AreaCorner> points) {
    Optional<AreaCorner> min = points.stream().reduce(AreaBounds::lower);
    Optional<AreaCorner> max = points.stream().reduce(AreaBounds::higher);
    return min.map(low -> new AreaBounds(low, max.orElseThrow()));
  }

  public int width() {
    return max.x() - min.x() + 1;
  }

  public int height() {
    return max.y() - min.y() + 1;
  }

  public int depth() {
    return max.z() - min.z() + 1;
  }

  public WorkArea.Place center() {
    return new WorkArea.Place((min.x() + max.x() + 1) * HALF, (min.z() + max.z() + 1) * HALF);
  }

  private static AreaCorner lower(AreaCorner one, AreaCorner other) {
    return new AreaCorner(
        Math.min(one.x(), other.x()), Math.min(one.y(), other.y()), Math.min(one.z(), other.z()));
  }

  private static AreaCorner higher(AreaCorner one, AreaCorner other) {
    return new AreaCorner(
        Math.max(one.x(), other.x()), Math.max(one.y(), other.y()), Math.max(one.z(), other.z()));
  }
}
