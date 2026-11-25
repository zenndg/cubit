package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.gametest.AreaBoxes;
import java.util.List;
import org.junit.jupiter.api.Test;

class AreaOutwardTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final WorkArea ELL =
      AreaBoxes.drawn(
          List.of(
              new AreaColumn(0, 0),
              new AreaColumn(19, 0),
              new AreaColumn(19, 9),
              new AreaColumn(9, 9),
              new AreaColumn(9, 19),
              new AreaColumn(0, 19)),
          0,
          0,
          OVERWORLD);
  private static final WorkArea.Place WEST_EDGE = new WorkArea.Place(0.5, 8);
  private static final WorkArea.Place SOUTH_EDGE = new WorkArea.Place(14, 9.5);
  private static final WorkArea.Place INNER_EAST_EDGE = new WorkArea.Place(9.5, 14);
  private static final WorkArea.Place CORNER = new WorkArea.Place(0.5, 0.5);
  private static final WorkArea.Place FAR_CORNER = new WorkArea.Place(19.5, 9.5);
  private static final WorkArea.Place INNER_CORNER = new WorkArea.Place(9.5, 9.5);
  private static final WorkArea.Place PAST_WEST = new WorkArea.Place(-3, 9);
  private static final WorkArea.Place PAST_EAST = new WorkArea.Place(25, 9);
  private static final WorkArea.Place PAST_SOUTH = new WorkArea.Place(9, 25);
  private static final WorkArea.Place PAST_NORTH = new WorkArea.Place(9, -3);

  @Test
  void acrossTheNearestEdge() {
    assertEquals(new AreaOutward(-1, 0), outwardFrom(WEST_EDGE));
    assertEquals(new AreaOutward(0, 1), outwardFrom(SOUTH_EDGE));
    assertEquals(new AreaOutward(1, 0), outwardFrom(INNER_EAST_EDGE));
  }

  @Test
  void atACornerTheOutsideIsTheDiagonal() {
    assertEquals(new AreaOutward(-1, -1), outwardFrom(CORNER));
    assertEquals(new AreaOutward(1, 1), outwardFrom(FAR_CORNER));
  }

  /** Straight ways stay inside there, and only the diagonal leads to the cut quarter. */
  @Test
  void atTheInnerCornerOfAnEllTheOutsideIsTheDiagonalToo() {
    assertEquals(new AreaOutward(1, 1), outwardFrom(INNER_CORNER));
  }

  @Test
  void alreadyOutsideTheWayGoesOn() {
    assertEquals(new AreaOutward(-1, 0), outwardFrom(PAST_WEST));
    assertEquals(new AreaOutward(1, 0), outwardFrom(PAST_EAST));
    assertEquals(new AreaOutward(0, 1), outwardFrom(PAST_SOUTH));
    assertEquals(new AreaOutward(0, -1), outwardFrom(PAST_NORTH));
  }

  private static AreaOutward outwardFrom(WorkArea.Place place) {
    return ELL.outwardFrom(place.x(), place.z());
  }
}
