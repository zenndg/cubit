package io.github.zenndg.cubit.domain.behavior;

import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.Optional;
import java.util.UUID;

/**
 * The team of the Galactic Patrollers of one owner: who fights together, how far the call goes and
 * when one of them is wounded, so the others come to its rescue (answers 1643, 1644, 1691 and
 * 1707).
 *
 * @param callRange how far the call goes, beyond the areas that touch
 * @param rescueRadius around a wounded Patroller, where a hostile becomes the target of all
 * @param lowHealthPercent the share of the health under which a Patroller is wounded
 */
public record PatrolTeam(int callRange, int rescueRadius, int lowHealthPercent) {
  private static final int PERCENT = 100;
  private static final String INVALID = "Invalid patrol team: ";
  private static final String SEPARATOR = ", ";

  /** A Patroller as the team sees it: whose it is, where it stands and the area it guards. */
  public record Member(
      UUID owner, String dimension, double x, double y, double z, Optional<WorkArea> area) {}

  public PatrolTeam {
    if (callRange < 1 || rescueRadius < 1 || lowHealthPercent < 0 || lowHealthPercent > PERCENT) {
      throw new IllegalArgumentException(
          INVALID + callRange + SEPARATOR + rescueRadius + SEPARATOR + lowHealthPercent);
    }
  }

  /** The same owner, in the same dimension, with areas that touch or within the call. */
  public boolean together(Member one, Member other) {
    if (!one.owner().equals(other.owner()) || !one.dimension().equals(other.dimension())) {
      return false;
    }
    boolean touching =
        one.area().isPresent()
            && other.area().isPresent()
            && one.area().get().touches(other.area().get());
    return touching || distance(one, other) <= callRange;
  }

  public boolean wounded(double health, double maxHealth) {
    return health * PERCENT < maxHealth * lowHealthPercent;
  }

  private static double distance(Member one, Member other) {
    double dx = one.x() - other.x();
    double dy = one.y() - other.y();
    double dz = one.z() - other.z();
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }
}
