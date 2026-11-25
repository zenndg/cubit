package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.tier.RobotTier;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * The color of a part of the Network Server and whether it shines, on its body and on its glow. A
 * part gives its color to the ones inside it, so a light is always a part without others.
 */
record ServerPart(int colour, boolean bright) {
  private static final int WHITE = -1;

  static ServerPart of(GeoBone bone, ServerShown shown, RobotTier tier, boolean glow, int colour) {
    float light = ServerLights.of(bone, shown);
    if (glow) {
      return new ServerPart(ServerLights.grey(light), true);
    }
    if (ServerLights.isLight(bone)) {
      return new ServerPart(ServerLights.colour(light, WHITE), light > 0);
    }
    return new ServerPart(TierBand.colour(bone, tier, colour), false);
  }
}
