package io.github.zenndg.cubit.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The hostile each Galactic Patroller keeps, so the others choose another (answer 1568). It lives
 * only in memory, on the server, and a Patroller that stops fighting gives its target back.
 */
final class PatrolReservations {
  private static final Map<UUID, UUID> BY_TARGET = new HashMap<>();

  private PatrolReservations() {}

  static boolean reservedByOther(UUID target, UUID patroller) {
    UUID holder = BY_TARGET.get(target);
    return holder != null && !holder.equals(patroller);
  }

  static void reserve(UUID target, UUID patroller) {
    release(patroller);
    BY_TARGET.put(target, patroller);
  }

  static void release(UUID patroller) {
    BY_TARGET.values().removeIf(patroller::equals);
  }
}
