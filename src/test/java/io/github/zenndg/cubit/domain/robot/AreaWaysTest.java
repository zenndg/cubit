package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.gametest.AreaBoxes;
import java.util.List;
import org.junit.jupiter.api.Test;

class AreaWaysTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final double PRECISION = 1e-9;
  private static final int[] BOX_POINTS = {0, 0, 19, 0, 19, 19, 0, 19};
  private static final int[] ELL_POINTS = {0, 0, 19, 0, 19, 4, 4, 4, 4, 19, 0, 19};

  /** A U: two legs joined by the north side. */
  private static final int[] YOU_POINTS = {0, 0, 19, 0, 19, 19, 15, 19, 15, 4, 4, 4, 4, 19, 0, 19};

  private static final WorkArea BOX = drawn(BOX_POINTS);
  private static final WorkArea ELL = drawn(ELL_POINTS);
  private static final WorkArea YOU = drawn(YOU_POINTS);
  private static final WorkArea.Place WEST_LEG = new WorkArea.Place(2.5, 18.5);
  private static final WorkArea.Place EAST_LEG = new WorkArea.Place(18.5, 18.5);
  private static final WorkArea.Place NORTH_END = new WorkArea.Place(18.5, 2.5);
  private static final WorkArea.Place INNER_WEST = new WorkArea.Place(4.5, 4.5);
  private static final WorkArea.Place INNER_EAST = new WorkArea.Place(15.5, 4.5);
  private static final WorkArea.Place OUTSIDE = new WorkArea.Place(10.5, 10.5);

  @Test
  void inABoxTheWayIsStraight() {
    assertEquals(EAST_LEG, BOX.next(WEST_LEG, EAST_LEG));
    assertEquals(straight(WEST_LEG, EAST_LEG), BOX.way(WEST_LEG, EAST_LEG), PRECISION);
  }

  @Test
  void inAnEllTheWayTurnsAtTheInnerCorner() {
    double around = straight(WEST_LEG, INNER_WEST) + straight(INNER_WEST, NORTH_END);

    assertEquals(INNER_WEST, ELL.next(WEST_LEG, NORTH_END));
    assertEquals(around, ELL.way(WEST_LEG, NORTH_END), PRECISION);
    assertTrue(around > straight(WEST_LEG, NORTH_END));
  }

  @Test
  void fromTheInnerCornerTheWayGoesOn() {
    assertEquals(NORTH_END, ELL.next(INNER_WEST, NORTH_END));
    assertEquals(WEST_LEG, ELL.next(INNER_WEST, WEST_LEG));
  }

  @Test
  void inAYouTheWayTurnsTwice() {
    double around =
        straight(WEST_LEG, INNER_WEST)
            + straight(INNER_WEST, INNER_EAST)
            + straight(INNER_EAST, EAST_LEG);

    assertEquals(INNER_WEST, YOU.next(WEST_LEG, EAST_LEG));
    assertEquals(INNER_EAST, YOU.next(INNER_WEST, EAST_LEG));
    assertEquals(around, YOU.way(WEST_LEG, EAST_LEG), PRECISION);
  }

  @Test
  void toOrFromOutsideTheWayIsStraight() {
    assertFalse(ELL.holds(WEST_LEG, OUTSIDE));
    assertEquals(OUTSIDE, ELL.next(WEST_LEG, OUTSIDE));
    assertEquals(straight(OUTSIDE, WEST_LEG), ELL.way(OUTSIDE, WEST_LEG), PRECISION);
  }

  @Test
  void aPlaceIsNoWayFromItself() {
    assertEquals(0, ELL.way(WEST_LEG, WEST_LEG), PRECISION);
  }

  private static double straight(WorkArea.Place from, WorkArea.Place to) {
    return from.distance(to.x(), to.z());
  }

  private static WorkArea drawn(int... numbers) {
    AreaPath path = AreaPath.EMPTY;
    for (int index = 0; index < numbers.length; index += 2) {
      path = path.with(new AreaColumn(numbers[index], numbers[index + 1]));
    }
    List<AreaColumn> points = path.points();
    return AreaBoxes.drawn(points, 0, 0, OVERWORLD);
  }
}
