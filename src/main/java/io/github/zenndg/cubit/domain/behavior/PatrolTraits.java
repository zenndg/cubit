package io.github.zenndg.cubit.domain.behavior;

/**
 * What the Galactic Patroller knows about a mob, read by the game layer from the signs of the game,
 * with no fixed list, so a mob of any mod counts (answers 1633, 1634 and 1701 to 1705).
 *
 * @param shielded the owner, a player, a robot or a villager, never attacked
 * @param animal an animal of the game, guarded while calm
 * @param spared an iron or snow golem, a tamed mob or a peaceful one of the water
 * @param enemy a hostile of the game, an Enemy
 * @param monster registered in the MONSTER category
 * @param neutral a NeutralMob, attacked even when calm, unless it is an animal
 * @param listed in the tag of the mobs it always attacks
 * @param ignored in the tag of the mobs it never attacks, which wins
 * @param hurting chasing someone it guards
 * @param angryAtOwner angry at its owner
 */
public record PatrolTraits(
    boolean shielded,
    boolean animal,
    boolean spared,
    boolean enemy,
    boolean monster,
    boolean neutral,
    boolean listed,
    boolean ignored,
    boolean hurting,
    boolean angryAtOwner) {

  /** Whether it attacks the mob: the ignore tag wins, and the guarded ones never come in. */
  public boolean attacked() {
    if (shielded || ignored || calmAnimal()) {
      return false;
    }
    return turnedAnimal() || listed || !spared && hostile();
  }

  /** Past calmAnimal, an animal that is not an Enemy is a neutral one that turned. */
  private boolean turnedAnimal() {
    return animal && !enemy;
  }

  /** A hostile of the game, a neutral one, or anyone chasing someone it guards (answer 1705). */
  private boolean hostile() {
    return enemy || monster || neutral || hurting;
  }

  /**
   * Whether it guards the mob: a mob chasing it counts as hurting someone, and the orb and the
   * blade pass through it.
   */
  public boolean guarded() {
    return shielded || calmAnimal();
  }

  /** An animal that is not an Enemy, and calm: not a neutral one that turned (answer 1702). */
  private boolean calmAnimal() {
    return animal && !enemy && !(neutral && (hurting || angryAtOwner));
  }
}
