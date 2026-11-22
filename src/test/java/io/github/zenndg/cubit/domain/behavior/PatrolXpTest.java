package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import org.junit.jupiter.api.Test;

class PatrolXpTest {
  private static final int RATE = 50;
  private static final int OWNER_DISTANCE = 24;
  private static final int ZOMBIE_XP = 5;
  private static final long CAPACITY = 3_000_000;
  private static final long HALF = 1_500_000;
  private static final long ALMOST_FULL = CAPACITY - 120;
  private static final int FITS = 2;
  private static final int NEGATIVE = -1;
  private static final PatrolXp XP = new PatrolXp(RATE, OWNER_DISTANCE);
  private static final Battery HALF_BATTERY = new Battery(new Energy(CAPACITY), new Energy(HALF));

  @Test
  void farFromTheOwnerItTakesTheXpAsEnergy() {
    PatrolXp.Absorbed taken = XP.absorb(ZOMBIE_XP, HALF_BATTERY, false);
    assertEquals(ZOMBIE_XP, taken.points());
    assertEquals(HALF + ZOMBIE_XP * RATE, taken.battery().charge().amount());
  }

  @Test
  void withTheOwnerNearTheXpIsLeftForTheOwner() {
    PatrolXp.Absorbed taken = XP.absorb(ZOMBIE_XP, HALF_BATTERY, true);
    assertEquals(0, taken.points());
    assertEquals(HALF, taken.battery().charge().amount());
  }

  @Test
  void onlyTheWholePointsThatFitAreTaken() {
    Battery almostFull = new Battery(new Energy(CAPACITY), new Energy(ALMOST_FULL));
    PatrolXp.Absorbed taken = XP.absorb(ZOMBIE_XP, almostFull, false);
    assertEquals(FITS, taken.points());
    assertEquals(ALMOST_FULL + FITS * RATE, taken.battery().charge().amount());
  }

  @Test
  void aRateOfZeroTurnsItOff() {
    assertEquals(
        0, new PatrolXp(0, OWNER_DISTANCE).absorb(ZOMBIE_XP, HALF_BATTERY, false).points());
  }

  @Test
  void theOwnerCountsAsNearWithinTheDistance() {
    assertTrue(XP.ownerNear(OWNER_DISTANCE));
    assertFalse(XP.ownerNear(OWNER_DISTANCE + 1));
  }

  @Test
  void rejectsANegativeRate() {
    assertThrows(IllegalArgumentException.class, () -> new PatrolXp(NEGATIVE, OWNER_DISTANCE));
  }
}
