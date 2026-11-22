package io.github.zenndg.cubit.domain.behavior;

/**
 * Which point of the route the Cubit walks to, in a loop (answer 1561). A route that shrank sends
 * it back to the first point. The stops live in PatrolStops.
 */
public record PatrolProgress(int index) {
  public static final PatrolProgress START = new PatrolProgress(0);

  public PatrolProgress {
    index = Math.max(0, index);
  }

  /** The next point of the loop. */
  public PatrolProgress next(int routeSize) {
    return new PatrolProgress(routeSize == 0 ? 0 : (index + 1) % routeSize);
  }

  public PatrolProgress within(int routeSize) {
    return index < routeSize ? this : START;
  }
}
