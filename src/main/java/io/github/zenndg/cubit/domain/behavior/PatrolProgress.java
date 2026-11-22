package io.github.zenndg.cubit.domain.behavior;

/**
 * Where the Cubit is in its round: the point it walks to and the pause it still waits there,
 * looking around (answer 1581). A route that shrank sends it back to the first point.
 */
public record PatrolProgress(int index, int pauseLeft) {
  public static final PatrolProgress START = new PatrolProgress(0, 0);

  public PatrolProgress {
    index = Math.max(0, index);
    pauseLeft = Math.max(0, pauseLeft);
  }

  public boolean pausing() {
    return pauseLeft > 0;
  }

  /** It reached its point and looks around for the pause. */
  public PatrolProgress arrived(int pauseTicks) {
    return new PatrolProgress(index, pauseTicks);
  }

  public PatrolProgress tick() {
    return new PatrolProgress(index, pauseLeft - 1);
  }

  /** The next point of the loop, after the pause. */
  public PatrolProgress next(int routeSize) {
    return new PatrolProgress(routeSize == 0 ? 0 : (index + 1) % routeSize, 0);
  }

  public PatrolProgress within(int routeSize) {
    return index < routeSize ? this : START;
  }
}
