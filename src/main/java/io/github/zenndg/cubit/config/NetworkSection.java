package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.robot.AreaDrawingSpec;
import io.github.zenndg.cubit.domain.robot.AreaShape;
import net.neoforged.neoforge.common.ModConfigSpec;

/** The config keys of the Network. The names are a contract (answer 2334). */
final class NetworkSection {
  private static final String SECTION = "network";
  private static final String MIN_POINTS = "area_min_points";
  private static final int MIN_POINTS_DEFAULT = 4;
  private static final int MIN_POINTS_MAX = 32;
  private static final String MAX_POINTS = "area_max_points";
  private static final int MAX_POINTS_DEFAULT = 32;
  private static final String REACH = "area_reach_blocks";
  private static final int REACH_DEFAULT = 32;
  private static final int REACH_MAX = 128;

  private final ModConfigSpec.IntValue minPoints;
  private final ModConfigSpec.IntValue maxPoints;
  private final ModConfigSpec.IntValue reach;

  /** In a section of its own, out of the general one. */
  static NetworkSection in(ModConfigSpec.Builder builder) {
    builder.push(SECTION);
    NetworkSection section = new NetworkSection(builder);
    builder.pop();
    return section;
  }

  private NetworkSection(ModConfigSpec.Builder builder) {
    minPoints =
        builder.defineInRange(
            MIN_POINTS, MIN_POINTS_DEFAULT, AreaDrawingSpec.FEWEST_POINTS, MIN_POINTS_MAX);
    maxPoints =
        builder.defineInRange(
            MAX_POINTS, MAX_POINTS_DEFAULT, AreaDrawingSpec.FEWEST_POINTS, AreaShape.MAX_POINTS);
    reach = builder.defineInRange(REACH, REACH_DEFAULT, 1, REACH_MAX);
  }

  /** With the limits reversed, the fewest points win, so the area can still be closed (rule 17). */
  AreaDrawingSpec drawing() {
    return new AreaDrawingSpec(
        minPoints.get(), Math.max(minPoints.get(), maxPoints.get()), reach.get());
  }
}
