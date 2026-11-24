package io.github.zenndg.cubit.domain.xp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import org.junit.jupiter.api.Test;

class XpStateTest {
  private static final int FLUID = 6_400;
  private static final int CAPACITY = 8_000;
  private static final float HEALTH = 5;
  private static final XpState XP = new XpState(FLUID, new XpSettings(XpMode.RETURN_TO_DOCK, true));
  private static final RobotState OLD = new RobotState(RobotTier.BASE, new Energy(1), HEALTH);

  @Test
  void aRobotSavedBeforeTheXpHasAnEmptyTank() {
    assertEquals(XpState.EMPTY, OLD.xp());
  }

  /** Recalled, dead or with another charge, it keeps its XP (answer 1838). */
  @Test
  void theXpStaysWithTheRobot() {
    RobotState state = OLD.withXp(XP);
    assertEquals(XP, state.damaged().xp());
    assertEquals(XP, state.withEnergy(Energy.ZERO).xp());
    assertEquals(XP, state.withIntroduced(false).xp());
    assertEquals(XP, state.withTorches(false).xp());
    assertEquals(XP, state.withSpeed(state.speed()).xp());
  }

  @Test
  void theTankTakesTheCapacityOfTheTier() {
    assertEquals(new XpTank(FLUID, CAPACITY), XP.tank(CAPACITY));
    assertThrows(IllegalArgumentException.class, () -> new XpState(-1, XpSettings.INITIAL));
  }

  @Test
  void theSettingsChangeOneAtATime() {
    XpSettings ignoring = XpSettings.INITIAL.withMode(XpMode.IGNORE);
    assertEquals(XpMode.IGNORE, ignoring.mode());
    assertFalse(ignoring.deleteExcess());
    assertTrue(XpSettings.INITIAL.toggledExcess().deleteExcess());
  }
}
