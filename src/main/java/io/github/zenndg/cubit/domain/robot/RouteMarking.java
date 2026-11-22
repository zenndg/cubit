package io.github.zenndg.cubit.domain.robot;

import java.util.Optional;

/**
 * What a click of the Mark app does to the route of the Galactic Patroller: a marked point comes
 * out, and a new one goes in at the end, only on top of a solid block inside the area and while the
 * tier keeps room. Shift takes out the clicked point, or else the last one (answers 1579, 1599 and
 * 1611).
 */
public final class RouteMarking {
  /** What the click did, for the action bar. */
  public enum Result {
    NO_AREA,
    OUTSIDE_AREA,
    NOT_SOLID,
    ADDED,
    REMOVED,
    FULL
  }

  /** The clicked point, with what the world says about it. */
  public record Click(boolean hasArea, boolean insideArea, boolean solid, RoutePoint point) {}

  /** The route after the click, and what the click did. */
  public record Marked(PatrolRoute route, Result result) {}

  private RouteMarking() {}

  public static Marked click(PatrolRoute route, Click click, int maxPoints) {
    if (route.contains(click.point())) {
      return new Marked(route.remove(click.point()), Result.REMOVED);
    }
    Optional<Result> refused = refusal(click);
    if (refused.isPresent()) {
      return new Marked(route, refused.get());
    }
    if (route.size() >= maxPoints) {
      return new Marked(route, Result.FULL);
    }
    return new Marked(route.add(click.point(), maxPoints), Result.ADDED);
  }

  /** The clicked point when it is marked, or else the last one; nothing on an empty route. */
  public static Optional<PatrolRoute> unmark(PatrolRoute route, Optional<RoutePoint> clicked) {
    if (route.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        clicked.filter(route::contains).map(route::remove).orElse(route.removeLast()));
  }

  private static Optional<Result> refusal(Click click) {
    if (!click.hasArea()) {
      return Optional.of(Result.NO_AREA);
    }
    if (!click.insideArea()) {
      return Optional.of(Result.OUTSIDE_AREA);
    }
    return click.solid() ? Optional.empty() : Optional.of(Result.NOT_SOLID);
  }
}
