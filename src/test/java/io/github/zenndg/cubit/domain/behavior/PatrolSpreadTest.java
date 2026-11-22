package io.github.zenndg.cubit.domain.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PatrolSpreadTest {
  private static final int POINTS = 8;
  private static final int CORNERS = 4;
  private static final int ONE_POINT = 1;
  private static final long EARLY = 100;
  private static final long LATE = 200;
  private static final int SELF = 7;
  private static final int MATE = 3;
  private static final int OTHER_MATE = 9;
  private static final int SECOND = 2;
  private static final int SIXTH = 6;
  private static final int OPPOSITE = 4;
  private static final int ACROSS_CORNER = 3;

  @Test
  void aloneOnItsPointItKeepsIt() {
    PatrolSpread.Walker self = new PatrolSpread.Walker(SECOND, LATE, SELF);
    List<PatrolSpread.Walker> mates = List.of(new PatrolSpread.Walker(SIXTH, EARLY, MATE));
    assertEquals(SECOND, PatrolSpread.place(POINTS, self, mates));
  }

  @Test
  void theFirstThereKeepsThePoint() {
    PatrolSpread.Walker self = new PatrolSpread.Walker(0, EARLY, SELF);
    List<PatrolSpread.Walker> mates = List.of(new PatrolSpread.Walker(0, LATE, MATE));
    assertEquals(0, PatrolSpread.place(POINTS, self, mates));
  }

  @Test
  void theLaterOneGoesToTheFarthestPointOfTheLoop() {
    PatrolSpread.Walker self = new PatrolSpread.Walker(0, LATE, SELF);
    List<PatrolSpread.Walker> mates = List.of(new PatrolSpread.Walker(0, EARLY, MATE));
    assertEquals(OPPOSITE, PatrolSpread.place(POINTS, self, mates));
  }

  @Test
  void theFarthestFromEveryMate() {
    PatrolSpread.Walker self = new PatrolSpread.Walker(0, LATE, SELF);
    List<PatrolSpread.Walker> mates =
        List.of(
            new PatrolSpread.Walker(0, EARLY, MATE),
            new PatrolSpread.Walker(OPPOSITE, EARLY, OTHER_MATE));
    assertEquals(SECOND, PatrolSpread.place(POINTS, self, mates));
  }

  @Test
  void aTieGoesByTheIdSoOneOfThemMoves() {
    PatrolSpread.Walker lower = new PatrolSpread.Walker(1, EARLY, MATE);
    PatrolSpread.Walker higher = new PatrolSpread.Walker(1, EARLY, SELF);
    assertEquals(1, PatrolSpread.place(CORNERS, lower, List.of(higher)));
    assertEquals(ACROSS_CORNER, PatrolSpread.place(CORNERS, higher, List.of(lower)));
  }

  @Test
  void aSinglePointCannotSpread() {
    PatrolSpread.Walker self = new PatrolSpread.Walker(0, LATE, SELF);
    assertEquals(
        0, PatrolSpread.place(ONE_POINT, self, List.of(new PatrolSpread.Walker(0, EARLY, MATE))));
  }
}
