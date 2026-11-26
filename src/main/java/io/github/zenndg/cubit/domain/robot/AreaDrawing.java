package io.github.zenndg.cubit.domain.robot;

import io.github.zenndg.cubit.domain.tier.AreaSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The drawing of an area, point by point, in right angles (answers 2240 and 2243 to 2246). A point
 * that cannot be in it is refused at the click, and the drawing goes on (answer 2356).
 *
 * @param points the marked blocks, in order
 * @param closed whether the last point joins the first, so the area can be applied
 */
public record AreaDrawing(List<AreaCorner> points, boolean closed) {
  public static final AreaDrawing EMPTY = new AreaDrawing(List.of(), false);

  private static final String NOT_CLOSED_MESSAGE = "A drawing that does not close cannot be closed";

  /** What a click did, or why it did nothing. */
  public enum Step {
    ADDED,
    CLOSED,
    REOPENED,
    ALREADY_CLOSED,
    TOO_FEW,
    FULL,
    NOT_STRAIGHT,
    CROSSES,
    TOO_BIG,
    TOO_HIGH,
    OUTSIDE
  }

  /** The drawing after a click, and what the click did. */
  public record Click(AreaDrawing drawing, Step step) {}

  public AreaDrawing {
    points = List.copyOf(points);
    if (closed && !pathOf(points).closes()) {
      throw new IllegalArgumentException(NOT_CLOSED_MESSAGE);
    }
  }

  /** A block clicked anywhere, as the client foresees the next point without the antennas. */
  public Click click(AreaCorner at, AreaDrawingSpec spec, AreaSpec tier) {
    return click(at, spec, tier, AreaChunks.ANYWHERE);
  }

  /**
   * A block clicked, with the numbers of the drawing, the limits of the tier and the chunks of the
   * antennas of the Cubit, which hold the points, the lines and the inside (answers 2586 and 2601).
   */
  public Click click(AreaCorner at, AreaDrawingSpec spec, AreaSpec tier, AreaChunks reach) {
    if (closed) {
      boolean marked = points.stream().anyMatch(point -> point.column().equals(at.column()));
      return marked
          ? new Click(new AreaDrawing(points, false), Step.REOPENED)
          : new Click(this, Step.ALREADY_CLOSED);
    }
    if (!points.isEmpty() && points.getFirst().column().equals(at.column())) {
      return close(spec, reach);
    }
    Step step = whyNot(at, spec, tier, reach);
    return step == Step.ADDED ? new Click(with(at), step) : new Click(this, step);
  }

  /** Takes out the last point of an open drawing (answer 2245). */
  public AreaDrawing undo() {
    return points.isEmpty() ? this : new AreaDrawing(points.subList(0, points.size() - 1), false);
  }

  /** How many points are still missing for the drawing to close. */
  public int missing(AreaDrawingSpec spec) {
    return Math.max(0, spec.minPoints() - points.size());
  }

  /** The box around the points, or nothing without points. */
  public Optional<AreaBounds> bounds() {
    return AreaBounds.around(points);
  }

  /** The area of a closed drawing: its height goes from the lowest point to the highest (2244). */
  public Optional<WorkArea> area(String dimension) {
    if (!closed) {
      return Optional.empty();
    }
    AreaBounds bounds = bounds().orElseThrow();
    return AreaShape.of(path())
        .map(shape -> new WorkArea(shape, bounds.min().y(), bounds.max().y(), dimension));
  }

  private Click close(AreaDrawingSpec spec, AreaChunks reach) {
    if (missing(spec) > 0) {
      return new Click(this, Step.TOO_FEW);
    }
    if (!path().straightTo(points.getFirst().column())) {
      return new Click(this, Step.NOT_STRAIGHT);
    }
    if (path().crossesToClose()) {
      return new Click(this, Step.CROSSES);
    }
    boolean inside = AreaShape.of(path()).filter(reach::holdsShape).isPresent();
    return inside
        ? new Click(new AreaDrawing(points, true), Step.CLOSED)
        : new Click(this, Step.OUTSIDE);
  }

  private Step whyNot(AreaCorner at, AreaDrawingSpec spec, AreaSpec tier, AreaChunks reach) {
    if (points.size() >= spec.maxPoints()) {
      return Step.FULL;
    }
    Step line = lineTo(at.column(), reach);
    if (line != Step.ADDED) {
      return line;
    }
    AreaBounds bounds = with(at).bounds().orElseThrow();
    if (bounds.width() > tier.sizeBlocks() || bounds.depth() > tier.sizeBlocks()) {
      return Step.TOO_BIG;
    }
    return bounds.height() > tier.heightBlocks() ? Step.TOO_HIGH : Step.ADDED;
  }

  /** The line from the last point: straight, clear of the drawing and inside the chunks. */
  private Step lineTo(AreaColumn at, AreaChunks reach) {
    if (points.isEmpty()) {
      return reach.holds(at) ? Step.ADDED : Step.OUTSIDE;
    }
    if (!path().straightTo(at)) {
      return Step.NOT_STRAIGHT;
    }
    if (path().crossesTo(at)) {
      return Step.CROSSES;
    }
    return reach.holdsLine(points.getLast().column(), at) ? Step.ADDED : Step.OUTSIDE;
  }

  private AreaDrawing with(AreaCorner point) {
    List<AreaCorner> next = new ArrayList<>(points);
    next.add(point);
    return new AreaDrawing(next, false);
  }

  private AreaPath path() {
    return pathOf(points);
  }

  private static AreaPath pathOf(List<AreaCorner> points) {
    return new AreaPath(points.stream().map(AreaCorner::column).toList());
  }
}
