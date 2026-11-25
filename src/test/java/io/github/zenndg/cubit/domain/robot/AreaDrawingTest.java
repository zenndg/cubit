package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.tier.AreaSpec;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AreaDrawingTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final int MOST_POINTS = 6;
  private static final int REACH = 32;
  private static final AreaDrawingSpec SPEC =
      new AreaDrawingSpec(AreaDrawingSpec.FEWEST_POINTS, MOST_POINTS, REACH);
  private static final AreaSpec ADVANCED = new AreaSpec(16, 8);
  private static final int Y = 64;
  private static final int HIGH = 71;
  private static final List<AreaCorner> SQUARE =
      List.of(at(0, 0), at(9, 0), new AreaCorner(9, HIGH, 9), at(0, 9));
  private static final List<AreaCorner> THREE = SQUARE.subList(0, 3);
  private static final List<AreaCorner> TWO = SQUARE.subList(0, 2);
  private static final List<AreaCorner> CROOKED = List.of(at(0, 0), at(9, 0), at(9, 9), at(5, 9));
  private static final List<AreaCorner> OVER =
      List.of(at(0, 0), at(9, 0), at(9, 9), at(0, 9), at(0, 12));
  private static final List<AreaCorner> BACK =
      List.of(at(3, 0), at(9, 0), at(9, 9), at(12, 9), at(12, 0));
  private static final List<AreaCorner> FULL =
      List.of(at(0, 0), at(9, 0), at(9, 4), at(4, 4), at(4, 9), at(0, 9));
  private static final AreaCorner FIRST = SQUARE.getFirst();
  private static final AreaCorner TOO_HIGH = new AreaCorner(9, HIGH + 1, 5);
  private static final AreaCorner IN_THE_MIDDLE = at(5, 5);
  private static final AreaCorner ON_THE_LINE = at(5, 0);
  private static final AreaCorner THE_LAST = at(9, 0);
  private static final AreaCorner PAST_X = at(16, 0);
  private static final AreaCorner PAST_Z = at(9, 16);
  private static final AreaCorner AT_THE_LIMIT = at(15, 0);
  private static final AreaCorner ONE_MORE = at(0, 5);
  private static final AreaCorner A_POINT = at(9, 9);

  @Test
  void pointsInRightAnglesGoIn() {
    AreaDrawing drawing = drawn(SQUARE);

    assertEquals(SQUARE, drawing.points());
    assertFalse(drawing.closed());
    assertTrue(drawing.area(OVERWORLD).isEmpty());
  }

  @Test
  void aPointOutOfTheRightAngleIsRefusedAndTheDrawingStays() {
    AreaDrawing drawing = drawn(TWO);
    AreaDrawing.Click click = click(drawing, IN_THE_MIDDLE);

    assertEquals(AreaDrawing.Step.NOT_STRAIGHT, click.step());
    assertEquals(drawing, click.drawing());
  }

  @Test
  void aLineOverTheDrawingIsRefused() {
    AreaDrawing drawing = drawn(TWO);

    assertEquals(AreaDrawing.Step.CROSSES, click(drawing, ON_THE_LINE).step());
    assertEquals(AreaDrawing.Step.CROSSES, click(drawing, THE_LAST).step());
  }

  @Test
  void aPointPastTheLimitsOfTheTierIsRefused() {
    AreaDrawing drawing = drawn(TWO);

    assertEquals(AreaDrawing.Step.TOO_BIG, click(drawing, PAST_X).step());
    assertEquals(AreaDrawing.Step.TOO_BIG, click(drawing, PAST_Z).step());
    assertEquals(AreaDrawing.Step.ADDED, click(drawing, AT_THE_LIMIT).step());
    assertEquals(AreaDrawing.Step.TOO_HIGH, click(drawing, TOO_HIGH).step());
  }

  @Test
  void theFirstPointClosesIt() {
    AreaDrawing.Click click = click(drawn(SQUARE), FIRST);

    assertEquals(AreaDrawing.Step.CLOSED, click.step());
    assertTrue(click.drawing().closed());
  }

  @Test
  void itClosesOnlyWithEnoughPoints() {
    AreaDrawing three = drawn(THREE);

    assertEquals(AreaDrawing.Step.TOO_FEW, click(three, FIRST).step());
    assertEquals(1, three.missing(SPEC));
    assertEquals(0, drawn(SQUARE).missing(SPEC));
  }

  @Test
  void theLineThatClosesIsStraightAndClearToo() {
    AreaDrawing crooked = drawn(CROOKED);
    AreaDrawing over = drawn(OVER);
    AreaDrawing back = drawn(BACK);

    assertEquals(AreaDrawing.Step.NOT_STRAIGHT, click(crooked, FIRST).step());
    assertEquals(AreaDrawing.Step.CROSSES, click(over, FIRST).step());
    assertEquals(AreaDrawing.Step.CROSSES, click(back, BACK.getFirst()).step());
  }

  @Test
  void pastTheMostPointsItOnlyCloses() {
    AreaDrawing full = drawn(FULL);

    assertEquals(AreaDrawing.Step.FULL, click(full, ONE_MORE).step());
    assertEquals(AreaDrawing.Step.CLOSED, click(full, FIRST).step());
  }

  @Test
  void aMarkedPointReopensAClosedOneWithAllItsPoints() {
    AreaDrawing closed = click(drawn(SQUARE), FIRST).drawing();
    AreaDrawing.Click click = click(closed, A_POINT);

    assertEquals(AreaDrawing.Step.REOPENED, click.step());
    assertEquals(drawn(SQUARE), click.drawing());
    assertEquals(AreaDrawing.Step.ALREADY_CLOSED, click(closed, IN_THE_MIDDLE).step());
  }

  @Test
  void undoTakesOutTheLastPoint() {
    assertEquals(drawn(THREE), drawn(SQUARE).undo());
    assertEquals(AreaDrawing.EMPTY, AreaDrawing.EMPTY.undo());
  }

  @Test
  void theHeightGoesFromTheLowestPointToTheHighest() {
    WorkArea area = click(drawn(SQUARE), FIRST).drawing().area(OVERWORLD).orElseThrow();

    assertEquals(Y, area.low());
    assertEquals(HIGH, area.high());
    assertTrue(area.fits(ADVANCED));
  }

  @Test
  void anOpenDrawingIsNeverMadeClosed() {
    assertThrows(IllegalArgumentException.class, () -> new AreaDrawing(THREE, true));
  }

  @Test
  void theNumbersOfTheDrawingAreChecked() {
    int few = AreaDrawingSpec.FEWEST_POINTS;

    assertThrows(
        IllegalArgumentException.class, () -> new AreaDrawingSpec(few - 1, MOST_POINTS, REACH));
    assertThrows(IllegalArgumentException.class, () -> new AreaDrawingSpec(few, few - 1, REACH));
    assertThrows(IllegalArgumentException.class, () -> new AreaDrawingSpec(few, MOST_POINTS, 0));
  }

  @Test
  void aMarkingKeepsTheCubitAndItsDrawing() {
    UUID robot = UUID.randomUUID();
    AreaMarking marking = AreaMarking.of(robot, OVERWORLD).with(drawn(SQUARE));

    assertEquals(robot, marking.robot());
    assertEquals(OVERWORLD, marking.dimension());
    assertEquals(drawn(SQUARE), marking.drawing());
  }

  private static AreaDrawing drawn(List<AreaCorner> points) {
    AreaDrawing drawing = AreaDrawing.EMPTY;
    for (AreaCorner point : points) {
      AreaDrawing.Click click = click(drawing, point);
      assertEquals(AreaDrawing.Step.ADDED, click.step());
      drawing = click.drawing();
    }
    return drawing;
  }

  private static AreaDrawing.Click click(AreaDrawing drawing, AreaCorner at) {
    return drawing.click(at, SPEC, ADVANCED);
  }

  private static AreaCorner at(int x, int z) {
    return new AreaCorner(x, Y, z);
  }
}
