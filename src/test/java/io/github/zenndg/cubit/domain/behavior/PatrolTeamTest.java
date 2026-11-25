package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.gametest.AreaBoxes;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PatrolTeamTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String NETHER = "minecraft:the_nether";
  private static final UUID OWNER = new UUID(0, 1);
  private static final UUID STRANGER = new UUID(0, 2);
  private static final int CALL_RANGE = 32;
  private static final int RESCUE_RADIUS = 8;
  private static final int LOW_HEALTH = 25;
  private static final int NEGATIVE = -1;
  private static final double MAX_HEALTH = 100;
  private static final double WOUNDED = 24;
  private static final double AT_THE_LIMIT = 25;
  private static final double CLOSE = 20;
  private static final double FAR = 100;
  private static final PatrolTeam TEAM = new PatrolTeam(CALL_RANGE, RESCUE_RADIUS, LOW_HEALTH);
  private static final WorkArea WEST =
      AreaBoxes.between(new AreaCorner(0, 64, 0), new AreaCorner(63, 70, 63), OVERWORLD);
  private static final WorkArea EAST =
      AreaBoxes.between(new AreaCorner(64, 64, 0), new AreaCorner(127, 70, 63), OVERWORLD);

  private static PatrolTeam.Member member(UUID owner, String dimension, double x, WorkArea area) {
    return new PatrolTeam.Member(owner, dimension, x, 0, 0, Optional.ofNullable(area));
  }

  @Test
  void theSameOwnerNearbyIsOneTeam() {
    assertTrue(
        TEAM.together(member(OWNER, OVERWORLD, 0, null), member(OWNER, OVERWORLD, CLOSE, null)));
  }

  @Test
  void touchingAreasAreOneTeamAtAnyDistance() {
    assertTrue(
        TEAM.together(member(OWNER, OVERWORLD, 0, WEST), member(OWNER, OVERWORLD, FAR, EAST)));
  }

  @Test
  void anotherOwnerFarAwayOrAnotherDimensionIsNot() {
    assertFalse(
        TEAM.together(member(OWNER, OVERWORLD, 0, null), member(STRANGER, OVERWORLD, CLOSE, null)));
    assertFalse(
        TEAM.together(member(OWNER, OVERWORLD, 0, null), member(OWNER, OVERWORLD, FAR, null)));
    assertFalse(
        TEAM.together(member(OWNER, OVERWORLD, 0, null), member(OWNER, NETHER, CLOSE, null)));
  }

  @Test
  void woundedBelowTheLowHealth() {
    assertTrue(TEAM.wounded(WOUNDED, MAX_HEALTH));
    assertFalse(TEAM.wounded(AT_THE_LIMIT, MAX_HEALTH));
  }

  @Test
  void rejectsInvalidNumbers() {
    assertThrows(
        IllegalArgumentException.class, () -> new PatrolTeam(0, RESCUE_RADIUS, LOW_HEALTH));
    assertThrows(IllegalArgumentException.class, () -> new PatrolTeam(CALL_RANGE, 0, LOW_HEALTH));
    assertThrows(
        IllegalArgumentException.class, () -> new PatrolTeam(CALL_RANGE, RESCUE_RADIUS, NEGATIVE));
  }
}
