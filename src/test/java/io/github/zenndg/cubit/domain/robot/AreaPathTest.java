package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AreaPathTest {
  private static final int[] SQUARE = {0, 0, 5, 0, 5, 5, 0, 5};
  private static final int[] SMALLEST = {0, 0, 1, 0, 1, 1, 0, 1};
  private static final int[] OPEN_END = {0, 0, 5, 0, 5, 5, 2, 5};
  private static final int[] EIGHT = {0, 0, 5, 0, 5, 5, 2, 5, 2, -3, 0, -3};
  private static final int[] BACK_OVER = {0, 0, 5, 0, 3, 0, 3, 5, 0, 5};
  private static final int[] SIDE_BY_SIDE = {0, 0, 5, 0, 5, 1, 0, 1};
  private static final int[] LINE = {0, 0, 5, 0};
  private static final int[] CORNER = {0, 0, 5, 0, 5, 5};
  private static final int[] BACK_ON_THE_FIRST = {0, 0, 5, 0, 5, 5, 3, 5, 3, 0};
  private static final AreaColumn SECOND = new AreaColumn(5, 0);
  private static final AreaColumn FREE = new AreaColumn(0, 5);
  private static final AreaColumn THE_LAST = new AreaColumn(5, 5);
  private static final AreaColumn ON_A_SIDE = new AreaColumn(5, 2);
  private static final AreaColumn PAST_A_SIDE = new AreaColumn(5, -1);
  private static final int[] CLOSES_OVER = {2, 0, 5, 0, 5, 5, 0, 5, 0, 2, 4, 2, 4, 1, 4, 0};

  @Test
  void aSquareCloses() {
    assertTrue(path(SQUARE).closes());
    assertTrue(path(SMALLEST).closes());
  }

  @Test
  void linesSideBySideAreFine() {
    assertTrue(path(SIDE_BY_SIDE).closes());
  }

  @Test
  void theLineThatClosesIsStraightToo() {
    assertFalse(path(OPEN_END).closes());
    assertFalse(path(OPEN_END).straightTo(new AreaColumn(0, 0)));
  }

  @Test
  void aPathThatCrossesItselfDoesNotClose() {
    assertFalse(path(EIGHT).closes());
    assertFalse(path(BACK_OVER).closes());
    assertFalse(path(CLOSES_OVER).closes());
  }

  @Test
  void tooFewPointsDoNotClose() {
    assertFalse(AreaPath.EMPTY.closes());
    assertFalse(path(LINE).closes());
  }

  @Test
  void theNextLineStaysClearOfTheDrawing() {
    AreaPath corner = path(CORNER);

    assertFalse(corner.crossesTo(FREE));
    assertTrue(corner.crossesTo(THE_LAST));
    assertTrue(corner.crossesTo(ON_A_SIDE));
    assertTrue(corner.crossesTo(PAST_A_SIDE));
  }

  @Test
  void theFirstLineOnlyNeedsToLeaveItsPoint() {
    assertFalse(path(0, 0).crossesTo(new AreaColumn(1, 0)));
    assertTrue(path(0, 0).crossesTo(new AreaColumn(0, 0)));
  }

  @Test
  void theLastLineMayJoinABlockNextToTheFirst() {
    assertFalse(path(SMALLEST).crossesToClose());
    assertTrue(path(BACK_ON_THE_FIRST).crossesToClose());
  }

  @Test
  void theSidesJoinThePointsInOrder() {
    assertEquals(SQUARE.length / 2 - 1, path(SQUARE).sides().size());
    assertEquals(SECOND, path(SQUARE).sides().getFirst().to());
  }

  private static AreaPath path(int... numbers) {
    AreaPath path = AreaPath.EMPTY;
    for (int index = 0; index < numbers.length; index += 2) {
      path = path.with(new AreaColumn(numbers[index], numbers[index + 1]));
    }
    return path;
  }
}
