package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class AreaShapeTest {
  private static final double PRECISION = 1e-9;

  /** Ten by ten without its south east quarter, from 5 and 5 on. */
  private static final AreaShape ELL = shape(0, 0, 9, 0, 9, 4, 4, 4, 4, 9, 0, 9);

  private static final int ELL_COLUMNS = 75;
  private static final AreaShape BACKWARDS = shape(4, 9, 4, 4, 9, 4, 9, 0, 0, 0, 0, 9);
  private static final AreaShape HALFWAY = shape(3, 0, 9, 0, 9, 4, 4, 4, 4, 9, 0, 9, 0, 0);
  private static final AreaShape SQUARE = shape(0, 0, 9, 0, 9, 9, 0, 9);
  private static final AreaShape CUT = shape(5, 5, 9, 5, 9, 9, 5, 9);
  private static final AreaShape CUT_APART = shape(6, 6, 9, 6, 9, 9, 6, 9);
  private static final AreaShape INSIDE_IT = shape(1, 1, 2, 1, 2, 2, 1, 2);
  private static final int[] LINE = {0, 0, 9, 0};
  private static final List<AreaColumn> ON_THE_LINES = columns(0, 0, 9, 4, 4, 9, 6, 4);
  private static final List<AreaColumn> OUT = columns(5, 5, 9, 9, 10, 0, -1, 0);
  private static final AreaColumn INNER = new AreaColumn(4, 4);
  private static final AreaColumn HOLE = new AreaColumn(4, 4);
  private static final List<AreaColumn> AROUND_THE_HOLE = columns(1, 4, 7, 3, 7, 4);
  private static final WorkArea.Place MIDDLE = new WorkArea.Place(5, 5);
  private static final WorkArea.Place NEAREST_TO_THE_MIDDLE = new WorkArea.Place(4.5, 4.5);
  private static final double IN_THE_CUT = 7;
  private static final double CUT_GAP = 2;
  private static final double EAST = 13;
  private static final double EAST_GAP = 3;

  /** The tips of a C almost meet, and the hole between them is outside. */
  private static final AreaShape CEE =
      shape(0, 0, 8, 0, 8, 3, 6, 3, 6, 2, 2, 2, 2, 6, 6, 6, 6, 4, 8, 4, 8, 8, 0, 8);

  @Test
  void theBlocksOfTheLinesAreInside() {
    assertTrue(ON_THE_LINES.stream().allMatch(column -> ELL.has(column.x(), column.z())));
  }

  @Test
  void theQuarterCutOutIsOutside() {
    assertTrue(OUT.stream().noneMatch(column -> ELL.has(column.x(), column.z())));
    assertEquals(ELL_COLUMNS, ELL.columnCount());
  }

  @Test
  void aHoleBetweenLinesSideBySideIsOutside() {
    assertFalse(CEE.has(HOLE.x(), HOLE.z()));
    assertTrue(AROUND_THE_HOLE.stream().allMatch(column -> CEE.has(column.x(), column.z())));
  }

  @Test
  void everyColumnIsInsideOnce() {
    List<AreaColumn> columns = IntStream.range(0, ELL.columnCount()).mapToObj(ELL::column).toList();

    assertEquals(ELL_COLUMNS, columns.stream().distinct().count());
    assertTrue(columns.stream().allMatch(column -> ELL.has(column.x(), column.z())));
  }

  @Test
  void theSameShapeIsTheSameWhateverThePointItStartedFrom() {
    assertEquals(ELL, BACKWARDS);
    assertEquals(ELL.hashCode(), BACKWARDS.hashCode());
    assertEquals(ELL.corners(), BACKWARDS.corners());
    assertNotEquals(ELL, SQUARE);
  }

  @Test
  void aPointHalfwayAlongASideIsNotACorner() {
    assertEquals(ELL, HALFWAY);
    assertEquals(new AreaColumn(0, 0), HALFWAY.corners().getFirst());
  }

  @Test
  void anOpenPathHasNoShape() {
    assertTrue(AreaShape.of(AreaPath.EMPTY).isEmpty());
    assertTrue(AreaShape.of(path(LINE)).isEmpty());
  }

  @Test
  void aShapeTooLargeForTheMemoryIsRefused() {
    int far = AreaShape.MAX_SIDE;

    assertTrue(AreaShape.of(path(0, 0, far, 0, far, 1, 0, 1)).isEmpty());
    assertTrue(AreaShape.of(path(0, 0, 0, far, 1, far, 1, 0)).isEmpty());
    assertTrue(
        AreaShape.of(path(Integer.MIN_VALUE, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 1, 0, 1))
            .isEmpty());
    AreaPath many = AreaPath.EMPTY;
    for (int point = 0; point <= AreaShape.MAX_POINTS; point++) {
      many = many.with(new AreaColumn(point, 0));
    }
    assertTrue(AreaShape.of(many).isEmpty());
  }

  @Test
  void theHeartIsTheMiddleWhenItIsInside() {
    assertEquals(MIDDLE, SQUARE.heart());
  }

  @Test
  void theHeartOfAnEllIsThePlaceInsideNearestToTheMiddle() {
    assertEquals(NEAREST_TO_THE_MIDDLE, ELL.heart());
  }

  @Test
  void theDistanceGoesToTheNearestEdgeAlsoInTheCutQuarter() {
    assertEquals(0, ELL.distance(2, 2), PRECISION);
    assertEquals(CUT_GAP, ELL.distance(IN_THE_CUT, IN_THE_CUT), PRECISION);
    assertEquals(EAST_GAP, ELL.distance(EAST, 2), PRECISION);
  }

  @Test
  void theNearestCornerCountsTheInnerOne() {
    assertEquals(ELL.corners().indexOf(INNER), ELL.nearestCorner(INNER.x() + 1, INNER.z() + 1));
  }

  @Test
  void shapesThatOverlapOrTouchAreOne() {
    assertTrue(ELL.touches(CUT));
    assertTrue(CUT.touches(ELL));
    assertTrue(ELL.touches(INSIDE_IT));
    assertTrue(INSIDE_IT.touches(ELL));
    assertFalse(ELL.touches(CUT_APART));
  }

  private static List<AreaColumn> columns(int... numbers) {
    return path(numbers).points();
  }

  private static AreaShape shape(int... numbers) {
    return AreaShape.of(path(numbers)).orElseThrow();
  }

  private static AreaPath path(int... numbers) {
    AreaPath path = AreaPath.EMPTY;
    for (int index = 0; index < numbers.length; index += 2) {
      path = path.with(new AreaColumn(numbers[index], numbers[index + 1]));
    }
    return path;
  }
}
