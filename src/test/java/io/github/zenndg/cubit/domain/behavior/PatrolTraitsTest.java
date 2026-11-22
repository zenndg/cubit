package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PatrolTraitsTest {
  /** What a mob is, before the tags and what it does. */
  private enum Kind {
    PLAYER(true, false, false, false, false, false),
    PEACEFUL(false, false, false, false, false, false),
    ZOMBIE(false, false, false, true, true, false),
    MONSTER_ONLY(false, false, false, false, true, false),
    ENDERMAN(false, false, false, false, true, true),
    SHULKER(false, false, false, true, true, false),
    COW(false, true, false, false, false, false),
    HOGLIN(false, true, false, true, true, false),
    BEE(false, true, false, false, false, true),
    IRON_GOLEM(false, false, true, false, false, true);

    private final boolean shielded;
    private final boolean animal;
    private final boolean spared;
    private final boolean enemy;
    private final boolean monster;
    private final boolean neutral;

    Kind(
        boolean shielded,
        boolean animal,
        boolean spared,
        boolean enemy,
        boolean monster,
        boolean neutral) {
      this.shielded = shielded;
      this.animal = animal;
      this.spared = spared;
      this.enemy = enemy;
      this.monster = monster;
      this.neutral = neutral;
    }

    PatrolTraits with(boolean listed, boolean ignored, boolean hurting, boolean angry) {
      return new PatrolTraits(
          shielded, animal, spared, enemy, monster, neutral, listed, ignored, hurting, angry);
    }

    PatrolTraits calm() {
      return with(false, false, false, false);
    }

    PatrolTraits hurting() {
      return with(false, false, true, false);
    }

    PatrolTraits angry() {
      return with(false, false, false, true);
    }

    PatrolTraits listed() {
      return with(true, false, false, false);
    }

    PatrolTraits ignored() {
      return with(false, true, false, false);
    }
  }

  @Test
  void attacksEveryEnemyMonsterAndNeutral() {
    assertTrue(Kind.ZOMBIE.calm().attacked());
    assertTrue(Kind.MONSTER_ONLY.calm().attacked());
    assertTrue(Kind.ENDERMAN.calm().attacked());
    assertTrue(Kind.SHULKER.calm().attacked());
  }

  @Test
  void neverAttacksTheProtectedOrTheSpared() {
    assertFalse(Kind.PLAYER.hurting().attacked());
    assertFalse(Kind.COW.calm().attacked());
    assertFalse(Kind.IRON_GOLEM.calm().attacked());
    assertFalse(Kind.IRON_GOLEM.hurting().attacked());
    assertFalse(Kind.PEACEFUL.calm().attacked());
  }

  @Test
  void anAnimalThatIsAnEnemyIsAttacked() {
    assertTrue(Kind.HOGLIN.calm().attacked());
    assertFalse(Kind.HOGLIN.calm().guarded());
  }

  @Test
  void aNeutralAnimalIsGuardedUntilItTurns() {
    assertFalse(Kind.BEE.calm().attacked());
    assertTrue(Kind.BEE.calm().guarded());
    assertTrue(Kind.BEE.hurting().attacked());
    assertFalse(Kind.BEE.hurting().guarded());
    assertTrue(Kind.BEE.angry().attacked());
  }

  @Test
  void aPeacefulMobHurtingSomeoneIsAttacked() {
    assertTrue(Kind.PEACEFUL.hurting().attacked());
  }

  @Test
  void theIgnoreTagWinsOverEverything() {
    assertFalse(Kind.ZOMBIE.ignored().attacked());
    assertFalse(Kind.ZOMBIE.with(true, true, true, true).attacked());
  }

  @Test
  void theTargetTagAddsAMobButNeverAProtectedOne() {
    assertTrue(Kind.PEACEFUL.listed().attacked());
    assertTrue(Kind.IRON_GOLEM.listed().attacked());
    assertFalse(Kind.PLAYER.listed().attacked());
    assertFalse(Kind.COW.listed().attacked());
  }

  @Test
  void guardsThePlayersRobotsVillagersAndCalmAnimals() {
    assertTrue(Kind.PLAYER.calm().guarded());
    assertTrue(Kind.COW.calm().guarded());
    assertFalse(Kind.ZOMBIE.calm().guarded());
    assertFalse(Kind.IRON_GOLEM.calm().guarded());
  }
}
