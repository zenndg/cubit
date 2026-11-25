package io.github.zenndg.cubit.domain.behavior;

/**
 * How a called Cubit comes to its owner: close by it walks, as when it follows, and from farther,
 * or without charge to walk, it appears (answer 2462).
 */
public enum CallArrival {
  WALKS,
  APPEARS;

  public static CallArrival of(
      boolean charged, boolean sameDimension, double distanceBlocks, int teleportDistanceBlocks) {
    boolean near = sameDimension && distanceBlocks < teleportDistanceBlocks;
    return charged && near ? WALKS : APPEARS;
  }
}
