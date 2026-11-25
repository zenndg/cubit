package io.github.zenndg.cubit.client;

import software.bernie.geckolib.cache.object.GeoBone;

/** The lights of the Solar Relay, lit only while it has its server (answers 2268 and 2374). */
final class RelayLights {
  private static final String LINK = "link_";
  private static final int WHITE = -1;

  private RelayLights() {}

  static boolean isLight(GeoBone bone) {
    return bone.getName().startsWith(LINK);
  }

  static int colour(boolean lit) {
    return ServerLights.colour(lit ? 1f : 0f, WHITE);
  }
}
